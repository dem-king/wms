# Global ID Stringification Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 统一前后端所有雪花 ID 相关字段为字符串传输与消费，彻底消除 JavaScript 大整数精度丢失问题。

**Architecture:** 后端继续通过 Jackson 统一把 REST JSON 中的 `Long` 输出为字符串；前端从类型层开始引入 `EntityId = string`，再逐步修复 API、页面、状态管理、路由与测试中所有实体 ID 的消费方式。整个改造按“类型/API -> 业务域 -> 系统域 -> 全局清扫”的顺序分批推进，每批都执行测试与构建校验。

**Tech Stack:** Spring Boot 3, Jackson, Vue 3, TypeScript, Vite, Vitest, Element Plus

---

### Task 1: 固化后端 Long 字符串化基线

**Files:**
- Modify: `d:\Codes\WMS_code\wms-server\wms-common\src\main\java\com\wms\common\config\JacksonConfig.java`
- Test: `d:\Codes\WMS_code\wms-server\wms-common\src\test\java\com\wms\common\config\JacksonConfigTest.java`

**Step 1: 写失败测试**
- 断言 `2056308883391909890L` 序列化后包含 `"id":"2056308883391909890"`

**Step 2: 运行测试验证失败**
- Run: `mvn -pl wms-common -Dtest=JacksonConfigTest test`
- Expected: FAIL，当前 JSON 中 `id` 为数值

**Step 3: 写最小实现**
- 在 `JacksonConfig` 中为 `Long.class` 和 `long` 注册 `ToStringSerializer`

**Step 4: 再跑测试**
- Run: `mvn -pl wms-common -Dtest=JacksonConfigTest test`
- Expected: PASS

**Step 5: 校验影响**
- 确认无额外 Jackson 自定义器覆盖默认行为

### Task 2: 统一前端基础 ID 类型

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\types\business.d.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\types\item.d.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\types\label.d.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\types\system.d.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\types\auth.d.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\types\warehouse.d.ts`

**Step 1: 写失败检查**
- 先执行 TypeScript 构建，记录因 ID 类型不一致导致的报错

**Step 2: 运行检查**
- Run: `npm run build`
- Expected: FAIL，指向 API / 视图层仍按 `number` 使用实体 ID

**Step 3: 写最小实现**
- 在类型文件中统一引入或复用 `EntityId = string`
- 将所有实体主键、外键、批量 ID 数组改为字符串
- 保留状态码、数量、金额、排序号等数值字段不变

**Step 4: 再跑构建**
- Run: `npm run build`
- Expected: 仍 FAIL，但报错收敛到 API / 页面消费层

**Step 5: 记录残余报错**
- 作为下一批 API 和页面修复输入

### Task 3: 修复业务域 API 的 ID 强转与入参类型

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\api\business\inbound.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\business\outbound.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\business\return.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\business\scrap.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\business\transfer.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\api\business\inbound.spec.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\api\business\outbound.spec.ts`

**Step 1: 写失败测试**
- 补充或更新测试，断言 API 归一化后返回的 `id`、`warehouseId`、`itemId` 等保持字符串

**Step 2: 运行测试验证失败**
- Run: `npm test -- src/api/business/inbound.spec.ts src/api/business/outbound.spec.ts`
- Expected: FAIL，当前实现仍使用 `Number(...)`

**Step 3: 写最小实现**
- 删除所有针对实体 ID 的 `Number(...)`
- 路径参数、查询参数、请求体 ID 改为字符串

**Step 4: 再跑测试**
- Run: `npm test -- src/api/business/inbound.spec.ts src/api/business/outbound.spec.ts`
- Expected: PASS

**Step 5: 追加回归扫描**
- 用搜索确认业务 API 中不再残留实体 ID 强转

### Task 4: 修复物品与标签域 API

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\api\item\category.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\item\item.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\item\label.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\item\machineSpare.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\item\stock.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\item\tag.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\api\item\label.spec.ts`

**Step 1: 写失败测试**
- 断言标签、物品、库存链路中的实体 ID 保持字符串

**Step 2: 运行测试验证失败**
- Run: `npm test -- src/api/item/label.spec.ts`
- Expected: FAIL 或类型不匹配

**Step 3: 写最小实现**
- 更新 API 入参与返回值类型
- 移除所有实体 ID 的数值化逻辑

**Step 4: 再跑测试**
- Run: `npm test -- src/api/item/label.spec.ts`
- Expected: PASS

**Step 5: 构建确认**
- Run: `npm run build`
- Expected: 报错继续收敛到页面层

### Task 5: 修复系统、认证、审批 API

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\auth.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\config.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\dept.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\menu.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\permission.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\role.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\supplier.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\system\user.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\api\approval\index.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\api\system\auth.spec.ts`

**Step 1: 写失败测试**
- 确认认证 / 用户 / 菜单 / 审批等接口的 ID 使用字符串

**Step 2: 运行测试验证失败**
- Run: `npm test -- src/api/system/auth.spec.ts`
- Expected: FAIL 或类型不匹配

**Step 3: 写最小实现**
- 所有实体 ID 入参与返回值改成字符串
- 避免菜单树、角色授权、审批配置中的 ID 被数值化

