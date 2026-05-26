# 日志表统一实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 统一项目中的登录日志和操作日志落表，消除 `auth_*_log` 与 `sys_*_log` 双轨并存问题，确保认证模块与系统管理端使用同一套日志数据源。

**Architecture:** 保留 `sys_login_log` 和 `sys_oper_log` 作为唯一标准日志表。认证模块通过 `AuthAuditService` 委托系统日志服务完成登录、失败、登出审计；业务模块继续沿用 `@OperLog + OperLogAspect` 主链路。数据库初始化脚本和迁移脚本同步收敛到 `sys_*` 表。

**Tech Stack:** Java 17, Spring Boot, MyBatis-Plus, MySQL 8.0, JUnit 5, Mockito

---

### Task 1: 切换个人资料测试到系统登录日志模型

**Files:**
- Modify: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplProfileTest.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java`

**Step 1: 先写失败测试**

把 `AuthServiceImplProfileTest` 中的 `AuthLoginLog` 替换为 `SysLoginLog`，并保持“最近一次成功登录信息”断言不变。

```java
SysLoginLog loginLog = new SysLoginLog();
loginLog.setLoginIp("127.0.0.1");
loginLog.setLoginTime(LocalDateTime.of(2026, 5, 21, 16, 30, 0));
when(authAuditService.getLatestSuccessLoginLog(1001L)).thenReturn(loginLog);
```

**Step 2: 运行测试确认失败**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: 编译失败，提示 `AuthAuditService.getLatestSuccessLoginLog` 返回类型仍为 `AuthLoginLog`，与测试中的 `SysLoginLog` 不兼容。

**Step 3: 写最小实现**

修改 `AuthAuditService` 和 `AuthServiceImpl`：
- `getLatestSuccessLoginLog(Long userId)` 返回类型改为 `SysLoginLog`
- `AuthServiceImpl` 改用 `SysLoginLog` 构建 `LastLoginInfoVo`
- 清理 `AuthLoginLog` 导入

关键实现示例：

```java
@Override
public AuthProfileVo getCurrentProfile() {
    Long userId = getCurrentUserId();
    SysUserVo user = sysUserService.getById(userId);
    SysLoginLog lastLoginLog = authAuditService.getLatestSuccessLoginLog(userId);
    ...
}

private LastLoginInfoVo buildLastLoginInfo(SysLoginLog loginLog) {
    if (loginLog == null) {
        return null;
    }
    LastLoginInfoVo lastLoginInfo = new LastLoginInfoVo();
    lastLoginInfo.setLoginIp(loginLog.getLoginIp());
    lastLoginInfo.setLoginTime(loginLog.getLoginTime());
    return lastLoginInfo;
}
```

**Step 4: 重新运行测试确认通过**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: `BUILD SUCCESS`

**Step 5: 提交**

```bash
git add wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplProfileTest.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java
git commit -m "refactor: 切换认证资料查询到系统登录日志"
```

---

### Task 2: 为认证审计服务补系统日志委托测试并实现登录日志统一写入

**Files:**
- Create: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java`
- Modify: `wms-server/wms-system/src/main/java/com/wms/system/domain/entity/SysLoginLog.java`
- Modify: `wms-server/wms-system/src/main/java/com/wms/system/service/SysLoginLogService.java`
- Modify: `wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysLoginLogServiceImpl.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java`

**Step 1: 先写失败测试**

新增 `AuthAuditServiceImplTest`，至少覆盖两件事：
- `recordLoginLog(...)` 会委托 `SysLoginLogService.recordLoginLog(...)`
- `getLatestSuccessLoginLog(...)` 会委托 `SysLoginLogService.getLatestSuccessLoginLog(...)`

```java
verify(sysLoginLogService).recordLoginLog(
        1001L,
        "admin",
        "127.0.0.1",
        null,
        "Chrome",
        "Windows",
        "SUCCESS",
        null
);
verify(sysLoginLogService).getLatestSuccessLoginLog(1001L);
```

