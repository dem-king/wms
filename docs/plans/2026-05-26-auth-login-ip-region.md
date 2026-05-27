# 登录日志 IP 归属地实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为认证模块登录成功和登录失败日志补充基于 `ip2region` 离线库解析的真实登录地点，并写入 `sys_login_log.login_location`。

**Architecture:** 在 `wms-common` 下沉 `IpRegionResolver` 通用能力，集中处理离线库初始化、IP 解析、地区串格式化和异常兜底。`wms-auth` 仅在 `AuthServiceImpl` 中调用解析器，再通过 `AuthAuditService -> SysLoginLogService` 透传并落库，保持现有登录审计主链路不变。

**Tech Stack:** Java 17, Spring Boot 3, MyBatis-Plus, Maven, ip2region, JUnit 5, Mockito

---

### Task 1: 为 IP 地点解析器写失败测试并定义公共配置

**Files:**
- Create: `wms-server/wms-common/src/test/java/com/wms/common/util/IpRegionResolverTest.java`
- Create: `wms-server/wms-common/src/main/java/com/wms/common/config/IpRegionProperties.java`
- Create: `wms-server/wms-common/src/main/java/com/wms/common/util/IpRegionResolver.java`
- Modify: `wms-server/wms-common/pom.xml`

**Step 1: 写失败测试**

在 `IpRegionResolverTest` 先覆盖最小行为：

- 内网 IP 返回 `内网IP`
- 空 IP 返回 `未知`
- 原始地区串可被格式化成“省市区”

建议测试骨架：

```java
@Test
void shouldReturnIntranetForPrivateIp() {
    IpRegionResolver resolver = new IpRegionResolver(new IpRegionProperties());
    assertEquals("内网IP", resolver.resolve("127.0.0.1"));
}

@Test
void shouldFormatRegionTextToProvinceCityDistrict() {
    assertEquals("广东省深圳市南山区",
            IpRegionResolver.formatRegion("中国|0|广东省|深圳市|南山区"));
}
```

**Step 2: 运行测试确认失败**

Run:

```bash
mvn -pl wms-common -am "-Dtest=IpRegionResolverTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Expected: 编译失败，提示 `IpRegionResolver` 或 `IpRegionProperties` 尚不存在。

**Step 3: 写最小实现**

实现内容：

- 在 `wms-common/pom.xml` 增加 `org.lionsoul:ip2region` 依赖
- 新建 `IpRegionProperties`，使用 `@ConfigurationProperties(prefix = "wms.ip-region")`
- 新建 `IpRegionResolver`
  - 提供 `resolve(String ip)` 方法
  - 提供 `formatRegion(String rawRegion)` 静态方法
  - 内网地址直接返回 `内网IP`
  - 空值或异常返回 `未知`

建议实现片段：

```java
@Data
@Component
@ConfigurationProperties(prefix = "wms.ip-region")
public class IpRegionProperties {
    private boolean enabled = true;
    private String xdbPath = "classpath:ip2region.xdb";
}
```

```java
public String resolve(String ip) {
    if (!StringUtils.hasText(ip)) {
        return "未知";
    }
    if (isIntranetIp(ip)) {
        return "内网IP";
    }
    return formatRegion(search(ip));
}
```

**Step 4: 重新运行测试确认通过**

Run:

```bash
mvn -pl wms-common -am "-Dtest=IpRegionResolverTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Expected: `BUILD SUCCESS`

**Step 5: 提交**

```bash
git add wms-server/wms-common/pom.xml \
        wms-server/wms-common/src/main/java/com/wms/common/config/IpRegionProperties.java \
        wms-server/wms-common/src/main/java/com/wms/common/util/IpRegionResolver.java \
        wms-server/wms-common/src/test/java/com/wms/common/util/IpRegionResolverTest.java
git commit -m "feat: add ip region resolver"
```

---

### Task 2: 接入离线库资源并补应用配置

**Files:**
- Create: `wms-server/wms-common/src/main/resources/ip2region.xdb`
- Modify: `wms-server/wms-app/src/main/resources/application.yml`
- Modify: `wms-server/wms-app/src/main/resources/application-dev.yml`
- Modify: `wms-server/wms-app/src/main/resources/application-prod.yml`

**Step 1: 放置资源并补配置**

完成以下改动：

- 将 `ip2region.xdb` 放入 `wms-common/src/main/resources/`
- 在应用配置中增加 `wms.ip-region` 段
- 保留环境变量覆盖能力，避免路径硬编码

建议配置：

```yaml
wms:
  ip-region:
    enabled: true
    xdb-path: ${IP2REGION_XDB_PATH:classpath:ip2region.xdb}
```

**Step 2: 校验配置不引入敏感信息**

检查点：

- 不出现硬编码密码或密钥
- 仅新增 `IP2REGION_XDB_PATH` 环境变量占位
- 默认值为 classpath 路径而非绝对磁盘路径

**Step 3: 记录资源检查命令**

Run:

```bash
git diff -- wms-server/wms-app/src/main/resources/application.yml \
             wms-server/wms-app/src/main/resources/application-dev.yml \
             wms-server/wms-app/src/main/resources/application-prod.yml \
             wms-server/wms-common/src/main/resources/ip2region.xdb
```

Expected: 仅包含 `ip-region` 配置和离线库资源新增。

**Step 4: 提交**

```bash
git add wms-server/wms-app/src/main/resources/application.yml \
        wms-server/wms-app/src/main/resources/application-dev.yml \
        wms-server/wms-app/src/main/resources/application-prod.yml \
        wms-server/wms-common/src/main/resources/ip2region.xdb
git commit -m "chore: configure ip2region resource"
```

