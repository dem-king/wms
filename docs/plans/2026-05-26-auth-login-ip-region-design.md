# 登录日志 IP 归属地设计

## 1. 背景

当前登录审计链路已经统一写入 `sys_login_log`，且表结构中已有 `login_location` 字段，但认证模块在调用审计服务时一直传入 `null`，导致系统管理端和个人中心只能看到登录 IP，看不到真实登录地点。

本次需求要求使用 `ip2region` 离线库，在不依赖外部在线服务的前提下，为登录成功和登录失败日志补充真实登录地点。

## 2. 目标

### 2.1 本次目标

- 引入 `ip2region` 离线库解析客户端 IP
- 登录成功和登录失败日志都写入真实 `loginLocation`
- 登录地点统一格式为“省市区”
- 内网、本机、无法识别地址时提供稳定兜底值
- 保持登录主流程可用，地点解析失败不影响登录结果

### 2.2 本次不做

- 不接入在线地图或公网 IP 查询服务
- 不修改 `sys_login_log` 表结构
- 不回填历史登录日志地点
- 不扩展系统登录日志查询接口字段
- 不处理操作日志的 IP 地点解析

## 3. 设计原则

- 通用能力下沉：IP 归属地解析放在 `wms-common`，避免耦合在认证模块
- 最小侵入：尽量只补参数透传和解析逻辑，不重构现有登录审计主链路
- 离线优先：只依赖本地 `ip2region.xdb`，避免外部网络依赖
- 异常隔离：解析异常只影响地点字段，不影响登录本身

## 4. 方案选择

### 4.1 方案 A：在认证服务中直接调用 `ip2region`

做法：

- 在 `AuthServiceImpl` 中直接初始化并调用 `ip2region`
- 登录成功/失败时现场解析地点后写日志

优点：

- 改动点少
- 接入速度快

缺点：

- 地理解析能力耦合在认证模块
- 后续其他模块复用成本高
- `AuthServiceImpl` 职责继续变重

### 4.2 方案 B：在通用层提供 IP 地点解析能力

做法：

- 在 `wms-common` 新增 `IpRegionProperties` 和 `IpRegionResolver`
- 认证模块只依赖解析接口，不感知底层 `ip2region`
- `AuthServiceImpl` 在记录登录审计前解析地点并透传

优点：

- 职责边界清晰
- 后续风控、审计、报表都可复用
- 与现有 `IpUtil` 位于同一通用层，结构一致

缺点：

- 需要增加一个公共配置和资源文件
- 首次接入略多一个初始化步骤

### 4.3 方案 C：在日志服务写库前统一解析

做法：

- 在 `SysLoginLogServiceImpl.recordLoginLog(...)` 内部按 IP 自动解析地点

优点：

- 调用方改动最少

缺点：

- 日志服务混入基础设施逻辑
- 如果未来某些调用方已传入自定义地点，职责会冲突
- 测试边界不够清晰

### 4.4 推荐方案

选择方案 B。

原因：

- 符合“通用能力放 `wms-common`”的现有项目结构
- 认证模块只负责登录业务和审计参数组装，不持有第三方库细节
- 未来若监控、风控或操作审计也需要地理解析，可以直接复用

## 5. 目标架构

登录成功链路：

`AuthController -> AuthServiceImpl -> IpRegionResolver -> AuthAuditService -> SysLoginLogService -> sys_login_log`

登录失败链路：

`AuthController -> AuthServiceImpl -> IpRegionResolver -> AuthAuditService -> SysLoginLogService -> sys_login_log`

说明：

- `AuthController` 仍通过 `IpUtil` 获取客户端真实 IP
- `AuthServiceImpl` 在记录登录日志前解析地点
- `AuthAuditServiceImpl` 只负责透传 `loginLocation`
- `SysLoginLogServiceImpl` 只负责持久化，不承担地点推断职责

## 6. 数据与格式设计

### 6.1 输入

- 输入字段：`clientIp`
- 来源：现有 `IpUtil.getIpAddr(HttpServletRequest request)`

### 6.2 输出

- 字段：`loginLocation`
- 存储位置：`sys_login_log.login_location`

### 6.3 格式规则

- 公网 IP：输出“省市区”，如 `广东省深圳市南山区`
- 直辖市：输出区县优先，如 `北京市朝阳区`
- 内网、本机地址：统一输出 `内网IP`
- 解析失败或结果为空：统一输出 `未知`

