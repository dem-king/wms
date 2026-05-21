# Warehouse Visual Workbench Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将库房可视化页面重构为工作台结构，支持 2D/2.5D 视图切换、快速定位高亮、存放柜详情弹窗，并清理旧的错误 API 出口。

**Architecture:** 保留现有 `visual-layout.ts` 与 `visual-state.ts` 的纯函数核心，围绕它们拆出工作台头部、画布、摘要和详情弹窗组件。页面层只负责数据加载、状态编排和组件装配，交互能力通过纯函数与结构测试先失败、后实现、再验证通过。

**Tech Stack:** Vue 3、`<script setup lang="ts">`、Vitest、Vue Konva、Element Plus

---

### Task 1: 扩展纯状态与布局模型

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\visual-state.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\visual-layout.ts`
- Test: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\visual-state.spec.ts`
- Test: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\visual-layout.spec.ts`

**Step 1: Write the failing test**

- 为视图模式切换、按编码定位、命中链路高亮补充断言。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/visual-state.spec.ts src/views/warehouse/visual/visual-layout.spec.ts`
Expected: FAIL，提示缺少新状态字段或辅助函数。

**Step 3: Write minimal implementation**

- 扩展布局索引与状态收敛逻辑，提供快速定位与当前高亮目标。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/visual-state.spec.ts src/views/warehouse/visual/visual-layout.spec.ts`
Expected: PASS

### Task 2: 先写页面拆分与 API 清理失败测试

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\index.spec.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\warehouse\visual-api.spec.ts`

**Step 1: Write the failing test**

- 为工作台组件引用、视图切换、详情弹窗标记写源码结构测试。
- 为旧聚合 API 出口删除或禁用写测试。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts src/api/warehouse/visual-api.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

- 调整页面源码结构和 API 文件。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts src/api/warehouse/visual-api.spec.ts`
Expected: PASS

### Task 3: 实现工作台组件拆分

**Files:**
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualToolbar.vue`
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualStage.vue`
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualSummary.vue`
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\CabinetDetailDialog.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\index.vue`

**Step 1: Write the failing test**

- 让 `index.spec.ts` 先断言这些组件和关键交互标记存在。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

- 将原页面拆为工作台结构并接通 props/emits。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: PASS

### Task 4: 接通交互与验证

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\index.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualStage.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\CabinetDetailDialog.vue`

**Step 1: Write the failing test**

- 先让快速定位、2.5D 标识、详情弹窗触发路径依赖于新代码。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

- 完成快速定位高亮、2D/2.5D 切换和详情弹窗联动。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: PASS

### Task 5: 全量验证

**Files:**
- Verify only

**Step 1: Run focused tests**

Run: `npm test -- src/views/warehouse/visual src/api/warehouse/visual-api.spec.ts`
Expected: PASS

**Step 2: Run build**

Run: `npm run build`
Expected: PASS

**Step 3: Check diagnostics**

Run: VS Code diagnostics for modified files
Expected: no new blocking errors