**Step 4: 再跑测试**
- Run: `npm test -- src/api/system/auth.spec.ts`
- Expected: PASS

**Step 5: 记录页面影响**
- 作为系统页面批次的输入

### Task 6: 修复业务页面与扫码链路

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\inbound\components\InboundForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\outbound\components\OutboundForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\return\components\ReturnForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\scrap\components\ScrapForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\transfer\components\TransferForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\order-scan.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\views\business\order-scan.spec.ts`

**Step 1: 写失败测试**
- 断言扫码与表单回填使用字符串 ID，不丢精度

**Step 2: 运行测试验证失败**
- Run: `npm test -- src/views/business/order-scan.spec.ts`
- Expected: FAIL 或类型不匹配

**Step 3: 写最小实现**
- 将表单模型、下拉回填、编辑态单据 ID、明细项关联 ID 改为字符串

**Step 4: 再跑测试**
- Run: `npm test -- src/views/business/order-scan.spec.ts`
- Expected: PASS

**Step 5: 手工验证点**
- 入库、出库、归还、报废、调拨的编辑和提交链路能带对 ID

### Task 7: 修复物品、标签、库存页面

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\category\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\list\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\machineSpare\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\tag\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\label\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\label\components\LabelBind.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\label\print-status-sync.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\stock\index.vue`
- Test: `d:\Codes\WMS_code\wms-web\src\views\label\print-status-sync.spec.ts`

**Step 1: 写失败测试**
- 断言标签批量 ID、绑定 ID、库存定位 ID 保持字符串

**Step 2: 运行测试验证失败**
- Run: `npm test -- src/views/label/print-status-sync.spec.ts`
- Expected: FAIL 或类型不匹配

**Step 3: 写最小实现**
- 修改选择数组、绑定表单、批量打印与库存查询的 ID 类型

**Step 4: 再跑测试**
- Run: `npm test -- src/views/label/print-status-sync.spec.ts`
- Expected: PASS

**Step 5: 手工验证点**
- 标签绑定、批量打印、库存筛选与详情联动正确

### Task 8: 修复系统、审批、菜单树和角色分配页面

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\approval\config\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\approval\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\config\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\dept\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\menu\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\permission\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\role\components\RoleMenu.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\role\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\supplier\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\user\index.vue`

**Step 1: 写失败检查**
- 先运行构建，收集系统域剩余 ID 报错

**Step 2: 运行检查**
- Run: `npm run build`
- Expected: FAIL，错误集中在树组件、菜单、角色、审批页面

**Step 3: 写最小实现**
- 把树节点、表格编辑态、批量勾选数组、授权数组改为字符串 ID
- 修复 `find`、`includes`、`checkedKeys` 等依赖

**Step 4: 再跑构建**
- Run: `npm run build`
- Expected: PASS 或只剩零星残留报错

**Step 5: 手工验证点**
- 菜单树默认勾选、角色授权、审批配置节点回填正常

### Task 9: 修复 store、layout、router 等公共消费点

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\store\modules\user.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\store\modules\permission.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\composables\menu-layout.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Navbar.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\layouts\components\Sidebar.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\router\index.ts`
- Modify: `d:\Codes\WMS_code\wms-web\src\router\guard.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\store\modules\user.spec.ts`
- Test: `d:\Codes\WMS_code\wms-web\src\layouts\composables\__tests__\menu-layout.spec.ts`

**Step 1: 写失败测试**
- 断言菜单树和用户状态中实体 ID 使用字符串不影响高亮与权限链路

**Step 2: 运行测试验证失败**
- Run: `npm test -- src/store/modules/user.spec.ts src/layouts/composables/__tests__/menu-layout.spec.ts`
- Expected: FAIL 或类型不匹配

**Step 3: 写最小实现**
- 修复根菜单高亮、菜单树检索、用户角色菜单缓存中的 ID 类型

**Step 4: 再跑测试**
- Run: `npm test -- src/store/modules/user.spec.ts src/layouts/composables/__tests__/menu-layout.spec.ts`
- Expected: PASS

**Step 5: 手工验证点**
- 导航高亮、菜单切换、用户菜单缓存正常

### Task 10: 全局残留清扫与最终验证

**Files:**
- Scan: `d:\Codes\WMS_code\wms-web\src\**\*.{ts,vue,d.ts}`
- Verify: `d:\Codes\WMS_code\wms-server\wms-common\src\main\java\com\wms\common\config\JacksonConfig.java`

**Step 1: 写失败检查**
- 搜索残留模式：`id: number`、`Id: number`、`Number(`、`Record<number`、`Map<number`

**Step 2: 运行检查**
- Run: 使用代码搜索工具逐项扫描
- Expected: 找到残留点

**Step 3: 写最小实现**
- 仅对“实体 ID 场景”的残留点做最后修复
- 保留状态码与数值字典的 `number` 键不动

**Step 4: 跑最终验证**
- Run: `npm test`
- Run: `npm run build`
- Run: `mvn -pl wms-common test`
- Expected: 全部通过

**Step 5: 诊断检查**
- 对最近编辑文件运行诊断，确认无新增类型错误

