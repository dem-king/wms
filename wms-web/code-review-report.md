# WMS-Web 前端项目代码审查报告

> 审查日期：2026-05-26  
> 审查范围：`wms-web/` 全量前端代码（83 个文件）  
> 审查维度：项目配置、API 层、组件层、视图层、状态管理、路由、布局、样式、类型、工具函数

---

## 一、总体评估

| 维度 | 评分（1-5） | 评价 |
|------|------------|------|
| 架构设计 | ⭐⭐⭐⭐ | 布局系统与偏好设置设计精良，Reducer 模式状态管理是亮点 |
| 代码质量 | ⭐⭐⭐ | 新模块类型安全较好，但遗留 `any` 代码和空函数体拉低整体 |
| 安全性 | ⭐⭐⭐ | Token 存储在 localStorage 有 XSS 风险，部分文件存在注入隐患 |
| 类型安全 | ⭐⭐⭐ | 两极分化，新模块优秀，旧 index.ts 全 `any` |
| 可维护性 | ⭐⭐⭐ | 类型定义完整，但代码重复多，部分文件过长 |
| 测试覆盖 | ⭐⭐ | API 层约 22%，组件层约 5%，视图层几乎为零 |

**综合评分：3.1/5**

---

## 二、严重问题（P0 - 需立即修复）

### SEC-01 | `.env.production` — 生产环境 API 地址为空

