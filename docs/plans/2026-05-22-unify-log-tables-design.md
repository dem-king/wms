# 日志表统一设计

## 1. 背景

当前项目的日志能力存在两套实现：

- 登录日志：`wms-auth` 写入 `auth_login_log`，系统管理端查询 `sys_login_log`
- 操作日志：业务模块经 `@OperLog` + AOP 写入 `sys_oper_log`，认证模块登出单独写入 `auth_oper_log`

这导致两个直接问题：

1. 登录日志写查链路断裂，系统页面无法稳定看到真实登录数据
2. 操作日志存在双表并存，认证审计数据无法统一纳入系统日志视图

本次优化聚焦“表统一”，优先解决数据链路和模型重复问题，不扩展到更大的日志治理范围。

## 2. 目标

### 2.1 本次目标

- 保留 `sys_login_log` 和 `sys_oper_log` 作为唯一标准日志表
- 将认证模块的登录日志写入从 `auth_login_log` 切换到 `sys_login_log`
- 将认证模块的登出操作日志写入从 `auth_oper_log` 切换到 `sys_oper_log`
- 删除 `auth_login_log` 和 `auth_oper_log` 对应实体、Mapper 和初始化 DDL
- 为存量环境提供迁移脚本，支持旧数据并入 `sys_*` 表

### 2.2 本次不做

- 不新增 IP 地理位置解析
- 不引入第三方 User-Agent 精细解析库
- 不扩展日志查询前端能力
- 不实现日志归档、定时清理、冷热分层
- 不在本轮处理参数脱敏与响应脱敏增强

## 3. 设计原则

- 单一事实来源：登录日志与操作日志都只保留一套标准表
- 最小改动优先：前端查询接口和业务 Controller 尽量不动
- 兼容现有审计链路：保留业务模块 `@OperLog -> OperLogAspect -> sys_oper_log` 主链路
- 迁移可回退：先支持旧数据迁移，再考虑删除旧表

## 4. 方案选择

### 4.1 方案 A：保留双表并做双写

做法：

- 认证登录同时写 `auth_login_log` 和 `sys_login_log`
- 认证登出同时写 `auth_oper_log` 和 `sys_oper_log`

优点：

- 代码改动小
- 对旧表兼容最好

缺点：

- 双写增加一致性风险
- 冗余模型继续存在
- 后续维护和查询口径仍不统一

### 4.2 方案 B：统一到 `sys_*` 标准表

做法：

- 认证模块直接依赖系统日志实体与服务
- 登录统一写 `sys_login_log`
- 登出统一写 `sys_oper_log`
- 清理 `auth_*_log` 相关代码与 DDL

优点：

- 直接消除重复模型
- 查询与写入口径统一
- 与当前系统日志页面天然一致

缺点：

- 涉及跨模块依赖调整
- 需要补齐 `sys_login_log.user_id`
- 需要编写迁移脚本

### 4.3 方案 C：先抽象审计中心，再统一底层表

做法：

- 抽象一层审计门面服务
- 各模块只依赖统一审计接口
- 底层逐步切换到 `sys_*`

优点：

- 分层更清晰
- 后续扩展登录、操作、审计事件更灵活

缺点：

- 设计和改造范围明显超出本轮目标
- 交付周期长

### 4.4 推荐方案

选择方案 B。

原因：

