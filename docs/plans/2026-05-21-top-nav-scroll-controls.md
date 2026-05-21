# Top Nav Scroll Controls Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 让顶部菜单在宽度足够时完整显示全部项，在宽度不足时显示左右滚动按钮并支持横向滚动。

**Architecture:** 保持现有 `Navbar.vue` 的横向滚动容器结构，新增一个纯函数用于判断菜单是否溢出以及左右滚动按钮是否可用。组件层只负责读取滚动状态、展示按钮和驱动滚动，不改变菜单数据与现有联动高亮逻辑。

**Tech Stack:** Vue 3、TypeScript、Element Plus、Vitest、SCSS

---

### Task 1: 顶部滚动状态测试

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\menu-layout.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\menu-layout.spec.ts`

**Step 1: Write the failing test**

为顶部菜单横向滚动新增 2 个失败用例：
- 宽度足够时，不应视为溢出，左右按钮都不可用
- 宽度不足且滚动到中间位置时，应识别为溢出，左右按钮都可用

**Step 2: Run test to verify it fails**

Run: `npm test -- src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: FAIL，提示新的滚动状态判断函数不存在

**Step 3: Write minimal implementation**

在 `menu-layout.ts` 中新增纯函数，基于 `scrollLeft/clientWidth/scrollWidth` 返回：
- `isOverflowing`
- `canScrollLeft`
- `canScrollRight`

**Step 4: Run test to verify it passes**

Run: `npm test -- src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

### Task 2: Navbar 左右滚动按钮

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`

**Step 1: Add button structure**

在顶部菜单区域两侧增加左右滚动按钮，仅在溢出时显示。

**Step 2: Wire scroll state**

复用纯函数更新：
- 宽度足够时隐藏按钮并完整展示菜单
- 宽度不足时显示按钮并根据位置禁用左右按钮

**Step 3: Implement scroll actions**

按钮点击后按固定步长横向滚动，并同步状态。

**Step 4: Verify behavior**

手动检查：
- 宽度足够时无按钮，菜单完整显示
- 宽度不足时出现左右按钮
- 左右滚动到底时按钮禁用正确

### Task 3: 验证

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\menu-layout.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\menu-layout.spec.ts`

**Step 1: Run focused tests**

Run: `npm test -- src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 2: Run diagnostics**

检查 `Navbar.vue`、`menu-layout.ts` 的诊断信息

**Step 3: Run build**

Run: `npm run build`
Expected: PASS