- **文件**：[.env.production](file:///d:/Codes/WMS_code/wms-web/.env.production#L2)
- **行号**：L2
- **问题**：`VITE_API_BASE_URL=` 为空字符串。生产构建不启动 dev server 代理，前端所有 API 请求将发往空地址，全部失败。
- **修复**：填写生产环境后端地址，如 `https://wms-api.your-domain.com`。

### SEC-02 | `request.ts` — Blob 下载接口被 JSON 响应拦截器误判

- **文件**：[request.ts](file:///d:/Codes/WMS_code/wms-web/src/api/request.ts#L40-L46)
- **行号**：L40-L46
- **问题**：`exportReportExcel` 使用 `responseType: 'blob'`，但响应拦截器检查 `res.code !== 200`。Blob 对象无 `.code` 属性，`undefined !== 200` 恒为 true，**所有报表导出功能完全不可用**。
- **修复**：拦截器增加 `response.config.responseType === 'blob'` 判断，跳过 JSON 校验。

### SEC-03 | `business/index.ts` + `item/index.ts` — 全量 `any` 类型遗留代码

- **文件**：[business/index.ts](file:///d:/Codes/WMS_code/wms-web/src/api/business/index.ts)、[item/index.ts](file:///d:/Codes/WMS_code/wms-web/src/api/item/index.ts)
- **行号**：全文件
- **问题**：所有函数参数和返回值均使用 `any`，URL 路径与具名模块（`inbound.ts`、`item.ts` 等）冲突，形成两套平行 API 层，导入混淆严重。
- **修复**：删除这两个遗留文件，统一使用具名模块。

### SEC-04 | `LabelPrint/index.vue` — iframe 打印竞态条件导致打印失败

- **文件**：[LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L80-L119)
- **行号**：L80-L119
- **问题**：`window.print()` 异步打开打印对话框，但 `document.body.removeChild(iframe)` 在 `print()` 之后同步执行。用户看到对话框前 iframe 已被移除，**打印大概率失败或输出空白页**。
- **修复**：监听 `onafterprint` 事件后再移除 iframe，或使用 `setTimeout` 延迟移除。

### SEC-05 | `LabelPrint/index.vue` — innerHTML XSS 注入风险

- **文件**：[LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L109)
- **行号**：L109
- **问题**：`${printArea.innerHTML}` 将渲染内容通过 HTML 字符串拼接写入 iframe 的 `doc.write()`。若标签数据含恶意内容（如物品名称被注入 `<script>`），将导致存储型 XSS 攻击。
- **修复**：对 iframe 内容做 HTML 实体转义，或使用 `srcdoc` + CSP 头。

### SEC-06 | `dashboard/index.vue` — 完全空壳，所有数据硬编码为 0

- **文件**：[dashboard/index.vue](file:///d:/Codes/WMS_code/wms-web/src/views/dashboard/index.vue#L28-L41)
- **行号**：L28-L41
- **问题**：`statCards` 全部硬编码 `value: 0`，`onMounted` 函数体为空，没有调用任何 API。Dashboard 页面不展示任何真实数据，仅渲染空的统计卡片和 "暂无预警"。
- **修复**：接入后端 Dashboard 统计 API，在 `onMounted` 中获取真实数据。

### SEC-07 | `permission.ts` — 动态路由重复注册，`resetPermission` 未移除路由

- **文件**：[permission.ts](file:///d:/Codes/WMS_code/wms-web/src/store/modules/permission.ts#L41-L51)
- **行号**：L41-L51
- **问题**：`generateRoutes()` 使用 `router.addRoute()` 添加路由，但 `resetPermission()` 仅清空 store 数据，未调用 `router.removeRoute()`。多次调用 `generateRoutes()` 会导致路由重复注册，导航异常。
- **修复**：`resetPermission()` 中遍历 `dynamicRoutes` 调用 `router.removeRoute(route.name)`。

### SEC-08 | `ScrapForm.vue` + `TransferForm.vue` — `handleItemChange` 函数体为空

- **文件**：[ScrapForm.vue](file:///d:/Codes/WMS_code/wms-web/src/views/business/scrap/components/ScrapForm.vue#L128-L129)、[TransferForm.vue](file:///d:/Codes/WMS_code/wms-web/src/views/business/transfer/components/TransferForm.vue#L136-L137)
- **行号**：ScrapForm L128-L129 / TransferForm L136-L137
- **问题**：`handleItemChange(_row, _val)` 函数体完全为空。选择物品后不执行任何逻辑——不填充物品名称、不校验库存、不提示任何信息。明细行 `itemId` 选择后 UI 无任何反馈，用户无法确认选择是否生效。
- **修复**：实现 `handleItemChange`：根据 `val` 查询物品信息并回填 `row` 的关联字段，或至少做选择确认提示。

### SEC-09 | `ImagePreview/index.vue` — `resolveUrl` 实现与注释不一致，相对路径无法加载

- **文件**：[ImagePreview/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/ImagePreview/index.vue#L41-L48)
- **行号**：L41-L48
- **问题**：JSDoc 注释明确说"相对路径拼接 API base URL"，但实际代码仅 `return url` 不做任何拼接。后端返回相对路径（如 `/api/storage/file/xxx`）时图片完全无法加载。
- **修复**：实现 base URL 拼接逻辑，或更新注释说明依赖 Nginx 代理。

---

## 三、重要问题（P1 - 本迭代修复）

### API 层

| # | 文件 | 行号 | 问题 | 修复建议 |
|---|------|------|------|----------|
| I-01 | [request.ts](file:///d:/Codes/WMS_code/wms-web/src/api/request.ts#L43) | L43 | 非 200 响应 `Promise.reject(new Error(res.msg))` 丢弃了 `code` 和 `data`，调用方无法做细分错误处理 | 使用自定义 Error 类携带 `code` 和 `data` |
| I-02 | [request.ts](file:///d:/Codes/WMS_code/wms-web/src/api/request.ts#L45) | L45 | `return res as unknown as AxiosResponse<R>` 类型伪装，实际返回 `{code,msg,data}` 但 TS 类型认为是 `AxiosResponse` | 修改泛型函数返回 `Promise<R<T>>`，直接返回 `response.data` |
| I-03 | [request.ts](file:///d:/Codes/WMS_code/wms-web/src/api/request.ts#L49-L87) | L49-L87 | Token 刷新失败时 `pendingRequests` 队列中的 Promise 永远不会 resolve/reject，调用方永久挂起 | `clearAuthAndRedirect()` 中 reject 所有 pending Promises |
| I-04 | [request.ts](file:///d:/Codes/WMS_code/wms-web/src/api/request.ts#L2-L4) | L2-L4 | API 层直接依赖 `ElMessage` 和 `vue-router`，违反分层原则，测试困难 | 通过事件总线或回调注入解耦 |
| I-05 | [report/index.ts](file:///d:/Codes/WMS_code/wms-web/src/api/report/index.ts#L49-L50) | L49-L50 | 报表导出使用 `responseType: 'blob'`，与 JSON 响应拦截器冲突（同 SEC-02） | 同 SEC-02 修复 |
| I-06 | [business/inbound.ts](file:///d:/Codes/WMS_code/wms-web/src/api/business/inbound.ts#L50-L64) | L50-L64 | `ORDER_STATUS_TO_CODE` 映射表与 `outbound.ts` 完整重复，维护时需两处同步 | 提取到公共常量文件 `@/api/business/constants.ts` |
| I-07 | [system/log.ts](file:///d:/Codes/WMS_code/wms-web/src/api/system/log.ts#L4-L5) | L4-L5 | `getOperLogPage` 返回类型 `<SysOperLogVo>` 应为 `<PageResult<SysOperLogVo>>` | 修正泛型 |
| I-08 | [monitor/index.ts](file:///d:/Codes/WMS_code/wms-web/src/api/monitor/index.ts#L4-L7) | L4-L7 | 同上，分页接口返回类型错误 | 修正泛型 |
| I-09 | [common/storage.ts](file:///d:/Codes/WMS_code/wms-web/src/api/common/storage.ts#L24) | L24 | `deleteFile` 的 `objectName` 未 `encodeURIComponent`，特殊字符导致路径错误 | 添加编码 |

### 组件层

| # | 文件 | 行号 | 问题 | 修复建议 |
|---|------|------|------|----------|
| I-10 | [Chart/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/Chart/index.vue#L42-L44) | L42-L44 | `watch(() => props.option, ..., { deep: true })` 对大对象深度监听，性能开销巨大 | 改用 `shallowRef` 或要求调用方传入新对象引用 |
| I-11 | [Chart/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/Chart/index.vue#L53) | L53 | `window.addEventListener('resize', handleResize)` 无节流，窗口缩放时频繁重绘导致卡顿 | 加 200ms debounce |
| I-12 | [LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L81) | L81 | 使用静态 `id="label-print-area"` + `document.getElementById`，多实例 ID 冲突 | 改用 Vue `ref` 模板引用 |
| I-13 | [QrBarCode/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/QrBarCode/index.vue#L124-L132) | L124-L132 | `JsBarcode(barcodeRef.value, ...)` 直接操作 SVG DOM，Vue 对内部变更无感知 | 每次渲染前清空子节点 `barcodeRef.value.innerHTML = ''` |
| I-14 | [QrBarCode/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/QrBarCode/index.vue#L44-L45) | L44-L45 | JsBarcode 和 QRCode 库无条件全量引入 | 改为 `import()` 动态按需加载 |
| I-15 | [TableActionGroup.vue](file:///d:/Codes/WMS_code/wms-web/src/components/TableActionGroup/TableActionGroup.vue#L37) | L37 | `onClick` 异常无 try/catch，未捕获 rejection 传播到全局无提示 | 添加 try/catch + `ElMessage.error()` |
| I-16 | [FileUpload/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/FileUpload/index.vue) | L19 | server 模式进度直接从 0 跳到 100，大文件上传无中间态反馈 | 至少显示 indeterminate loading 动画 |
| I-17 | [Chart/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/Chart/index.vue#L6) | L6 | `chartType` prop 完全未使用（死代码），传入不会产生任何效果 | 实现自动配置逻辑或移除 |

### 视图层

| # | 文件 | 行号 | 问题 | 修复建议 |
|---|------|------|------|----------|
| I-18 | [visual/index.vue](file:///d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/index.vue#L136-L142) | L136-L142 | N+1 查询：先查区域列表 → 逐个区域查存放柜 → 逐个存放柜查库位。有 5 个区域、每个 10 个存放柜时会产生 56 次请求 | 后端增加按库房 ID 批量查询所有存放柜/库位的接口，前端改为单次请求 |
| I-19 | [ScrapForm.vue](file:///d:/Codes/WMS_code/wms-web/src/views/business/scrap/components/ScrapForm.vue#L124-L126) | L124-L126 | `addDetailRow()` 创建的行 `itemId` 为 `undefined as unknown as number`，提交时不做校验即传给后端 | 提交前校验每行的 `itemId` 不为空 |
| I-20 | [TransferForm.vue](file:///d:/Codes/WMS_code/wms-web/src/views/business/transfer/components/TransferForm.vue#L132-L134) | L132-L134 | 同上，明细行 `itemId` 未校验 | 同 I-19 |
| I-21 | [guard.ts](file:///d:/Codes/WMS_code/wms-web/src/router/guard.ts#L46-L53) | L46-L53 | 动态路由生成失败时（如网络异常），不分原因直接清除认证状态跳转登录页 | 区分 HTTP 401 和其他网络错误 |

### 状态管理/路由

| # | 文件 | 行号 | 问题 | 修复建议 |
|---|------|------|------|----------|
| I-22 | [auth.ts](file:///d:/Codes/WMS_code/wms-web/src/utils/auth.ts#L9-L18) | L9-L18 | `accessToken` 和 `refreshToken` 存储在 `localStorage`，XSS 可窃取 | 考虑 httpOnly Cookie + 内存存储 |
| I-23 | [permission.ts](file:///d:/Codes/WMS_code/wms-web/src/store/modules/permission.ts#L10) | L10 | `isRoutesAdded` 定义在 store 外部，不参与响应式系统，SSR 场景共享状态 | 移入 `defineStore` 内部 |
| I-24 | [app.ts](file:///d:/Codes/WMS_code/wms-web/src/store/modules/app.ts#L42) | L42 | `setLayout(newLayout: string)` 参数类型为 `string`，内部 `as any` 绕过类型检查 | 使用 `LayoutType` 类型 |
| I-25 | [user.ts](file:///d:/Codes/WMS_code/wms-web/src/store/modules/user.ts#L29-L97) | L29-L97 | JSDoc 注释多处乱码（"用?"、"登?"、"菜单数据传递给permission store用于动态路由生?"） | 修复编码问题 |

---

## 四、建议问题（P2 - 计划修复）

### API 层

| # | 文件 | 行号 | 问题 | 建议 |
|---|------|------|------|------|
| S-01 | [request.ts](file:///d:/Codes/WMS_code/wms-web/src/api/request.ts#L24) | L24 | 超时 30000ms，错误处理未区分 timeout/network/5xx | 根据 `error.code` 和 `error.response.status` 分类提示 |
| S-02 | [return.ts](file:///d:/Codes/WMS_code/wms-web/src/api/business/return.ts) | 全文件 | 缺少 `inbound`/`outbound` 那种枚举映射转换 | 补充 `ORDER_STATUS_TO_CODE` 映射 |
| S-03 | [scrap.ts](file:///d:/Codes/WMS_code/wms-web/src/api/business/scrap.ts) | 全文件 | 同上 | 同 S-02 |
| S-04 | [transfer.ts](file:///d:/Codes/WMS_code/wms-web/src/api/business/transfer.ts) | 全文件 | 同上 | 同 S-02 |
| S-05 | [system/log.ts](file:///d:/Codes/WMS_code/wms-web/src/api/system/log.ts) | L4-L5 | 所有函数压缩单行，可读性差 | 拆分为多行 |
| S-06 | [monitor/index.ts](file:///d:/Codes/WMS_code/wms-web/src/api/monitor/index.ts) | L4-L7 | 同上 | 同上 |

### 组件层

| # | 文件 | 行号 | 问题 | 建议 |
|---|------|------|------|------|
| S-07 | [LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L21-L37) | L21-L37 | 模板中使用魔法数字 `1`、`2`、`3` 判断标签类型 | 创建常量枚举替代 |
| S-08 | [LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L98-L107) | L98-L107 | 打印 CSS 样式硬编码在 JS 字符串中 | 提取为常量或使用 `@media print` |
| S-09 | [LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L100) | L100 | `font-family: 'Microsoft YaHei'` 硬编码，非 Windows 系统不可用 | 使用系统字体栈 |
| S-10 | [QrBarCode/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/QrBarCode/index.vue#L125) | L125 | `format: 'CODE128'` 硬编码条形码格式 | 添加 `barcodeFormat` prop |
| S-11 | [QrBarCode/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/QrBarCode/index.vue#L119) | L119 | `await nextTick()` 在 QR 路径中无必要，浪费微任务周期 | 移到 barcode 分支内 |
| S-12 | [Chart/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/Chart/index.vue#L8) | L8 | `option?: Record<string, any>` 丧失 ECharts 类型检查 | 使用 `echarts.EChartsOption` |
| S-13 | [TableActionGroup.vue](file:///d:/Codes/WMS_code/wms-web/src/components/TableActionGroup/TableActionGroup.vue#L43-L53) | L43-L53 | 按钮无 `loading` 属性，异步操作可重复点击 | 维护 `loadingKeys` 集合 |
| S-14 | [FileUpload/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/FileUpload/index.vue) | 全文件 | 组件名为 `FileUpload`，默认文案却是"上传图片"，命名不一致 | 统一命名或拆分 |
| S-15 | [ImagePreview/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/ImagePreview/index.vue#L5) | L5 | `:key="img.id \|\| index"` 索引作为 fallback key | 使用 `img.id \|\| img.imageUrl` |
| S-16 | [ImagePreview/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/ImagePreview/index.vue#L3-L12) | L3-L12 | `el-image` 未设置 `alt` 属性，无障碍缺失 | 添加 `:alt="img.imageName \|\| '图片'"` |

### 基础设施

| # | 文件 | 行号 | 问题 | 建议 |
|---|------|------|------|------|
| S-17 | `package.json` | — | `eslint` 及插件在 lint 脚本中引用但未安装 | 安装依赖或移除脚本 |
| S-18 | `vitest.config.ts` | L11 | `environment: 'node'`，Vue 组件测试不可用 | 改为 `'jsdom'` |
| S-19 | `index.html` | L7 | 页面标题硬编码，未使用 `VITE_APP_TITLE` 环境变量 | `<title><%= VITE_APP_TITLE %></title>` |
| S-20 | — | — | 缺少 `.gitignore` 文件 | 创建并忽略 `node_modules/`、`dist/`、`.env.local` |
| S-21 | `tsconfig.node.json` | L9 | `include` 遗漏 `vitest.config.ts` | 补充 |
| S-22 | [variables.scss](file:///d:/Codes/WMS_code/wms-web/src/styles/variables.scss#L7-L9) | L7-L9 | SCSS 变量（sidebar 210px）与偏好设置默认值（224px）不一致 | 统一数值或标记 `@deprecated` |
| S-23 | [dark.css](file:///d:/Codes/WMS_code/wms-web/src/styles/design-tokens/dark.css) | — | `--ring` 变量在 slate 主题下缺少 `%` 符号 | 补充 |
| S-24 | [warehouse.d.ts](file:///d:/Codes/WMS_code/wms-web/src/types/warehouse.d.ts) | — | `EntityId` 定义为 `string`，但后端雪花 ID 为 `number` | 修正为 `number` |
| S-25 | [business.d.ts](file:///d:/Codes/WMS_code/wms-web/src/types/business.d.ts) | — | 568 行文件过长，混合多种业务类型 | 按子业务拆分 |
| S-26 | [auth.d.ts](file:///d:/Codes/WMS_code/wms-web/src/types/auth.d.ts) | — | `MenuTreeNode` 定义在 auth 类型文件中，实际属于系统管理模块 | 移到 `system.d.ts` |

---

## 五、安全审查专项

| 风险等级 | 问题 | 文件 |
|---------|------|------|
| 🔴 高 | Token 存储在 localStorage，XSS 可窃取 | [auth.ts](file:///d:/Codes/WMS_code/wms-web/src/utils/auth.ts#L9-L18) |
| 🔴 高 | 标签打印 innerHTML 注入，存储型 XSS 风险 | [LabelPrint/index.vue](file:///d:/Codes/WMS_code/wms-web/src/components/LabelPrint/index.vue#L109) |
| 🟠 中 | `refreshToken` 同样存 localStorage，可被无限续期 | [auth.ts](file:///d:/Codes/WMS_code/wms-web/src/utils/auth.ts#L9-L18) |
| 🟠 中 | Token 无过期时间检测，仅依赖 API 401 | [guard.ts](file:///d:/Codes/WMS_code/wms-web/src/router/guard.ts#L23) |
| 🟠 中 | 登录密码明文传输（建议 RSA 加密，项目已有 `getRsaPublicKey`） | [system/auth.ts](file:///d:/Codes/WMS_code/wms-web/src/api/system/auth.ts) |
| 🟡 低 | `clearAuth()` 未清除 sessionStorage | [auth.ts](file:///d:/Codes/WMS_code/wms-web/src/utils/auth.ts#L57-L63) |
| 🟡 低 | `document.title = to.meta.title` 若 title 来自用户输入有风险 | [guard.ts](file:///d:/Codes/WMS_code/wms-web/src/router/guard.ts#L58) |

---

## 六、测试覆盖评估

| 层级 | 测试文件数 | 用例数 | 覆盖率估计 | 评价 |
|------|-----------|--------|-----------|------|
| API 层 | 6 | 14 | ~22% | inbound/outbound 测试较好，return/scrap/transfer 完全缺失 |
| 组件层 | 1 | 2 | ~5% | 仅 rendering.spec.ts，其他 6 个组件无测试 |
| 视图层 | 9 | 若干 | ~10% | visual 模块测试较好，业务页面无组件级测试 |
| 状态管理 | 2 | 若干 | ~30% | menu-layout.spec.ts 和 user.spec.ts |
| **总计** | **18** | **~40** | **~15%** | **严重不足** |

**关键测试缺口：**
- `request.ts` 核心请求模块完全无测试
- `ScrapForm`/`TransferForm` 空函数体无测试暴露
- `LabelPrint` 打印核心逻辑无测试
- 导航守卫（router guard）无测试
- `vitest.config.ts` 中 `environment: 'node'` 导致 Vue 组件测试不可用

---

## 七、性能审查

| 影响等级 | 问题 | 位置 | 建议 |
|---------|------|------|------|
| 🔴 高 | Chart 组件 deep watch 大对象 | [Chart/index.vue#L42](file:///d:/Codes/WMS_code/wms-web/src/components/Chart/index.vue#L42) | 改为浅监听 |
| 🔴 高 | Chart 组件 resize 无节流 | [Chart/index.vue#L53](file:///d:/Codes/WMS_code/wms-web/src/components/Chart/index.vue#L53) | 加 debounce 200ms |
| 🔴 高 | 仓库可视化 N+1 查询（56+ 次请求） | [visual/index.vue#L136-142](file:///d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/index.vue#L136-L142) | 后端批量查询 |
| 🟠 中 | QrBarCode 库无条件打包 | [QrBarCode/index.vue#L44](file:///d:/Codes/WMS_code/wms-web/src/components/QrBarCode/index.vue#L44) | 动态 import |
| 🟠 中 | QrBarCode watch 无去抖 | [QrBarCode/index.vue#L139](file:///d:/Codes/WMS_code/wms-web/src/components/QrBarCode/index.vue#L139) | v-model 输入 debounce |
| 🟡 低 | ImagePreview 无懒加载 | [ImagePreview/index.vue#L3](file:///d:/Codes/WMS_code/wms-web/src/components/ImagePreview/index.vue#L3) | 使用 `el-image` lazy |

---

## 八、与后端开发规则对照检查（前端对应项）

| 后端规则 | 前端对应关注点 | 状态 |
|----------|---------------|------|
| 禁止硬编码敏感信息 | `.env.production` API 地址为空可能导致 fallback 硬编码 | ⚠️ 需检查 |
| 前后端命名一致 | 环境变量 `VITE_API_BASE_URL`、API 路径对齐 | ✅ |
| 返回 VO 而非 Entity | `normalizeInboundOrder` 已将 API 类型转为 UI 类型 | ✅ |
| 禁止魔法数字 | `LabelPrint` 模板中存在标签类型魔法数字 `1/2/3` | ❌ 违规 |
| 配置分离 | 开发/生产 `.env` 分离 | ✅ |
| 关键逻辑有行内注释 | `handleItemChange` 等关键函数缺少注释 | ⚠️ 不足 |

---

## 九、修复优先级路线图

### 第一周（P0 - 阻塞性）
1. ✅ 修复 `.env.production` API 地址
2. ✅ 修复 Blob 下载拦截器误判
3. ✅ 删除 `business/index.ts` 和 `item/index.ts` 遗留代码
4. ✅ 修复 LabelPrint 打印竞态条件和 XSS 风险
5. ✅ 修复 `permission.ts` 路由重复注册

### 第二周（P0 - 功能缺陷）
6. ✅ 实现 Dashboard 数据获取
7. ✅ 实现 `ScrapForm`/`TransferForm` 的 `handleItemChange`
8. ✅ 修复 `ImagePreview.resolveUrl` 相对路径

### 第三周（P1 - 质量提升）
9. 修复 `request.ts` Token 刷新泄漏
10. 修复 `request.ts` 类型伪装
11. 解耦 API 层与 UI/路由依赖
12. 修复 Chart deep watch 性能和 resize 节流
13. 修复仓库可视化 N+1 查询
14. 安装 ESLint 或修复 lint 脚本

### 第四周（P1 - 一致性）
15. 补充 return/scrap/transfer 枚举映射
16. 修复分页接口返回类型
17. 统一 `EntityId` 类型定义
18. 提取重复的 `ORDER_STATUS_TO_CODE`

### 后续迭代（P2 - 优化）
19. `vitest.config.ts` 环境改为 jsdom
20. Token 存储安全加固
21. 补充测试覆盖（目标 ≥ 50%）
22. 修复 `variables.scss` 与偏好设置值不一致
23. 创建 `.gitignore`、`.editorconfig`

---

## 十、亮点总结

项目中以下方面值得肯定：

1. **可视化仓库模块** — 使用 Reducer 模式管理状态（`visual-state.ts`、`layout-editor.ts`），设计优秀，配套测试完善
2. **偏好设置系统** — 14 种主题预设、7 种布局、CSS 变量注入机制，高度可配置
3. **标签模块工具化** — `code-display.ts`、`print-feedback.ts`、`print-status-sync.ts` 独立抽取，模块化好
4. **inbound/outbound API 层** — normalize 转换层设计（API Vo → UI Vo），类型完整，枚举映射清晰
5. **QrBarCode 组件** — `rendering.ts` 纯函数 + 组件分离，架构清晰，便于测试
6. **TableActionGroup** — 配置驱动的操作按钮组，通用性好
7. **FileUpload + useFileUpload** — Hook 抽象上传逻辑，支持 server/presign 双模式

---

> *本报告由自动化代码审查工具生成，审查了 wms-web 项目中 83 个文件，共发现 9 个严重问题、25 个重要问题、26 个建议问题。*