# Profile Center Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 新增个人中心页面与右上角入口，并通过后端 `/auth/profile` 接口返回当前登录用户基础资料、最近登录时间和最近登录 IP。

**Architecture:** 后端在 `wms-auth` 模块新增“当前登录用户个人中心信息”查询能力，Controller 仅负责鉴权与转发，Service 负责聚合当前用户资料和最近成功登录日志，并返回独立 VO。前端新增隐藏静态路由 `/profile`、个人中心页面和用户下拉入口，页面首屏用 `userStore` 兜底，随后以 `/auth/profile` 实时数据刷新展示。

**Tech Stack:** Spring Boot、MyBatis-Plus、Vue 3、`<script setup lang="ts">`、Pinia、Vue Router、Element Plus、Vitest

---

### Task 1: 先补前端 API 失败测试并定义类型

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\types\auth.d.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\system\auth.ts`
- Create: `D:\Codes\WMS_code\wms-web\src\api\system\auth.spec.ts`

**Step 1: Write the failing test**

- 为 `getProfile()` 编写断言，要求调用 `get('/auth/profile')`。
- 为 `ProfileResp` 中的最近登录字段建立最小类型约束使用场景。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/api/system/auth.spec.ts`
Expected: FAIL，提示 `getProfile` 未定义或请求路径不匹配。

**Step 3: Write minimal implementation**

- 在 `auth.d.ts` 新增个人中心响应类型：
  - `ProfileResp`
  - `LastLoginInfoVO`
- 在 `src/api/system/auth.ts` 新增：

```ts
export function getProfile() {
  return get<ProfileResp>('/auth/profile')
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/api/system/auth.spec.ts`
Expected: PASS

### Task 2: 先补前端页面结构与入口失败测试

**Files:**
- Create: `D:\Codes\WMS_code\wms-web\src\views\profile\index.spec.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\router\index.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\layouts\components\UserDropdown.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`

**Step 1: Write the failing test**

- 断言静态路由中包含 `/profile`，且 `meta.title` 为“个人中心”。
- 断言 `UserDropdown.vue` 存在“个人中心”菜单项和新的事件出口。
- 断言 `Navbar.vue` 监听新的个人中心事件并调用路由跳转。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: FAIL，提示缺少路由、页面引用或入口事件。

**Step 3: Write minimal implementation**

- 在静态路由中新增隐藏页：

```ts
{
  path: 'profile',
  name: 'Profile',
  component: () => import('@/views/profile/index.vue'),
  meta: { title: '个人中心', hidden: true }
}
```

- 在 `UserDropdown.vue` 增加：
  - `emit('profile')`
  - 新菜单项“个人中心”
- 在 `Navbar.vue` 增加 `openProfile()` 并传给 `UserDropdown`。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: PASS

### Task 3: 先补后端个人中心查询失败测试

**Files:**
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\AuthService.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\AuthAuditService.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\domain\vo\AuthProfileVo.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\domain\vo\LastLoginInfoVo.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-auth\src\test\java\com\wms\auth\service\impl\AuthServiceImplProfileTest.java`

**Step 1: Write the failing test**

- 模拟当前用户、用户实体和登录日志，断言 Service 能返回：
  - `userInfo`
  - `lastLoginTime`
  - `lastLoginIp`
- 断言没有成功登录日志时返回空的最近登录信息而不是抛错。

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: FAIL，提示缺少查询方法或 VO。

**Step 3: Write minimal implementation**

- 在 `AuthService` 新增：

```java
AuthProfileVo getCurrentProfile();
```

- 在 `AuthAuditService` 新增：

```java
AuthLoginLog getLatestSuccessLoginLog(Long userId);
```

- 新增个人中心 VO：
  - `AuthProfileVo`
  - `LastLoginInfoVo`

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

### Task 4: 实现后端 `/auth/profile` 接口

**Files:**
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\controller\AuthController.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthServiceImpl.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthAuditServiceImpl.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\mapper\AuthLoginLogMapper.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-system\src\main\java\com\wms\system\service\SysUserService.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-system\src\main\java\com\wms\system\service\impl\SysUserServiceImpl.java`

**Step 1: Write the failing test**

- 让 `AuthServiceImplProfileTest` 依赖真实实现路径。
- 如项目已有 Controller 测试框架，可补一个最小接口测试；若无，则以 Service 测试覆盖主逻辑。

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: FAIL

**Step 3: Write minimal implementation**

- `AuthController` 新增：

```java
@Operation(summary = "获取当前用户个人中心信息")
@PreAuthorize("isAuthenticated()")
@GetMapping("/profile")
public R<AuthProfileVo> getProfile() {
    return R.ok(authService.getCurrentProfile());
}
```

- `SysUserService` 新增按 ID 查询实体方法，供认证模块读取当前用户资料。
- `AuthAuditServiceImpl` 使用 `LambdaQueryWrapper` 查询最近一次成功登录日志。
- `AuthServiceImpl` 基于当前用户 ID 聚合用户资料和最近登录信息。

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

### Task 5: 实现前端个人中心页面

**Files:**
- Create: `D:\Codes\WMS_code\wms-web\src\views\profile\index.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\store\modules\user.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\layouts\components\UserDropdown.vue`

**Step 1: Write the failing test**

- 让 `index.spec.ts` 断言页面存在以下板块标记：
  - 账号概览
  - 基础资料
  - 登录信息
  - 安全中心
- 断言页面会调用 `getProfile()` 并展示“最近登录时间 / 最近登录 IP”字段。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

- 页面使用 `onMounted` 请求 `/auth/profile`。
- 首屏使用 `userStore.userInfo`、`permissions`、`roles` 生成兜底摘要。
- 页面提供：
  - 修改密码按钮
  - 退出登录按钮
  - 最近登录信息展示
- 用户名称显示统一改为：

```ts
const displayName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '用户')
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: PASS

### Task 6: 全量前后端验证

**Files:**
- Verify only

**Step 1: Run focused frontend tests**

Run: `npm test -- src/api/system/auth.spec.ts src/views/profile/index.spec.ts src/store/modules/user.spec.ts`
Expected: PASS

**Step 2: Run frontend full tests**

Run: `npm test`
Expected: PASS

**Step 3: Run frontend build**

Run: `npm run build`
Expected: PASS

**Step 4: Run backend focused tests**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

**Step 5: Check diagnostics**

Run: VS Code diagnostics for modified frontend files
Expected: no new blocking errors