**Step 2: 运行测试确认失败**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: 编译失败，提示 `SysLoginLogService` 尚未提供 `recordLoginLog` 或 `getLatestSuccessLoginLog` 所需签名。

**Step 3: 写最小实现**

实现以下改动：
- `SysLoginLog` 新增 `userId`
- `SysLoginLogService` 增加 `recordLoginLog(...)` 和 `getLatestSuccessLoginLog(Long userId)`
- `SysLoginLogServiceImpl` 实现插入和按 `userId + SUCCESS` 查询最近记录
- `AuthAuditServiceImpl` 改为委托 `SysLoginLogService`

关键实现示例：

```java
@Async
@Override
public void recordLoginLog(Long userId, String username, String loginIp,
                           String loginLocation, String browser, String os,
                           String status, String failReason) {
    SysLoginLog loginLog = new SysLoginLog();
    loginLog.setUserId(userId);
    loginLog.setUsername(username);
    loginLog.setLoginIp(loginIp);
    loginLog.setLoginLocation(loginLocation);
    loginLog.setBrowser(browser);
    loginLog.setOs(os);
    loginLog.setStatus(status);
    loginLog.setFailReason(failReason);
    loginLog.setLoginTime(LocalDateTime.now());
    sysLoginLogMapper.insert(loginLog);
}
```

**Step 4: 重新运行测试确认通过**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: `BUILD SUCCESS`

**Step 5: 提交**

```bash
git add wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java \
        wms-server/wms-system/src/main/java/com/wms/system/domain/entity/SysLoginLog.java \
        wms-server/wms-system/src/main/java/com/wms/system/service/SysLoginLogService.java \
        wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysLoginLogServiceImpl.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java
git commit -m "feat: 统一认证登录日志到系统日志表"
```

---

### Task 3: 切换认证登录成功、失败、登出链路到系统日志表

**Files:**
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java`

**Step 1: 先写失败测试**

在 `AuthAuditServiceImplTest` 中再补一条登出操作测试，要求 `recordOperLog(...)` 委托 `SysOperLogService.asyncSave(...)`；如果当前测试文件未覆盖，可直接新增一个断言用例。

```java
verify(sysOperLogService).asyncSave(operLog);
```

**Step 2: 运行测试确认失败**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: 失败，提示当前实现仍依赖 `AuthOperLogMapper`，无法满足对 `SysOperLogService` 的委托断言。

**Step 3: 写最小实现**

在 `AuthServiceImpl` 中：
- 登录成功时调用 `authAuditService.recordLoginLog(...)`
- 登录失败时调用 `authAuditService.recordLoginLog(...)`
- 登出时构造 `SysOperLog`，写入 `SysOperLogService`

建议实现片段：

```java
private void recordLoginSuccess(String username, Long userId, String ip, String ua) {
    UserAgentParser.UaInfo uaInfo = UserAgentParser.parse(ua);
    authAuditService.recordLoginLog(userId, username, ip, null,
            uaInfo.getBrowser(), uaInfo.getOs(), "SUCCESS", null);
}

private void recordLoginFail(String username, Long userId, String ip, String ua, String reason) {
    UserAgentParser.UaInfo uaInfo = UserAgentParser.parse(ua);
    authAuditService.recordLoginLog(userId, username, ip, null,
            uaInfo.getBrowser(), uaInfo.getOs(), "FAIL", reason);
}
```

登出日志写法：

```java
SysOperLog operLog = new SysOperLog();
operLog.setOperatorId(userId);
operLog.setOperatorName(username);
operLog.setModule("auth");
operLog.setType("LOGOUT");
operLog.setDesc("用户登出");
operLog.setStatus("SUCCESS");
operLog.setOperTime(LocalDateTime.now());
authAuditService.recordOperLog(operLog);
```

如仓库中不存在 `UserAgentParser`，在 `wms-server/wms-common/src/main/java/com/wms/common/util/UserAgentParser.java` 新增一个轻量解析工具，并为其补一个小型常量级测试；若已有同类工具，则直接复用，避免重复建设。

**Step 4: 重新运行测试确认通过**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest,AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: `BUILD SUCCESS`

**Step 5: 提交**

```bash
git add wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java \
        wms-server/wms-common/src/main/java/com/wms/common/util/UserAgentParser.java \
        wms-server/wms-common/src/test/java/com/wms/common/util/UserAgentParserTest.java
