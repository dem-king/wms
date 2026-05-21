# Profile Edit Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为当前登录用户增加个人资料编辑与本地头像上传能力，并在保存成功后同步刷新个人中心和顶部导航展示。

**Architecture:** 后端在 `wms-auth` 模块新增当前用户资料更新与头像上传接口，所有操作都只面向当前登录用户。前端在现有个人中心页面内新增内联编辑区域和头像上传区，先完成本地预览，再通过接口保存，最终统一刷新 `/auth/profile` 和 `userStore`，保证页面与导航栏一致。

**Tech Stack:** Spring Boot、MyBatis-Plus、Vue 3、`<script setup lang="ts">`、Pinia、Vue Router、Element Plus、Vitest

---

### Task 1: 先补前端资料编辑与头像上传 API 失败测试

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\types\auth.d.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\system\auth.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\system\auth.spec.ts`

**Step 1: Write the failing test**

- 为 `updateProfile()` 编写断言，要求调用 `put('/auth/profile', payload)`。
- 为 `uploadAvatar()` 编写断言，要求调用 `post('/auth/profile/avatar', formData)`。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/api/system/auth.spec.ts`
Expected: FAIL，提示缺少 `updateProfile` 或 `uploadAvatar`。

**Step 3: Write minimal implementation**

- 在 `auth.d.ts` 中新增：
  - `UpdateProfileReq`
  - `UploadAvatarResp`
- 在 `auth.ts` 中新增：

```ts
export function updateProfile(data: UpdateProfileReq) {
  return put<ProfileResp>('/auth/profile', data)
}

export function uploadAvatar(data: FormData) {
  return post<UploadAvatarResp>('/auth/profile/avatar', data)
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/api/system/auth.spec.ts`
Expected: PASS

### Task 2: 先补个人中心编辑区失败测试

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\profile\index.spec.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\profile\index.vue`

**Step 1: Write the failing test**

- 让测试断言页面源码包含：
  - `编辑资料`
  - `真实姓名`
  - `手机号`
  - `邮箱`
  - `uploadAvatar`
  - `updateProfile`
  - `handleAvatarChange`
  - `handleSaveProfile`

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

- 在个人中心增加编辑资料卡片和表单状态。
- 增加头像上传区、本地预览占位和保存按钮。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: PASS

### Task 3: 先补后端资料更新失败测试

**Files:**
- Create: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\domain\dto\UpdateProfileDto.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\test\java\com\wms\auth\service\impl\AuthServiceImplProfileTest.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\AuthService.java`

**Step 1: Write the failing test**

- 为 `updateCurrentProfile()` 编写测试，断言：
  - 只更新当前登录用户
  - 真实姓名、手机号、邮箱、头像被正确写入
  - 返回最新资料

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: FAIL，提示缺少更新方法或 DTO。

**Step 3: Write minimal implementation**

- 在 `AuthService` 新增：

```java
AuthProfileVo updateCurrentProfile(UpdateProfileDto dto);
```

- `UpdateProfileDto` 包含：
  - `realName`
  - `phone`
  - `email`
  - `avatar`

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

### Task 4: 实现后端资料更新接口

**Files:**
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\controller\AuthController.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthServiceImpl.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-system\src\main\java\com\wms\system\service\SysUserService.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-system\src\main\java\com\wms\system\service\impl\SysUserServiceImpl.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-system\src\main\java\com\wms\system\domain\dto\SysUserDto.java` or add dedicated profile-update method as needed

**Step 1: Write the failing test**

- 让已有测试依赖真实更新路径。
- 如无 Controller 测试基建，则以 Service 测试保证核心行为。

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: FAIL

**Step 3: Write minimal implementation**

- 新增接口：

```java
@DataScope
@PreAuthorize("isAuthenticated()")
@PutMapping("/profile")
public R<AuthProfileVo> updateProfile(@Valid @RequestBody UpdateProfileDto dto) {
    return R.ok(authService.updateCurrentProfile(dto));
}
```

- Service 内通过当前用户 ID 获取用户并更新资料。
- 返回更新后的 `AuthProfileVo`。

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

### Task 5: 先补后端头像上传失败测试

**Files:**
- Create: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\domain\vo\UploadAvatarVo.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\test\java\com\wms\auth\service\impl\AuthServiceImplProfileTest.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\AuthService.java`

**Step 1: Write the failing test**

- 断言头像上传逻辑会：
  - 拒绝非图片类型
  - 拒绝超限文件
  - 成功时返回头像 URL

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: FAIL

**Step 3: Write minimal implementation**

- 在 `AuthService` 新增：

```java
UploadAvatarVo uploadCurrentUserAvatar(MultipartFile file);
```

- `UploadAvatarVo` 只返回头像 URL。

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

### Task 6: 实现后端头像上传接口

**Files:**
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\controller\AuthController.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-auth\src\main\java\com\wms\auth\service\impl\AuthServiceImpl.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-app\src\main\resources\application.yml`
- Modify: `D:\Codes\WMS_code\wms-server\wms-app\src\main\resources\application-dev.yml` if upload path config is needed

**Step 1: Write the failing test**

- 依赖 Task 5 的失败测试继续推进。

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: FAIL

**Step 3: Write minimal implementation**

- 新增接口：

```java
@PreAuthorize("isAuthenticated()")
@PostMapping("/profile/avatar")
public R<UploadAvatarVo> uploadAvatar(@RequestParam("file") MultipartFile file) {
    return R.ok(authService.uploadCurrentUserAvatar(file));
}
```

- 在 Service 中：
  - 校验文件类型与大小
  - 保存文件
  - 生成访问 URL
  - 不直接更新资料表，交给前端后续统一保存

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-auth -Dtest=AuthServiceImplProfileTest test`
Expected: PASS

### Task 7: 实现前端编辑资料与头像上传

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\profile\index.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\store\modules\user.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\types\auth.d.ts`

**Step 1: Write the failing test**

- 让 `index.spec.ts` 依赖以下标记：
  - `editForm`
  - `previewAvatarUrl`
  - `handleAvatarChange`
  - `handleSaveProfile`
  - `syncUserInfo`

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

- 增加编辑资料表单和校验规则。
- 使用文件选择 / 上传按钮处理本地头像上传。
- 保存成功后：
  - 调用 `/auth/profile`
  - 更新页面资料
  - 更新 `userStore.userInfo`

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/profile/index.spec.ts`
Expected: PASS

### Task 8: 全量验证

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
