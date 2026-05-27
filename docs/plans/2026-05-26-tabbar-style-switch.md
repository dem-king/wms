# 标签页样式切换 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 让偏好设置中的 `chrome / card / plain` 三种标签页样式在实际页面中立即生效。

**Architecture:** 保持现有偏好设置数据结构和 `TagsView.vue` 标签数据逻辑不变，新增一个纯函数负责把 `tabbar.styleType` 映射为容器类名，并由 `TagsView.vue` 基于该类名切换三套样式。测试聚焦于样式类型映射，避免为本次小改动引入不必要的组件测试复杂度。

**Tech Stack:** Vue 3、TypeScript、SCSS、Vitest

---

### Task 1: 标签页样式映射测试

**Files:**
- Create: `d:\Codes\WMS_code\wms-web\src\layouts\composables\tabbar-style.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\tabbar-style.spec.ts`

**Step 1: Write the failing test**

在 `tabbar-style.spec.ts` 中新增失败用例，覆盖：
- `chrome` 映射到 `tags-view--chrome`
- `card` 映射到 `tags-view--card`
- `plain` 映射到 `tags-view--plain`
- 非法值回退到 `tags-view--chrome`

**Step 2: Run test to verify it fails**

Run: `npm test -- src/layouts/composables/__tests__/tabbar-style.spec.ts`
Expected: FAIL，提示 `resolveTabbarStyleClass` 不存在

**Step 3: Write minimal implementation**

在 `tabbar-style.ts` 中新增纯函数 `resolveTabbarStyleClass(styleType)`，仅返回上述 4 种场景对应的类名。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/layouts/composables/__tests__/tabbar-style.spec.ts`
Expected: PASS

### Task 2: 接入 TagsView 样式切换

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\TagsView.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\tabbar-style.ts`

**Step 1: Wire style class**

在 `TagsView.vue` 中读取 `preferences.tabbar.styleType`，通过 `resolveTabbarStyleClass` 生成容器类名，并挂载到根节点。

**Step 2: Add style branches**

在 `TagsView.vue` 中为 `chrome / card / plain` 增加三套样式分支：
- `chrome` 保持现有浏览器页签风格
- `card` 使用卡片化背景、边框和激活阴影
- `plain` 使用轻量胶囊/文字风格

**Step 3: Keep behavior unchanged**

确认以下逻辑不变：
- 标签点击切换路由
- 标签关闭
- 当前路由激活态
- 右侧更多操作入口

**Step 4: Verify manually**

手动检查偏好设置中切换三种样式后，当前标签栏视觉有明确变化。

### Task 3: 验证

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\TagsView.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\tabbar-style.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\tabbar-style.spec.ts`

**Step 1: Run focused tests**

Run: `npm test -- src/layouts/composables/__tests__/tabbar-style.spec.ts`
Expected: PASS

**Step 2: Run diagnostics**

检查 `TagsView.vue`、`tabbar-style.ts`、`tabbar-style.spec.ts` 的诊断信息

**Step 3: Run build**

Run: `npm run build`
Expected: PASS