git commit -m "refactor: 统一认证登录与登出审计链路"
```

---

### Task 4: 收敛数据库初始化脚本与迁移脚本

**Files:**
- Modify: `database/sys_log_tables.sql`
- Modify: `database/wms_full_init.sql`
- Modify: `database/auth_ddl.sql`
- Create: `database/migration/V20260522__unify_log_tables.sql`

**Step 1: 先写迁移和初始化变更**

完成以下 SQL 调整：
- `sys_login_log` 增加 `user_id` 字段和 `idx_user_id`
- `wms_full_init.sql` 只保留 `sys_login_log` 和 `sys_oper_log`
- `auth_ddl.sql` 删除旧日志表定义，改为说明注释
- 新增迁移脚本，把 `auth_login_log` / `auth_oper_log` 数据搬迁到 `sys_*`

迁移脚本核心片段：

```sql
ALTER TABLE sys_login_log
ADD COLUMN user_id BIGINT DEFAULT NULL COMMENT '用户ID' AFTER id,
ADD INDEX idx_user_id (user_id);

INSERT INTO sys_login_log (
    id, user_id, username, login_ip, browser, os, status, fail_reason, login_time,
    del_flag, create_time, create_by, update_time, update_by
)
SELECT
    id,
    user_id,
    username,
    login_ip,
    SUBSTRING(user_agent, 1, 128),
    'Unknown',
    CASE login_result WHEN 0 THEN 'SUCCESS' WHEN 1 THEN 'FAIL' ELSE 'FAIL' END,
    fail_reason,
    login_time,
    del_flag,
    create_time,
    create_by,
    update_time,
    update_by
FROM auth_login_log;
```

**Step 2: 人工校验脚本内容**

检查点：
- 没有 `AUTO_INCREMENT`
- 包含完整公共字段
- 旧表删除语句默认注释掉，避免误删生产数据
- 不手动拼接逻辑删除筛选条件

**Step 3: 记录验证命令**

Run:
```bash
git diff -- database/sys_log_tables.sql database/wms_full_init.sql database/auth_ddl.sql database/migration/V20260522__unify_log_tables.sql
```
Expected: 只包含日志表统一相关 SQL 变更。

**Step 4: 提交**

```bash
git add database/sys_log_tables.sql database/wms_full_init.sql database/auth_ddl.sql database/migration/V20260522__unify_log_tables.sql
git commit -m "feat: 收敛日志表初始化与迁移脚本"
```

---

### Task 5: 删除认证模块旧日志模型与持久层

**Files:**
- Delete: `wms-server/wms-auth/src/main/java/com/wms/auth/domain/entity/AuthLoginLog.java`
- Delete: `wms-server/wms-auth/src/main/java/com/wms/auth/domain/entity/AuthOperLog.java`
- Delete: `wms-server/wms-auth/src/main/java/com/wms/auth/mapper/AuthLoginLogMapper.java`
- Delete: `wms-server/wms-auth/src/main/java/com/wms/auth/mapper/AuthOperLogMapper.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java`

**Step 1: 删除旧文件并清理引用**

确保 `AuthAuditServiceImpl` 不再注入 `AuthLoginLogMapper` / `AuthOperLogMapper`，然后删除无用实体和 Mapper。

**Step 2: 全局检索确认无残留引用**

Run:
```bash
rg "AuthLoginLog|AuthOperLog|AuthLoginLogMapper|AuthOperLogMapper" d:/Codes/WMS_code/wms-server
```
Expected: 只允许出现在迁移说明、计划文档或 Git 历史之外的删除痕迹中；生产代码不再引用。

**Step 3: 提交**

```bash
git add -A
git commit -m "chore: 删除认证模块旧日志模型"
```

---

### Task 6: 做完整验证并记录结果

**Files:**
- Modify: `docs/plans/2026-05-22-unify-log-tables.md`
- Optional Test: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java`
- Optional Test: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplProfileTest.java`

**Step 1: 跑认证模块定向测试**

Run:
```bash
mvn -pl wms-auth -am "-Dtest=UserAgentParserTest,AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest,AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```
Expected: `BUILD SUCCESS`

