# 鉴权无感刷新优化设计

**日期：** 2026-05-22

**目标：** 在保持现有“记住我仅记住用户名”行为不变的前提下，完善双 Token 无感刷新能力，补齐刷新后的 Claim 丢失、登出后 Refresh Token 仍可继续刷新、仅靠 401 被动刷新导致的体验抖动等问题。

## 现状

- 后端已支持登录下发 `accessToken` 和 `refreshToken`，并提供 `/auth/token/refresh` 刷新接口。
- 前端已实现 `401 -> 调用 refreshToken -> 更新 token -> 重放原请求` 的兜底无感刷新。
- 当前刷新逻辑存在两个关键缺陷：
  - `refreshToken` 未携带 `username`、`roles`，但刷新时却尝试从 `refreshToken` 中读取这些 Claim，导致新签发的 `accessToken` 可能出现 Claim 丢失。
  - 登出时只撤销当前 `accessToken`，没有同步撤销当前 `refreshToken`，导致已登出的 Refresh Token 在有效期内理论上仍可继续换取新 Token。
- 前端当前没有主动预刷新能力，`accessToken` 过期后的首个请求需要先走一次 401 才会触发刷新，用户体验存在抖动。
- 登录页“记住我”只保存用户名到本地存储，不参与 Token 生命周期和登录态续期，本次保持不变。

## 方案

### 方案选型

- 采用方案 B：`最小修复 + 预刷新`。
- 保留现有 401 兜底刷新链路，避免一次性重构鉴权协议。
- 在此前提下补齐后端 Token 数据与撤销能力，并在前端增加轻量级预刷新调度，降低首个过期请求命中 401 的概率。

### 后端设计

#### 刷新时重新装配用户上下文

- `TokenServiceImpl.refreshToken` 不再依赖 `refreshToken` 中不存在的 `username`、`roles` Claim。
- 刷新时仅使用 `refreshToken` 的 `subject` 解析出 `userId`，再通过已有服务查询当前用户信息和角色信息，重新签发新的 Token 对。
- 新生成的 `accessToken` 继续保留 `username`、`roles` Claim，保证后续认证过滤器和业务读取逻辑稳定。

#### 引入 Refresh Token 可撤销信息

- 为 `refreshToken` 建立可撤销关联信息，至少能在登出时定位并撤销当前会话对应的 Refresh Token。
- 登录成功后，创建会话时同时记录当前 `accessToken jti` 与 `refreshToken jti` 的关联关系。
- 用户登出时：
  - 撤销当前 `accessToken`
  - 撤销当前会话对应的 `refreshToken`
  - 清理会话关联记录

#### 兼容现有接口

- 保持 `/auth/login`、`/auth/token/refresh`、`/auth/logout` 的请求/响应结构不变。
- `LoginResp` 与 `TokenResp` 仍返回 `accessToken`、`refreshToken`、`expiresIn`，避免前端 API 类型大范围改动。

### 前端设计

#### 过期时间持久化

- 在现有 `wms_token`、`wms_refresh_token` 基础上，新增 `accessTokenExpiresAt` 的本地持久化。
- 登录成功、401 刷新成功、预刷新成功后，统一更新：
  - `accessToken`
  - `refreshToken`
  - `accessTokenExpiresAt`

#### 预刷新调度

- 增加轻量级鉴权调度器，根据 `accessTokenExpiresAt` 计算下次预刷新时间。
- 默认在 `accessToken` 到期前一段固定安全窗口内主动刷新，避免用户的第一个业务请求落在过期边界上。
- 预刷新调度只维护一个定时器，重复登录、重复刷新时先清理旧定时器再重建，避免并发或重复执行。

#### 401 兜底刷新保留

- 继续保留当前响应拦截器中的 401 刷新逻辑，作为预刷新失效时的兜底方案。
- 若预刷新正在进行，业务请求遇到 401 时应复用同一刷新 Promise，避免同时发起多次刷新请求。
- 若刷新失败，则统一清理本地鉴权状态并跳转登录页。

#### 保持“记住我”不变

- 登录页仍只记住用户名。
- 不新增 `rememberMe` 请求字段，不改变 Token 有效期和本地鉴权存储策略。

### 数据流

#### 登录成功

1. 后端返回 `accessToken`、`refreshToken`、`expiresIn`
2. 前端持久化 Token 和绝对过期时间
3. 前端启动预刷新定时器

#### 预刷新成功

1. 定时器触发调用 `/auth/token/refresh`
2. 前端更新 Token 和绝对过期时间
3. 重建下一轮预刷新定时器

#### 访问令牌意外过期

1. 业务请求返回 401
2. 前端走现有拦截器兜底刷新
3. 刷新成功后重放原请求
4. 若刷新失败则清理鉴权并跳转登录页

#### 退出登录

1. 前端调用 `/auth/logout`
2. 后端撤销当前 `accessToken` 与对应 `refreshToken`
3. 前端清理本地 Token、过期时间和预刷新定时器

### 错误处理

- `refreshToken` 无效、过期、已撤销时，后端返回明确 401 错误码与消息。
- 前端遇到刷新失败时不做静默吞错，统一执行登出清理，避免页面停留在“看似登录、实际不可用”的状态。
- 预刷新失败后若用户仍停留在业务页，下一次请求会触发统一跳转逻辑，不再尝试无限刷新重试。

### 测试策略

- 后端补充 `TokenServiceImpl` 相关测试，覆盖：
  - 刷新时重新装配用户上下文
  - 登出后 Refresh Token 不可再次刷新
  - Refresh Token 被撤销后的异常路径
- 前端补充鉴权工具与请求层测试，覆盖：
  - 登录后写入过期时间
  - 预刷新成功后更新时间与定时器
  - 401 兜底刷新仍然可用
  - 刷新失败时清理本地状态

## 不做范围

- 不调整“记住我”为记住登录态或延长 Token 有效期。
- 不引入基于用户活跃度的 30 分钟无操作倒计时。
- 不改为后端在每个业务响应头中回传新 Token 的滑动续期模式。
- 不做多端会话列表、设备管理、登录设备踢出页面等扩展功能。
