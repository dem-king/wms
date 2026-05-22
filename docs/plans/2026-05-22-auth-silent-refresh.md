# Auth Silent Refresh Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复当前双 Token 无感刷新中的关键缺陷，并新增前端预刷新能力，在不改变“记住我仅记住用户名”行为的前提下提升登录态稳定性。

**Architecture:** 后端继续保留现有 `/auth/login`、`/auth/token/refresh`、`/auth/logout` 接口，但改为在刷新时按 `userId` 重新装配用户上下文，并把当前会话对应的 `refreshToken` 纳入可撤销管理。前端在现有 `401 -> refresh -> 重放请求` 兜底机制上增加“基于绝对过期时间的单定时器预刷新”，登录、刷新、登出统一通过鉴权工具层维护 token、过期时间和调度器状态。

**Tech Stack:** Java 17, Spring Boot 3, Spring Security, Redis, Vue 3, TypeScript, Axios, Pinia, Vitest

---

### Task 1: 后端补齐 Refresh Token 会话上下文

**Files:**
- Modify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\TokenServiceImpl.java`
- Modify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\TokenService.java`
- Modify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\constant\AuthRedisKey.java`
- Modify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthServiceImpl.java`
- Test: `d:\Codes\WMS_code\wms-server\wms-auth\src\test\java\com\wms\auth\service\impl\TokenServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void refreshTokenShouldRebuildAccessTokenClaimsFromUserContext() {
    TokenResp issued = tokenService.generateTokenPair(1L, "admin", List.of("ADMIN"));
    TokenResp refreshed = tokenService.refreshToken(issued.getRefreshToken());

    Claims claims = tokenService.parseToken(refreshed.getAccessToken());
    assertEquals("admin", claims.get("username", String.class));
    assertEquals(List.of("ADMIN"), claims.get("roles", List.class));
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=TokenServiceImplTest#refreshTokenShouldRebuildAccessTokenClaimsFromUserContext test`

Expected: FAIL because refreshed token currently rebuilds `accessToken` from a `refreshToken` that does not carry `username` and `roles`.

**Step 3: Write minimal implementation**

```java
// TokenServiceImpl.refreshToken
Long userId = Long.valueOf(claims.getSubject());
SysUserVo user = sysUserService.getById(userId);
List<String> roles = sysUserService.getUserRoles(userId).stream()
        .map(String::valueOf)
        .toList();
return generateTokenPair(userId, user.getUsername(), roles);
```

Also add a Redis session record that stores the current `refreshToken jti`, so refresh-token revocation has a lookup anchor for logout.

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=TokenServiceImplTest#refreshTokenShouldRebuildAccessTokenClaimsFromUserContext test`

Expected: PASS

**Step 5: Commit**

```bash
git add wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/TokenServiceImpl.java wms-server/wms-auth/src/main/java/com/wms/auth/service/TokenService.java wms-server/wms-auth/src/main/java/com/wms/auth/constant/AuthRedisKey.java wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/TokenServiceImplTest.java
git commit -m "fix: rebuild auth claims during token refresh"
```

### Task 2: 后端补齐登出时的 Refresh Token 撤销

**Files:**
- Modify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\TokenServiceImpl.java`
- Modify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthServiceImpl.java`
- Test: `d:\Codes\WMS_code\wms-server\wms-auth\src\test\java\com\wms\auth\service\impl\TokenServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void logoutShouldRevokeRefreshTokenForCurrentSession() {
    TokenResp issued = tokenService.generateTokenPair(1L, "admin", List.of("ADMIN"));
    tokenService.createSession(1L, issued.getAccessToken(), issued.getRefreshToken(), "127.0.0.1", "JUnit");

    authService.logout(issued.getAccessToken());

    BizException ex = assertThrows(BizException.class, () -> tokenService.refreshToken(issued.getRefreshToken()));
    assertEquals(AuthErrorCode.TOKEN_REVOKED.getCode(), ex.getCode());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=TokenServiceImplTest#logoutShouldRevokeRefreshTokenForCurrentSession test`

Expected: FAIL because logout currently revokes only the `accessToken` and deletes the session key.

**Step 3: Write minimal implementation**

```java
// createSession stores access jti + refresh jti
// logout loads refresh jti from session and writes revoke flag with remaining TTL
public void revokeRefreshTokenByUserId(Long userId) { ... }
```

Ensure the revoke entry TTL equals the remaining lifetime of the `refreshToken`, so Redis cleanup remains automatic.

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=TokenServiceImplTest#logoutShouldRevokeRefreshTokenForCurrentSession test`

Expected: PASS

**Step 5: Commit**

```bash
git add wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/TokenServiceImpl.java wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/TokenServiceImplTest.java
git commit -m "fix: revoke refresh token on logout"
```

### Task 3: 前端增加过期时间持久化与预刷新调度

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\utils\auth.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\store\modules\user.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\request.ts`
- Create: `d:\Codes\WMS_code\wms-web\src\utils\auth-refresh.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\store\modules\user.spec.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\utils\auth-refresh.spec.ts`

**Step 1: Write the failing tests**

```ts
it('persists access token expiry after login succeeds', async () => {
  await userStore.login(mockLoginResp)
  expect(getAccessTokenExpiresAt()).toBeGreaterThan(Date.now())
})