**Step 2: 跑服务端编译**

Run:
```bash
mvn -pl wms-server -am clean compile -DskipTests
```
Expected: `BUILD SUCCESS`

如果 `-pl wms-server` 不能定位聚合模块，则在仓库根目录执行：

```bash
cd wms-server
mvn clean compile -DskipTests
```

**Step 3: 记录手工验证项**

至少记录以下人工回归：
- 登录成功后 `sys_login_log` 新增 `SUCCESS` 记录
- 登录失败后 `sys_login_log` 新增 `FAIL` 记录
- 登出后 `sys_oper_log` 新增 `module=auth`、`type=LOGOUT` 记录
- 系统登录日志分页接口可查到认证侧产生的数据
- 个人中心最近登录信息仍能返回最近一次成功登录时间与 IP

**Step 4: 更新计划文档状态说明**

在文档末尾追加一段简短实施记录，注明：
- 实际执行的测试命令
- 编译结果
- 手工验证结果
- 若有未完成项，列出阻塞原因

**Step 5: 提交**

```bash
git add docs/plans/2026-05-22-unify-log-tables.md
git commit -m "docs: 更新日志表统一实施验证记录"
```

---

## 实施记录

### 已完成项

- 已将认证模块登录成功、登录失败、登出审计统一收敛到 `sys_login_log` 与 `sys_oper_log`
- 已为 `sys_login_log` 增加 `user_id` 字段，并在系统服务中支持按用户查询最近一次成功登录
- 已删除 `wms-auth` 中旧的 `AuthLoginLog`、`AuthOperLog` 及对应 Mapper
- 已新增迁移脚本 `database/migration/V20260522__unify_log_tables.sql`
- 已补充 `UserAgentParser`，将认证侧 `User-Agent` 拆分为 `browser` 与 `os`

### 实际执行命令

```bash
mvn -pl wms-auth -am "-Dtest=AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest,AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn -pl wms-auth -am "-Dtest=UserAgentParserTest,AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest,AuthServiceImplProfileTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
mvn clean compile -DskipTests
```

### 自动验证结果

- 认证链路定向测试通过：`UserAgentParserTest`、`AuthAuditServiceImplTest`、`AuthServiceImplAuthFlowTest`、`AuthServiceImplProfileTest`
- 定向测试汇总：`Tests run: 12, Failures: 0, Errors: 0, Skipped: 0`
- 服务端全量编译通过：`wms-server` 聚合模块 `BUILD SUCCESS`

### 手工回归项

- `sys_login_log` 是否新增 `SUCCESS` 登录记录：已通过代码链路与测试覆盖验证，待接数据库环境做最终联调确认
- `sys_login_log` 是否新增 `FAIL` 登录记录：已通过代码链路与测试覆盖验证，待接数据库环境做最终联调确认
- `sys_oper_log` 是否新增 `module=auth`、`type=LOGOUT` 记录：已通过代码链路与测试覆盖验证，待接数据库环境做最终联调确认
- 系统登录日志分页接口是否查到认证侧数据：接口读取链路已切到 `sys_login_log`，待接数据库环境做最终联调确认
- 个人中心最近登录信息是否仍返回最近一次成功登录 IP 与时间：已由 `AuthServiceImplProfileTest` 覆盖并通过

### 未完成项

- 尚未在真实数据库环境执行 `V20260522__unify_log_tables.sql` 迁移脚本
- 尚未对登录、失败登录、登出后的数据库实际落表结果做在线联调截图或 SQL 核验
