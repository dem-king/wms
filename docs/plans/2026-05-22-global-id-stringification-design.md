# 全仓雪花 ID 字符串化设计

**背景**

当前项目后端主键广泛使用雪花 ID。后端在 JSON 响应中若直接输出 `Long` 数值，前端一旦按 `number` 解析，就会在 JavaScript 安全整数边界之外发生精度丢失，导致下拉选中、详情查询、编辑提交、批量操作、路由参数、树节点关联等链路出现主键错乱。

仓储模块已经暴露并修复了这一问题，但扫描结果表明，`business`、`item`、`label`、`system`、`auth`、`approval` 等模块仍存在大量 `id: number`、`xxxId: number`、`Number(data.id)`、`Number(route.query.id)`、`Record<number, ...>` 等模式，继续保留这些模式会让同类问题在其他模块复发。

**目标**

在不改变业务状态码、枚举值、排序号等数值语义的前提下，统一项目中所有雪花 ID 相关字段的前后端传输与消费方式：

- 后端 REST JSON 中的 `Long` 一律按字符串输出
- 前端所有主键、外键、关联 ID、批量 ID 数组统一改为 `string`
- 删除所有把后端 ID 强制转为 `number` 的逻辑
- 修正所有依赖数值 ID 的比较、排序、Map/Record 键、路由、树组件、选择器和表单回填

**非目标**

- 不修改状态码、启用禁用标志、数量、金额、排序号等真正具有数值语义的字段
- 不改动非 JSON 响应场景的二进制导出、图片流等接口返回格式
- 不做与 ID 精度问题无关的业务重构

**方案对比**

1. 全量字符串化
   - 把所有雪花 ID 统一为字符串
   - 优点：类型系统一致，问题根治
   - 缺点：改动面大，需要分批验证

2. API 兼容层兜底
   - API 层尽量补救，页面尽量少改
   - 优点：改动小
   - 缺点：类型不一致，后续容易再次回归

3. 只修热点链路
   - 只处理已发现的出错页面
   - 优点：速度快
   - 缺点：无法满足全仓彻底修复目标

采用方案 1。

**总体架构**

后端保持 REST JSON `Long -> String` 的全局序列化能力，作为统一输出边界。前端以类型文件为源头，引入统一 `EntityId = string`，再向 API、页面、状态管理、路由与测试扩散，最终保证所有雪花 ID 在浏览器内全程以字符串流转。

本次改造采用“类型优先、API 次之、页面跟进、测试兜底”的分层推进方式。这样可以先让 TypeScript 暴露不一致点，再逐批修复页面和逻辑，降低大面积散改的失控风险。

**模块划分**

1. 后端
   - `wms-common` 中的 Jackson 配置继续统一序列化 `Long` / `long`
   - 验证异常响应、认证响应、分页响应也都走同一套 JSON 序列化链路

2. 前端基础层
   - `src/types/business.d.ts`
   - `src/types/item.d.ts`
   - `src/types/label.d.ts`
   - `src/types/system.d.ts`
   - `src/types/auth.d.ts`
   - 必要时抽出共享 `EntityId` 类型

3. 前端 API 层
   - 去掉 `Number(...)`、`parseInt(...)` 之类的主键转换
   - 所有路径参数、查询参数、请求体 ID 字段改为字符串
   - 修正返回归一化逻辑中的类型假设

4. 前端视图层
   - 表单选择器、编辑回填、详情查询、批量操作
   - 审批配置树、菜单树、角色分配、标签绑定、扫码回填
   - `Map<Record<number, ...>>` 改成字符串键，仅对实体 ID 场景生效

5. 测试与回归
   - 补充关键 API / 页面 / 工具函数回归测试
   - 构建、类型检查、针对性单测、诊断扫描

**关键规则**

1. 实体 ID 统一规则
   - `id`、`userId`、`roleId`、`deptId`、`itemId`、`labelId`、`warehouseId`、`areaId`、`cabinetId`、`binId`、`supplierId`、`configId`、`bizId`、`orderId`、`detailId` 等一律改为 `string`

2. 保持 number 的字段
   - `status`
   - `sortOrder`
   - `row` / `col`
   - `rows` / `cols`
   - `quantity`
   - `amount`
   - 其他明确属于计数、金额、状态码、枚举码的字段

3. 排序与比较
   - 原先用 `left.id - right.id` 的地方改为 `left.id.localeCompare(right.id)`
   - 原先使用 `Number(id)` 做排序回退的地方，改为字符串比较
   - 仅当字段本质是数值序号时才继续使用减法

4. 路由与组件
   - `route.query.xxxId`、`route.params.id` 默认按字符串读取
   - 组件 `v-model`、`el-select`、树节点 `node-key`、穿梭框/勾选数组中的实体 ID 统一为字符串

**风险点**

1. 树组件与选择组件
   - 例如菜单树、审批节点树、角色菜单分配，如果节点键类型不统一，会导致高亮、默认勾选、回填失败

2. 批量 ID 数组
   - 如标签打印、批量删除、角色菜单授权，如果仍使用 `number[]`，将继续产生错误请求

3. API 归一化
   - 业务单据 API 当前存在 `Number(...)` 强转，是最高风险点，必须优先移除

4. 测试快照与断言
   - 许多测试当前默认 `id` 为数字，需要同步切换为字符串

**分批实施策略**

第一批：基础类型与 API
- 先统一 `types` 和 `api`
- 移除所有显式的 ID 数值强转

第二批：业务与物品域
- `business`
- `item`
- `label`
- `stock`

第三批：系统与认证域
- `system`
- `auth`
- `approval`
- store / layout / router 中的关联消费点

第四批：全仓残留清扫与验证
- 全局扫描剩余的 `id: number`、`xxxId: number`
- 扫描 `Number(id)`、`Record<number, ...>`、`Map<number, ...>`
- 跑构建和回归测试

**验收标准**

- 前端不再把雪花 ID 定义或消费为 `number`
- 前端不再存在针对实体 ID 的 `Number(...)` 强转
- 所有主键/外键/批量 ID 数组在前端全程以字符串流转
- 后端 Long ID 的 REST JSON 输出保持字符串化
- 前端构建通过，关键单测通过，全局诊断无新增类型错误
