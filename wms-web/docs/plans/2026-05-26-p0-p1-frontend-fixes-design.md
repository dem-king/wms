# 2026-05-26 P0/P1 Frontend Fixes Design

## 背景

根据 `code-review-report.md`，本轮优先修复前端仓库内可以独立完成的 P0/P1 问题，避免引入需要额外后端接口或生产部署约定才能确定的改动。

## 本轮范围

### 包含

1. `src/api/request.ts`
   - 修复 Blob 响应被 JSON 拦截器误判
   - 修复 refresh 失败时挂起请求未被 reject 的问题
2. `src/store/modules/permission.ts`
   - 修复动态路由重复注册
   - 将路由生成状态收回 store 内部
3. `src/components/LabelPrint/index.vue`
   - 修复 iframe 打印竞态
   - 去掉基于 `innerHTML` 的注入方式，改为基于数据生成打印内容
4. `src/components/ImagePreview/index.vue`
   - 修复相对路径解析
5. `src/views/business/scrap/components/ScrapForm.vue`
   - 实现 `handleItemChange`
   - 增加提交前明细校验
6. `src/views/business/transfer/components/TransferForm.vue`
   - 实现 `handleItemChange`
   - 增加提交前明细校验
7. `src/views/dashboard/index.vue`
   - 复用现有报表接口展示真实统计与预警摘要
8. 遗留 API 文件
   - 删除 `src/api/business/index.ts`
   - 删除 `src/api/item/index.ts`

### 不包含

1. `.env.production`
   - 当前代码并未消费 `VITE_API_BASE_URL`
   - 请求基址固定为 `/api`
   - 生产环境应写绝对域名还是继续走反向代理，取决于部署方案，本轮不做主观硬编码
2. 需要新增后端接口的项
   - 本轮仅复用现有接口，不扩展后端契约
3. 扩大到 P2 的基础设施重构
   - 如 `vitest` 全量切到 `jsdom`、`Chart` 性能专项等，不纳入本轮

## 设计决策

### 1. 请求层修复

- 对 `responseType === 'blob'` 的响应直接返回原始 `Blob`
- 对 JSON 响应继续做统一 `code === 200` 校验
- 将挂起队列从仅回调提升为可统一 resolve/reject 的结构，确保 refresh 失败时所有等待请求都能结束

### 2. 动态路由修复

- 在 store 内维护 `isRoutesAdded`
- 记录当前已注册的动态路由名称和兜底路由名称
- `resetPermission()` 时显式 `removeRoute()`
- 生成前先清理旧路由，避免重复添加

### 3. 打印安全修复

- 不再把页面 DOM 的 `innerHTML` 直接写入 iframe
- 由 `labels` 数据生成打印 HTML 字符串，并对文本内容做 HTML 转义
- `print()` 后通过 `afterprint` 与兜底定时器清理 iframe，避免打印对话框尚未打开就被移除

### 4. 表单行为修复

- `handleItemChange` 根据选中的物品回填当前行
- 在当前数据结构未展示更多列的前提下，至少进行选择确认和重复项防御
- 提交前校验每一行 `itemId`、`quantity`

### 5. Dashboard 修复

- 使用现有报表接口：
  - `getStockSummary`
  - `getReturnSummary`
  - `getAlertSummary`
  - `getStockTrend`
- 默认查询最近一个月
- 统计卡显示真实数据
- 右侧展示预警摘要列表，左侧图表展示库存趋势

## 测试策略

1. 对可在当前 Node 测试环境下验证的模块补回归测试
   - `request.ts`
   - `permission.ts`
2. Vue SFC 涉及的修复先做
   - 类型检查
   - 构建验证
   - 诊断检查
3. 若实现过程中发现现有测试环境无法稳定覆盖某项组件行为，不强行扩展到新的测试栈，本轮以可重复验证为先

## 风险与缓解

1. `Dashboard` 复用报表接口可能与真实首页口径存在差异
   - 先以已有接口提供可用真实数据，避免继续展示全 0
2. 打印窗口的 `afterprint` 在不同浏览器行为不完全一致
   - 增加兜底清理逻辑
3. 删除遗留 API 文件前需确认无引用
   - 已搜索当前 `src` 下无直接导入