---

### Task 3: 先更新认证测试，锁定 loginLocation 透传行为

**Files:**
- Modify: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java`
- Modify: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplAuthFlowTest.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java`

**Step 1: 写失败测试**

更新两个测试文件：

- `AuthAuditServiceImplTest` 断言 `recordLoginLog(...)` 会透传 `loginLocation`
- `AuthServiceImplAuthFlowTest` 断言登录成功/失败时会把解析后的地点写入审计服务

建议断言：

```java
verify(sysLoginLogService).recordLoginLog(
        1001L,
        "admin",
        "127.0.0.1",
        "内网IP",
        "Chrome 124",
        "Windows 10",
        SysLogConstants.LOGIN_STATUS_SUCCESS,
        null
);
```

```java
verify(authAuditService).recordLoginLog(
        1001L,
        "admin",
        "127.0.0.1",
        "内网IP",
        "Chrome 124",
        "Windows 10",
        SysLogConstants.LOGIN_STATUS_SUCCESS,
        null
);
```

**Step 2: 运行测试确认失败**

Run:

```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Expected: 失败，提示 `AuthAuditService.recordLoginLog(...)` 签名尚未包含 `loginLocation` 或现有实现仍传 `null`。

**Step 3: 写最小实现**

修改 `AuthAuditService`：

- `recordLoginLog(...)` 签名增加 `String loginLocation`

示例：

```java
void recordLoginLog(Long userId, String username, String loginIp, String loginLocation,
                    String browser, String os, String status, String failReason);
```

**Step 4: 重新运行测试确认通过接口层编译**

Run:

```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Expected: 仍可能失败，但失败点应转移到实现类尚未同步。

**Step 5: 提交**

```bash
git add wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java \
        wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplAuthFlowTest.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/AuthAuditService.java
git commit -m "test: lock login location audit contract"
```

---

### Task 4: 实现认证链路 loginLocation 写入

**Files:**
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java`
- Modify: `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java`
- Modify: `wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysLoginLogServiceImpl.java`

**Step 1: 写最小实现**

完成以下改动：

- `AuthAuditServiceImpl.recordLoginLog(...)` 透传 `loginLocation`
- `AuthServiceImpl` 注入 `IpRegionResolver`
- `recordLoginSuccess(...)` 和 `recordLoginFail(...)` 先解析地点再写审计日志
- `SysLoginLogServiceImpl` 保持按入参落库，不再接收 `null` 地点

建议实现片段：

```java
private void recordLoginSuccess(String username, Long userId, String ip, String ua) {
    UserAgentParser.ParsedUserAgent parsedUserAgent = UserAgentParser.parse(ua);
    String loginLocation = ipRegionResolver.resolve(ip);
    authAuditService.recordLoginLog(
            userId,
            username,
            ip,
            loginLocation,
            parsedUserAgent.browser(),
            parsedUserAgent.os(),
            SysLogConstants.LOGIN_STATUS_SUCCESS,
            null
    );
}
```

```java
@Override
public void recordLoginLog(Long userId, String username, String loginIp, String loginLocation,
                           String browser, String os, String status, String failReason) {
    sysLoginLogService.recordLoginLog(
            userId, username, loginIp, loginLocation, browser, os, status, failReason
    );
}
```

**Step 2: 运行测试确认通过**

Run:

```bash
mvn -pl wms-auth -am "-Dtest=AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Expected: `BUILD SUCCESS`

**Step 3: 检查最近编辑文件诊断**

Run diagnostics for:

- `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java`
- `wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java`
- `wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysLoginLogServiceImpl.java`

Expected: 无新增编译或导入错误。

**Step 4: 提交**

```bash
git add wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthAuditServiceImpl.java \
        wms-server/wms-auth/src/main/java/com/wms/auth/service/impl/AuthServiceImpl.java \
        wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysLoginLogServiceImpl.java
git commit -m "feat: record login location with ip2region"
```

---

### Task 5: 做完整验证并记录结果

**Files:**
- Modify: `docs/plans/2026-05-26-auth-login-ip-region.md`
- Optional Test: `wms-server/wms-common/src/test/java/com/wms/common/util/IpRegionResolverTest.java`
- Optional Test: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthAuditServiceImplTest.java`
- Optional Test: `wms-server/wms-auth/src/test/java/com/wms/auth/service/impl/AuthServiceImplAuthFlowTest.java`

**Step 1: 跑公共层与认证层定向测试**

Run:

```bash
mvn -pl wms-auth -am "-Dtest=IpRegionResolverTest,AuthAuditServiceImplTest,AuthServiceImplAuthFlowTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Expected: `BUILD SUCCESS`

**Step 2: 跑服务端聚合编译**

Run:

```bash
cd wms-server
mvn clean compile -DskipTests
```

Expected: `BUILD SUCCESS`

**Step 3: 记录手工验证项**

至少记录以下结果：

- 本机地址登录后 `sys_login_log.login_location = 内网IP`
- 公网 IP 联调时地点格式为“省市区”
- 登录失败日志也写入地点
- 登录日志分页接口仍可正常返回地点字段

**Step 4: 将验证结果追加到计划文档末尾**

追加内容：

- 实际执行命令
- 测试结果
- 编译结果
- 手工验证结果
- 若离线库文件需额外部署，注明部署注意事项

**Step 5: 提交**

```bash
git add docs/plans/2026-05-26-auth-login-ip-region.md
git commit -m "docs: update login ip region verification"
```
