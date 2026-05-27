# P0/P1 Frontend Fixes Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复本轮前端可独立完成的 P0/P1 问题，恢复关键功能可用性并补上最核心回归验证。

**Architecture:** 先从可在当前测试环境下稳定验证的 `request.ts` 和 `permission.ts` 入手，按 TDD 写失败测试后做最小修复；随后处理 Vue 组件与页面层问题，通过类型检查、构建和诊断保证改动可落地。Dashboard 不新增后端接口，直接复用现有报表接口填充真实数据。

**Tech Stack:** Vue 3, TypeScript, Vite, Vitest, Pinia, Vue Router, Element Plus, Axios, ECharts

---

### Task 1: 请求层回归测试与修复

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\api\request.ts`
- Create: `d:\Codes\WMS_code\wms-web\src\api\request.spec.ts`

**Step 1: Write the failing test**

- 覆盖 `responseType: 'blob'` 不应走 JSON `code` 校验
- 覆盖 refresh 失败时挂起请求必须 reject

**Step 2: Run test to verify it fails**

Run: `npm test -- src/api/request.spec.ts`
Expected: FAIL，暴露 Blob 拦截错误或挂起队列未结束

**Step 3: Write minimal implementation**

- 区分 Blob 与 JSON 响应
- 将挂起队列改为可统一 resolve/reject
- refresh 失败时清空并 reject 队列

**Step 4: Run test to verify it passes**

Run: `npm test -- src/api/request.spec.ts`
Expected: PASS

### Task 2: 动态路由回归测试与修复

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\store\modules\permission.ts`
- Create: `d:\Codes\WMS_code\wms-web\src\store\modules\permission.spec.ts`

**Step 1: Write the failing test**

- 覆盖重复调用 `generateRoutes()` 不会累积脏路由
- 覆盖 `resetPermission()` 会移除已注册动态路由

**Step 2: Run test to verify it fails**

Run: `npm test -- src/store/modules/permission.spec.ts`
Expected: FAIL，暴露未调用 `removeRoute()` 或状态残留

**Step 3: Write minimal implementation**

- 将 `isRoutesAdded` 移入 store
- 记录并移除已添加路由
- 兜底 404 路由使用固定名称并统一清理

**Step 4: Run test to verify it passes**

Run: `npm test -- src/store/modules/permission.spec.ts`
Expected: PASS

### Task 3: 标签打印安全与稳定性修复

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\components\LabelPrint\index.vue`

**Step 1: Implement minimal fix**

- 用模板 `ref` 替代静态 DOM id
- 基于 `labels` 数据生成打印 HTML，不再使用 `innerHTML`
- 对文本内容做 HTML 转义
- 通过 `afterprint` + 兜底定时器清理 iframe

**Step 2: Verify behavior**

Run: `npm run build`
Expected: BUILD SUCCESS

### Task 4: 图片预览路径修复

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\components\ImagePreview\index.vue`

**Step 1: Implement minimal fix**

- 为相对路径补齐 `/api` 前缀
- 避免重复拼接
- 顺带修复 `key` 与 `alt` 的低风险问题

**Step 2: Verify behavior**

Run: `npm run build`
Expected: BUILD SUCCESS

### Task 5: 报废/调拨表单行为修复

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\scrap\components\ScrapForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\transfer\components\TransferForm.vue`

**Step 1: Implement minimal fix**

- `handleItemChange` 至少校验选择结果并阻止重复物品
- 提交前逐行校验 `itemId` 与 `quantity`
- 保持现有字段结构，不额外扩展后端 DTO

**Step 2: Verify behavior**

Run: `npm run build`
Expected: BUILD SUCCESS

### Task 6: Dashboard 接入真实数据

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\dashboard\index.vue`
- Reuse: `d:\Codes\WMS_code\wms-web\src\api\report\index.ts`

**Step 1: Implement minimal fix**

- 默认查询最近一个月
- 拉取 `getStockSummary`、`getReturnSummary`、`getAlertSummary`、`getStockTrend`
- 填充统计卡、趋势图和预警摘要

**Step 2: Verify behavior**

Run: `npm run build`
Expected: BUILD SUCCESS

### Task 7: 清理遗留 API 文件

**Files:**
- Delete: `d:\Codes\WMS_code\wms-web\src\api\business\index.ts`
- Delete: `d:\Codes\WMS_code\wms-web\src\api\item\index.ts`

**Step 1: Remove unused files**

- 删除前确认无引用

**Step 2: Verify behavior**

Run: `npm run build`
Expected: BUILD SUCCESS

### Task 8: 全量验证

**Files:**
- Check: `d:\Codes\WMS_code\wms-web\src\api\request.ts`
- Check: `d:\Codes\WMS_code\wms-web\src\store\modules\permission.ts`
- Check: `d:\Codes\WMS_code\wms-web\src\components\LabelPrint\index.vue`
- Check: `d:\Codes\WMS_code\wms-web\src\components\ImagePreview\index.vue`
- Check: `d:\Codes\WMS_code\wms-web\src\views\business\scrap\components\ScrapForm.vue`
- Check: `d:\Codes\WMS_code\wms-web\src\views\business\transfer\components\TransferForm.vue`
- Check: `d:\Codes\WMS_code\wms-web\src\views\dashboard\index.vue`

**Step 1: Run focused tests**

Run: `npm test -- src/api/request.spec.ts src/store/modules/permission.spec.ts`
Expected: PASS

**Step 2: Run full test suite**

Run: `npm test`
Expected: PASS

**Step 3: Run build**

Run: `npm run build`
Expected: BUILD SUCCESS

**Step 4: Run diagnostics**

- 使用编辑器诊断检查最近修改文件