it('schedules one proactive refresh before token expiry', async () => {
  setAuthSession({
    accessToken: 'access-token',
    refreshToken: 'refresh-token',
    expiresIn: 7200,
  })
  startRefreshScheduler()
  expect(setTimeout).toHaveBeenCalledTimes(1)
})
```

**Step 2: Run tests to verify they fail**

Run: `npm test -- src/store/modules/user.spec.ts src/utils/auth-refresh.spec.ts`

Expected: FAIL because no expiry persistence or proactive refresh scheduler exists yet.

**Step 3: Write minimal implementation**

```ts
// auth.ts
export function setAccessTokenExpiresAt(expiresAt: number) { ... }
export function getAccessTokenExpiresAt(): number | null { ... }

// auth-refresh.ts
export function startRefreshScheduler() { ... }
export function stopRefreshScheduler() { ... }
export async function refreshTokens() { ... }
```

Implementation rules:
- Derive `expiresAt` from `Date.now() + expiresIn * 1000`
- Use a single timer
- Refresh inside a safe window before expiry
- Rebuild the timer after every successful refresh
- Do not touch the login page “remember me” logic

**Step 4: Run tests to verify they pass**

Run: `npm test -- src/store/modules/user.spec.ts src/utils/auth-refresh.spec.ts`

Expected: PASS

**Step 5: Commit**

```bash
git add wms-web/src/utils/auth.ts wms-web/src/utils/auth-refresh.ts wms-web/src/store/modules/user.ts wms-web/src/store/modules/user.spec.ts wms-web/src/utils/auth-refresh.spec.ts
git commit -m "feat: add proactive auth refresh scheduler"
```

### Task 4: 前端整合 401 兜底刷新与预刷新复用同一链路

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\api\request.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\auth.spec.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\utils\auth-refresh.spec.ts`

**Step 1: Write the failing tests**

```ts
it('reuses the same refresh promise for proactive and 401 fallback refresh', async () => {
  mockRefreshApiOnce()
  await Promise.all([refreshTokens(), retryAfter401(mockRequestConfig)])
  expect(refreshApi).toHaveBeenCalledTimes(1)
})

it('clears auth state and redirects when refresh fails', async () => {
  mockRefreshApiRejectOnce()
  await expect(refreshTokens()).rejects.toThrow()
  expect(clearAuth).toHaveBeenCalled()
  expect(router.push).toHaveBeenCalledWith('/login')
})
```

**Step 2: Run tests to verify they fail**

Run: `npm test -- src/api/system/auth.spec.ts src/utils/auth-refresh.spec.ts`

Expected: FAIL because proactive refresh and interceptor fallback do not yet share one refresh execution path.

**Step 3: Write minimal implementation**

```ts
// request.ts
import { ensureFreshToken, handleAuthRefreshFailure } from '@/utils/auth-refresh'

if (error.response?.status === 401 && !originalRequest._retry) {
  const accessToken = await ensureFreshToken()
  originalRequest.headers.Authorization = `Bearer ${accessToken}`
  return service(originalRequest)
}
```

Keep `isRefreshing`/pending-queue semantics in one place only, so proactive refresh and 401 fallback cannot race each other.

**Step 4: Run tests to verify they pass**

Run: `npm test -- src/api/system/auth.spec.ts src/utils/auth-refresh.spec.ts`

Expected: PASS

**Step 5: Commit**

```bash
git add wms-web/src/api/request.ts wms-web/src/api/system/auth.spec.ts wms-web/src/utils/auth-refresh.spec.ts
git commit -m "fix: unify proactive and fallback token refresh"
```

### Task 5: 全量验证与诊断清理

**Files:**
- Verify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\TokenServiceImpl.java`
- Verify: `d:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthServiceImpl.java`
- Verify: `d:\Codes\WMS_code\wms-web\src\api\request.ts`
- Verify: `d:\Codes\WMS_code\wms-web\src\utils\auth.ts`
- Verify: `d:\Codes\WMS_code\wms-web\src\utils\auth-refresh.ts`
- Verify: `d:\Codes\WMS_code\wms-web\src\store\modules\user.ts`

**Step 1: Run backend focused tests**

Run: `mvn -pl wms-auth -Dtest=TokenServiceImplTest test`

Expected: PASS

**Step 2: Run frontend focused tests**

Run: `npm test -- src/store/modules/user.spec.ts src/api/system/auth.spec.ts src/utils/auth-refresh.spec.ts`

Expected: PASS

**Step 3: Run frontend build**

Run: `npm run build`

Expected: PASS with no TypeScript errors

**Step 4: Run diagnostics**

Use the editor diagnostics tool for the modified files and fix any new TypeScript or lint errors that were introduced.

**Step 5: Commit**

```bash
git add wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/TokenServiceImpl.java wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/TokenServiceImplTest.java wms-web/src/api/request.ts wms-web/src/utils/auth.ts wms-web/src/utils/auth-refresh.ts wms-web/src/utils/auth-refresh.spec.ts wms-web/src/store/modules/user.ts wms-web/src/store/modules/user.spec.ts wms-web/src/api/system/auth.spec.ts
git commit -m "feat: harden silent auth refresh flow"
```