### 6.4 原始结果清洗规则

`ip2region` 通常返回以 `|` 分隔的地区串。格式化时遵循：

- 过滤 `0`、空串、`内网IP`
- 只保留国家、省、市、区县中的中文地区信息
- 如果存在国家信息且为 `中国`，不额外拼接国家，只保留省市区
- 按顺序拼接并去重，避免出现 `北京市北京市朝阳区`

## 7. 代码改造设计

### 7.1 公共模块

需要修改：

- `wms-server/wms-common/pom.xml`
- `wms-server/wms-common/src/main/java/com/wms/common/config/IpRegionProperties.java`
- `wms-server/wms-common/src/main/java/com/wms/common/util/IpRegionResolver.java`
- `wms-server/wms-common/src/main/resources/ip2region.xdb`
- `wms-server/wms-common/src/test/java/com/wms/common/util/IpRegionResolverTest.java`

改动方向：

- 新增 `ip2region` Maven 依赖
- 新增配置类，支持 classpath 默认库文件和可选外部路径
- 新增解析器，负责初始化 `Searcher`、解析、结果格式化和兜底
- 增加单元测试覆盖内网 IP、空 IP、无法识别 IP、地区串格式化

### 7.2 认证模块

需要修改：

- `wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java`
- `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java`
- `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java`
- `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java`
- `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplAuthFlowTest.java`

改动方向：

- `AuthAuditService.recordLoginLog(...)` 增加 `loginLocation` 参数
- `AuthAuditServiceImpl` 将地点参数透传给 `SysLoginLogService`
- `AuthServiceImpl` 注入 `IpRegionResolver`，在成功/失败登录记录时传入解析后的地点
- 更新现有 Mockito 断言，确保成功/失败链路都会写入真实地点

### 7.3 应用配置

需要修改：

- `wms-server/wms-app/src/main/resources/application.yml`
- `wms-server/wms-app/src/main/resources/application-dev.yml`
- `wms-server/wms-app/src/main/resources/application-prod.yml`

改动方向：

- 新增 `wms.ip-region` 配置段
- 默认启用 classpath 资源文件
- 允许通过环境变量覆盖离线库路径

示例配置：

```yaml
wms:
  ip-region:
    enabled: true
    xdb-path: ${IP2REGION_XDB_PATH:classpath:ip2region.xdb}
```

## 8. 错误处理

- `IpRegionResolver` 初始化失败时记录错误日志，并进入可降级状态
- 单次 IP 解析失败时返回 `未知`
- 内网 IP 不走异常流程，直接返回 `内网IP`
- 登录日志写入失败继续沿用现有异步告警，不阻断认证主链路

## 9. 测试与验证

### 9.1 单元测试

- `IpRegionResolverTest`
  - 内网地址返回 `内网IP`
  - 空 IP 返回 `未知`
  - 原始地区串能格式化为“省市区”
- `AuthAuditServiceImplTest`
  - 验证 `loginLocation` 被正确透传到 `SysLoginLogService.recordLoginLog(...)`
- `AuthServiceImplAuthFlowTest`
  - 登录成功时写入解析后的地点
  - 登录失败时写入解析后的地点

### 9.2 编译验证

- 在 `wms-server` 聚合模块执行测试或编译，确认跨模块依赖、资源打包和配置绑定正常

### 9.3 手工验证

- 使用本机登录，确认 `sys_login_log.login_location` 为 `内网IP`
- 使用可识别公网地址联调时，确认 `sys_login_log.login_location` 写入省市区
- 系统登录日志页面可看到地点字段值

## 10. 风险与约束

- 若离线库文件未随应用打包或部署路径配置错误，地点解析会退化为 `未知`
- 内网地址无法通过 `ip2region` 得到真实地理位置，只能按约定显示 `内网IP`
- 不同版本 `ip2region.xdb` 的行政区结果可能略有差异，测试应聚焦格式化和兜底规则，不对具体公网样例做过强绑定

## 11. 实施顺序

1. 补公共层 `ip2region` 依赖、配置和解析器
2. 先写并更新相关单元测试
3. 改造认证审计接口与实现，透传 `loginLocation`
4. 改造 `AuthServiceImpl`，在登录成功和失败链路中写入真实地点
5. 更新应用配置并验证资源打包
6. 运行定向测试、诊断和聚合编译