- 能一次性解决当前最核心的“写 auth / 查 sys”问题
- 与现有 [2026-05-22-unify-log-tables.md](file:///d:/Codes/WMS_code/docs/plans/2026-05-22-unify-log-tables.md) 方向一致
- 对前端和业务模块影响最小，适合本轮快速收敛

## 5. 目标架构

### 5.1 登录日志链路

调整前：

`AuthController -> AuthServiceImpl -> AuthAuditService -> auth_login_log`

调整后：

`AuthController -> AuthServiceImpl -> AuthAuditService -> SysLoginLogService -> sys_login_log`

### 5.2 操作日志链路

业务主链路保持不变：

`@OperLog -> OperLogAspect -> SysOperLogService -> sys_oper_log`

认证登出链路调整为：

`AuthController.logout -> AuthServiceImpl.logout -> AuthAuditService -> SysOperLogService -> sys_oper_log`

### 5.3 查询链路

- 登录日志继续由系统管理接口查询 `sys_login_log`
- 操作日志继续由系统管理接口查询 `sys_oper_log`
- 前端接口路径和 VO 输出保持不变

## 6. 数据模型设计

### 6.1 `sys_login_log`

在现有基础上新增：

- `user_id BIGINT DEFAULT NULL COMMENT '用户ID'`
- 新增索引 `idx_user_id`

保留现有字段：

- `username`
- `login_ip`
- `login_location`
- `browser`
- `os`
- `status`
- `fail_reason`
- `login_time`

设计说明：

- `user_id` 用于系统管理查询、个人资料页“最近登录信息”、后续按用户维度统计
- `login_location` 暂时允许为空，不在本轮补 IP 解析
- `status` 继续沿用字符串值，统一为 `SUCCESS / FAIL / LOGOUT`

### 6.2 `sys_oper_log`

本轮保持结构不变，不新增字段。

设计说明：

- 当前 `sys_oper_log` 已能满足业务写操作审计
- 认证登出改写到该表后，用 `module=auth` 标识来源
- `type` 暂按现有登出语义写 `LOGOUT`

## 7. 代码改造设计

### 7.1 SQL 与初始化脚本

需要修改：

- `database/sys_log_tables.sql`
- `database/wms_full_init.sql`
- `database/auth_ddl.sql`

改动方向：

- 为 `sys_login_log` 增加 `user_id`
- 从全量初始化脚本移除 `auth_login_log`、`auth_oper_log`
- 清理 `auth_ddl.sql` 中旧日志表定义，保留说明注释

### 7.2 系统模块

需要修改：

- `wms-server/wms-system/.../entity/SysLoginLog.java`
- `wms-server/wms-system/.../service/SysLoginLogService.java`
- `wms-server/wms-system/.../service/impl/SysLoginLogServiceImpl.java`

改动方向：

- `SysLoginLog` 新增 `userId`
- `SysLoginLogService` 提供统一写入登录日志的方法
- `SysLoginLogServiceImpl` 支持按 `userId` 查询最近一次成功登录

### 7.3 认证模块

需要修改：

- `wms-server/wms-auth/.../service/AuthAuditService.java`
- `wms-server/wms-auth/.../service/impl/AuthAuditServiceImpl.java`
- `wms-server/wms-auth/.../service/impl/AuthServiceImpl.java`

改动方向：

- `AuthAuditService` 从依赖认证日志实体切换到系统日志实体
- `AuthAuditServiceImpl` 不再直接写 `auth_*_log`，改委托系统日志服务
- `AuthServiceImpl` 的登录成功、登录失败、登出审计统一通过系统日志服务完成

### 7.4 删除内容

计划删除：

- `AuthLoginLog`
- `AuthOperLog`
- `AuthLoginLogMapper`
- `AuthOperLogMapper`

删除原因：

- 统一表后这些模型不再有落地表可用
- 保留会误导后续开发继续向 `auth_*_log` 写入

## 8. 数据迁移设计

新增迁移脚本：

- `database/migration/V20260522__unify_log_tables.sql`

迁移步骤：

1. 给 `sys_login_log` 增加 `user_id` 和索引
2. 将 `auth_login_log` 数据迁移到 `sys_login_log`
3. 将 `auth_oper_log` 数据按最小必要字段迁移到 `sys_oper_log`
4. 旧表默认先保留，不在迁移脚本中直接强删

迁移策略说明：

- `auth_login_log.login_result` 需要映射到 `sys_login_log.status`
- `auth_login_log.user_agent` 只能部分映射到 `browser` / `os`，本轮允许降级处理
- `auth_oper_log` 与 `sys_oper_log` 字段不完全一致，优先保留审计核心字段
- 删除旧表建议放在人工确认迁移成功后执行，而不是默认自动执行

## 9. 错误处理

- 日志写入继续采用异步执行，避免影响主业务链路
- 日志写入失败仅记录告警，不中断登录、登出和业务写操作
- 迁移脚本执行前必须备份生产数据

## 10. 测试与验证

### 10.1 编译验证

- `wms-server` 执行编译，确认跨模块依赖调整后可通过

### 10.2 功能验证

- 登录成功后，`sys_login_log` 有新增记录
- 登录失败后，`sys_login_log` 有失败记录
- 登出后，`sys_oper_log` 有 `auth` 模块对应记录
- 系统日志查询接口可正常返回数据
- 用户个人资料中的最近登录信息可正常获取

### 10.3 回归范围

- 业务模块已有 `@OperLog` 的写接口
- 认证模块登录、登出、个人资料查询

## 11. 风险与约束

- `wms-auth` 依赖 `wms-system` 日志实体与服务，需确认当前模块依赖方向允许
- 状态值目前仍有中文和英文并存问题，本轮不额外治理
- `login_location` 本轮可能为空，需要接受该字段短期内信息不完整

## 12. 实施顺序

1. 修改日志表结构与初始化脚本
2. 修改系统登录日志实体与服务
3. 修改认证审计服务和认证业务实现
4. 删除 `auth_*_log` 相关实体与 Mapper
5. 新增迁移脚本
6. 编译并执行关键链路验证
