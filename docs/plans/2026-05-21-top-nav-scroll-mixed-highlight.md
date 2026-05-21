# Top Nav Scroll Mixed Highlight Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 让顶部导航在菜单过多时支持横向滚动，并增强混合菜单模式下顶部根菜单与左侧子菜单的联动高亮。

**Architecture:** 保持现有 `Navbar.vue`、`Sidebar.vue` 和 `use-split-menu.ts` 结构不变，复用共享的 `activeRootMenuId` 作为混合菜单联动高亮的唯一状态来源。顶部菜单滚动优先通过滚动容器与轻量状态提示实现，避免重写 Element Plus 菜单逻辑。

**Tech Stack:** Vue 3、TypeScript、Element Plus、Vitest、SCSS

---

### Task 1: Split Menu 状态测试

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\menu-layout.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\menu-layout.spec.ts`

**Step 1: Write the failing test**

为“根菜单联动高亮判断”补 2 个失败用例：
- 当前路由命中根菜单子路由时，根菜单应视为联动激活
- 显式指定 `activeRootMenuId` 时，即使当前路径不等于根菜单路径，也应保持根菜单激活

**Step 2: Run test to verify it fails**

Run: `npm test -- src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: FAIL，提示缺少新的联动判断能力

**Step 3: Write minimal implementation**

在 `menu-layout.ts` 中新增一个纯函数，统一判断顶部根菜单是否应激活，供顶部混合菜单复用。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

### Task 2: 顶部菜单横向滚动

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`

**Step 1: Add scroll container structure**

在顶部菜单区域增加横向滚动容器和滚动提示状态。

**Step 2: Implement minimal scrolling behavior**

支持：
- `overflow-x: auto`
- 鼠标滚轮映射到横向滚动
- 根据滚动位置显示左右渐隐提示

**Step 3: Verify behavior**

手动检查顶部菜单数量较多时：
- 不换行
- 可横向滚动
- 左右提示随滚动更新

### Task 3: 混合菜单联动高亮

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\NavbarMenuItem.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Sidebar.vue`

**Step 1: Reuse shared root menu state**

在顶部混合菜单中复用 `activeRootMenuId/activeRootMenu`，为根菜单项附加联动高亮类。

**Step 2: Add sidebar linked highlight**

在左侧子菜单区域增加当前根菜单的联动指示样式，使顶部根菜单和左侧子菜单在视觉上属于同一组。

**Step 3: Verify linked highlight**

手动检查：
- 点击顶部根菜单后左侧子菜单区域立即联动
- 进入子路由后顶部根菜单保持高亮
- 深色主题下高亮仍清晰

### Task 4: 验证

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\NavbarMenuItem.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Sidebar.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\menu-layout.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\menu-layout.spec.ts`

**Step 1: Run focused tests**

Run: `npm test -- src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 2: Run diagnostics**

检查 `Navbar.vue`、`NavbarMenuItem.vue`、`Sidebar.vue`、`menu-layout.ts` 的诊断信息

**Step 3: Run build**

Run: `npm run build`
Expected: PASS
