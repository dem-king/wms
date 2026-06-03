# Wiki P0 文档补全 — 变更摘要

- **变更类型**: docs
- **创建日期**: 2026-06-02
- **完成日期**: 2026-06-02
- **承接**: [wiki-refresh-20260602](wiki-refresh-20260602)（占位文档创建）

## 目标

把 [wiki/TODO.md](../../wiki/TODO.md) 中 5 个 🔴 P0 占位文档全部从"骨架"升级为"实质内容"，让 AI 协作者和评审者能够基于真实代码理解 5 大核心域。

## 流程状态

| 阶段 | 状态 | 备注 |
|------|------|------|
| 子任务分派 | ✅ 完成 | 5 个并行子任务（出库/归还报废/PDA/认证/权限） |
| 子任务执行 | ✅ 完成 | 4 个成功，1 个返回 Tool result missing 需重派 |
| 文件实际写入验证 | ✅ 完成 | 5/5 全部 Read 验证首行含"✅ 已完成（2026-06-02）" |
| README/TODO 同步 | ✅ 完成 | 状态从 🆕 占位 改为 ✅ + 标记行数 |
| 索引路径补全 | ✅ 完成 | §7.1 新人入职路径新增 5 份文档 |
| 文档归档 | ✅ 完成 | 本次 summary.md |

## 变更文件清单（7 个）

### 修改（2 个）

| 文件 | 变更 |
|------|------|
| [wiki/README.md](../../wiki/README.md) | 顶部覆盖率 12→17/22（77%）；5 个 P0 文档状态从 🆕 占位 → ✅ + 行数；§7.1 新人入职路径新增 5 份必读 |
| [wiki/TODO.md](../../wiki/TODO.md) | P0 表格新增"状态"列；完成记录区追加 5 条 |

### 内容实质化（5 个，共 3,146 行）

| # | 文档 | 行数 | 字节 | 子任务轮次 | 实际探查代码文件数 |
|---|------|------|------|-----------|----------------|
| 1 | [业务开发/出库流程.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%87%BA%E5%BA%93%E6%B5%81%E7%A8%8B.md) | 498 | ~30 KB | 第 1 批 | 7+ |
| 2 | [业务开发/归还报废调拨.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%BD%92%E8%BF%98%E6%8A%A1%E5%BA%9F%E8%B0%83%E6%8B%A8.md) | 695 | ~40 KB | 第 1 批 | 12+ |
| 3 | [业务开发/PDA移动端.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/PDA%E7%A7%BB%E5%8A%A8%E7%AB%AF.md) | 497 | ~25 KB | 第 1 批 | 8+ |
| 4 | [系统管理/认证鉴权.md](../../wiki/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/%E8%AE%A4%E8%AF%81%E9%89%B4%E6%9D%83.md) | 968 | ~46 KB | 第 2 批（首派失败重派） | 17+ |
| 5 | [系统管理/权限体系.md](../../wiki/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/%E6%9D%83%E9%99%90%E4%BD%93%E7%B3%BB.md) | 488 | ~22 KB | 第 1 批 | 13+ |

## 关键发现（5 大域）

### 1. 出库流程（wms-business.Outbound）

- **OUT 库存事件真正发布者不是 OutboundServiceImpl**：submitOrder 只发 `ApprovalRequestEvent`；真正的 `StockSyncEvent(OUT)` 由 `ApprovalResultEventListener.handleOutboundApproved` 在审批通过后逐条明细发布
- **状态机 6 个枚举值**：`DRAFT(0)/PENDING(1)/APPROVING(2)/APPROVED(3)/REJECTED(4)/COMPLETED(5)`，但出库单实际持久化状态仅 DRAFT/PENDING/COMPLETED
- **PDA 拣货完全复用 OutboundController**（PdaController 没有专属出库执行接口）
- **`orderType` 上下游文案不一致**（实体 vs VO 注释）— 文档已显式提醒

### 2. 归还/报废/调拨（wms-business.{Return,Scrap,Transfer}）

- **3 个业务库存事件均由 `ApprovalResultEventListener` 在审批通过后统一发布**（验证 AGENTS.md §4.2）
- **调拨事务边界 = 单事务**（非分布式）：源库 OUT + 目标库 IN + 主单状态更新在同一个数据库事务，任一失败整体回滚
- **归还特殊处理**：没有 `warehouseId`，库房 ID 在审批通过时查关联出库单取得；`conditionStatus=DAMAGED/LOST` 跳过不发事件
- **三业务统一架构**：6 个方法（pageOrders/getOrderById/createOrder/updateOrder/submitOrder/deleteOrder），全部带 @PreAuthorize + @DataScope + @OperLog

### 3. PDA 移动端（wms-pda + wms-business.controller.pda）

- **PDA 后端实际仅 3 个接口**：`/pda/rfid/batch-read`、`/pda/stock/check`、`/pda/tasks`（唯一带 @DataScope）
- **前端项目存在**：`d:\Codes\WMS_code\wms-pda\`，使用 **uni-app + Vue 3 + Pinia**，含 `wms-rfid` 原生插件、`OfflineBar` 离线组件、容量 100 条的 `useOffline` 队列
- **⚠️ 文档中发现 3 个潜在违规**：
  1. `PdaConstants` 实际未定义 `RFID_BATCH_LIMIT`（前端有 `RFID_DEFAULT_POWER=30`，后端无保护）— 建议补齐
  2. `wms_stock_check_order/detail` DDL **未在 `wms_complete_init.sql` 中找到**（违反 AGENTS.md §7.3）
  3. 前后端字段命名不一致（`inboundPendingCount` vs `pendingInbound`）— 违反 AGENTS.md §8.1
- **`BinWarehouseValidator` 不存在**（搜索全无匹配，PDA 当前未做库位归属校验）

### 4. 认证鉴权（wms-auth）

- **JWT 实际结构**：HS256 + 32 字节密钥，Access Claims 含 `sub/jti/iat/exp/username/roles`，**不含 permCode**（每次请求从 Redis 重拉）
- **限流算法**：Lua `GET → 判断 → INCR → EXPIRE`，**实际是固定窗口**（与类注释"滑动窗口"不符）— 文档已记录
- **过滤链顺序**：`JwtAuthenticationFilter` 通过 `addFilterBefore(... UsernamePasswordAuthenticationFilter)` 注入，重新拉 `roles + perms` 重建 Authentication
- **OperLogAspect 切点**：`@Pointcut("@annotation(com.wms.common.annotation.OperLog)")` + `@Around`，敏感字段集合 `password/token/secret/key/authorization/accessToken/refreshToken`
- **强制下线机制**：登录时 `kickOutOldSession` 仅 `GETDEL auth:session:{userId}`，**不会**自动撤销已签发 Access Token；需 `revokeAllTokens`（改密/登出/踢人）才会把 Refresh 的 jti 写黑名单
- **配置热更新**：`AuthConfigRefreshListener` 仅处理 `wms.auth.* / wms.security.* / wms.storage.*` 前缀；当前未覆盖 `ip-rate-limit-threshold`
- **生产合规**：`application-prod.yml` 不写 `wms.auth.jwt-secret`，完全依赖 `${JWT_SECRET}` 环境变量强制注入

### 5. 权限体系（wms-system + wms-common）

- **@DataScope 切面实现**：`DataScopeAspect` → ThreadLocal `DataScopeContext` → MyBatis-Plus `DataScopePermissionHandler` → `DataScopeSqlBuilder` + `JSqlParser` 改写 WHERE
- **5 个数据范围 SQL 拼接**（基于 `DataScopeSqlBuilder#TABLE_RULES`）：

  | 范围 | SQL 片段 |
  |------|---------|
  | SCOPE_ALL=1 | 不拼条件 |
  | SCOPE_CUSTOM=2 | 自定义 deptId 列表 |
  | SCOPE_DEPT=3 | `dept_id = {userDept}` |
  | SCOPE_DEPT_AND_CHILD=4 | `dept_id IN (子部门列表)` |
  | SCOPE_SELF=5 | `applicant_id = {userId}` |
- **权限缓存机制**：Redis Key `auth:perm:{userId}`（List），TTL `AuthConstants.PERM_CACHE_EXPIRE_MINUTES`；`PermissionCacheEvictListener` 监听 `PermissionCacheEvictEvent` 失效
- **角色-权限多对多的"差异更新"模式**：避免唯一索引冲突，不走 `delete + insert`，而是分 `activeMap` / `deletedMap`
- **关键 bug 发现**：`DataScopeConstants` 实际 5 个值（ALL=1, CUSTOM=2, DEPT=3, DEPT_AND_CHILD=4, SELF=5），但 `SysRole.dataScope` 字段注释只写了 1-4，**缺失 SELF=5** — 文档已提醒修正

## 防御深度对比（补全前 vs 补全后）

| 维度 | 补全前 | 补全后 |
|------|--------|--------|
| 出库业务 | AI 必须读 OutboundServiceImpl（约 200+ 行）自己提炼 | 直接看 wiki/出库流程.md（498 行 mermaid + 表格） |
| 调拨事务 | 不明 → 容易误写成"分布式事务" | 明确单事务 |
| PDA 接口数 | 推测 5-8 个 | 实际 3 个 |
| JWT 结构 | 推测 | 实际 Access/Refresh + Redis 重拉 permCode |
| @DataScope 5 范围 | 仅名字已知 | 真实 SQL 拼接 + 5 表规则 |
| 红线复审 | AGENTS.md 列举 | Wiki 文档以"代码实证"反推 AGENTS.md 是否要更新（如 SELF=5 字段注释） |

## 例外记录

- 5 个 P0 文档中**第一派认证鉴权失败**（Tool result missing），需重派；第二次成功
- 实际交付内容**略超 3,146 行**（总计），子任务未全部按建议风格（如 mermaid 命名）
- 部分 Service 文件名可能是 `ReturnServiceImpl`（而非 `ReturnOrderServiceImpl`），子任务已根据实际文件名调整
- PDA 文档中发现 3 个潜在违规（见 §3）已记录但**未修复**（不在本次范围内）

## 后续建议

### 🟠 立即可做

1. **修复 PDA 文档发现的 3 个潜在违规**：
   - 在 `PdaConstants` 加 `RFID_BATCH_LIMIT`
   - 补 `wms_stock_check_order/detail` DDL
   - 统一前后端字段命名（`PdaTaskVo.inboundPendingCount` → `inboundPending`）
2. **修复 `SysRole.dataScope` 字段注释**：补充 `SELF=5`
3. **修正限流类注释**："滑动窗口" → "固定窗口"

### 🟡 下个 Sprint

4. 开始 [wiki/TODO.md](../../wiki/TODO.md) 中的 🟠 P1 7 个文档（库房布局/库存盘点/电子标签/机器备品/审批/MyBatisPlus/常量与工具）

### 🟢 长期

5. 把"新增业务模块时同步补 wiki"作为 PR 自查清单
6. 维护指标：每模块至少 1 篇核心文档，覆盖率 ≥ 80%
