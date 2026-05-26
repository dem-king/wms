# WMS-Server 代码审查报告

> 审查日期：2026-05-26
> 审查范围：`d:\Codes\WMS_code\wms-server` 全部模块
> 审查依据：[AGENTS.md](file:///d:/Codes/WMS_code/AGENTS.md) 开发规则

---

## 审查概要

| 类别 | 通过项 | 违规项 | 建议项 |
|------|--------|--------|--------|
| 安全规则 | 3 | 1 | 1 |
| 分层架构 | 3 | 1 | 1 |
| 数据操作 | 4 | 0 | 0 |
| 业务逻辑 | 3 | 0 | 1 |
| 性能 | 3 | 0 | 0 |
| 注释规范 | 3 | 0 | 2 |
| 配置与SQL | 3 | 1 | 1 |
| 命名规范 | 3 | 0 | 0 |
| **合计** | **25** | **3** | **6** |

---

## 一、安全规则审查

### ✅ 通过项

1. **生产环境配置合规**：[application-prod.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-prod.yml) 中数据库密码、Redis密码、MinIO密钥、JWT密钥均使用 `${ENV_VAR}` 环境变量，无硬编码。

2. **@PreAuthorize 全覆盖**：所有 Controller 的每个 public 方法均添加了 `@PreAuthorize` 注解，包含具体的权限标识（如 `hasAuthority('system:user:list')`）或最小粒度 `isAuthenticated()`。

3. **@DataScope 全覆盖**：所有 Controller 的查询方法（page/list/getById/search等）均添加了 `@DataScope` 注解。

4. **@OperLog 写操作覆盖**：所有增删改 Controller 方法均添加了 `@OperLog` 注解。查询类 Controller（如 wms-report、wms-system/SysLoginLogController、SysOperLogController）仅含查询方法，不要求 @OperLog，属于合规。

---

### ❌ 违规项

#### 1.1 开发环境配置文件存在硬编码默认密码

| 文件 | 行号 | 违规内容 |
|------|------|----------|
| [application-dev.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-dev.yml) | L6 | `password: ${MYSQL_PASSWORD:wqy123456}` — 默认密码硬编码 |
| [application-dev.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-dev.yml) | L15 | `secret: ${JWT_SECRET:wms-secret-key-for-jwt-token-generation-2024}` — JWT密钥默认值硬编码 |
| [application-dev.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-dev.yml) | L21 | `jwt-secret: ${JWT_SECRET:wms-secret-key-for-jwt-token-generation-2024}` — 重复硬编码 |
| [application-dev.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-dev.yml) | L35-L36 | `minio-access-key: ${MINIO_ACCESS_KEY:minioadmin}` / `minio-secret-key: ${MINIO_SECRET_KEY:minioadmin}` — MinIO密钥默认值硬编码 |

**违规说明**：开发环境配置文件中使用明文默认值作为环境变量兜底，虽然使用 `${ENV_VAR:default}` 模式，但明文默认值存在泄露风险。

**建议修复**：将敏感信息的默认值替换为无意义的占位符，如：

```yaml
password: ${MYSQL_PASSWORD:dev_placeholder}
secret: ${JWT_SECRET:dev_placeholder}
minio-access-key: ${MINIO_ACCESS_KEY:dev_placeholder}
minio-secret-key: ${MINIO_SECRET_KEY:dev_placeholder}
```

---

### ⚠️ 建议项

#### S1.1 主配置文件日志实现未按环境区分

| 文件 | 行号 | 内容 |
|------|------|------|
| [application.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application.yml) | L36 | `log-impl: org.apache.ibatis.logging.stdout.StdOutImpl` |

**说明**：主配置文件使用 StdOutImpl（控制台输出SQL），生产环境配置已覆写为 NoLoggingImpl，合规。但建议在主配置中也使用变量控制，避免忘记在生产环境覆写：

```yaml
log-impl: ${MYBATIS_LOG_IMPL:org.apache.ibatis.logging.stdout.StdOutImpl}
```

---

## 二、分层架构审查

### ✅ 通过项

1. **Controller不含业务逻辑**：所有 Controller 方法仅做参数接收和 Service 调用，无 Entity→Vo 转换、无 SecurityUtil 调用、无业务判断。

2. **返回VO而非Entity**：所有 Controller 和 Service 公共方法的返回值均为 VO 类型（如 `SysUserVo`、`InboundOrderVo` 等）。

3. **存在独立Converter类**：wms-business、wms-item、wms-system 等模块均有独立的 Converter 类（如 `InboundOrderConverter`、`ItemConverter`、`SysMenuConverter` 等）。

4. **DTO/VO 命名规范**：所有 DTO 类以 `Dto` 结尾，VO 类以 `Vo` 结尾（小写 o）。

---

### ❌ 违规项

#### 2.1 wms-system 模块多个 ServiceImpl 使用私有 toVo 方法

以下 6 个 ServiceImpl 中使用了私有 `toVo` 转换方法，应将转换逻辑提取到独立 Converter 类：

| 文件 | 行号 | 方法签名 |
|------|------|----------|
| [SysUserServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysUserServiceImpl.java) | L395 | `private SysUserVo toVo(SysUser user)` |
| [SysRoleServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysRoleServiceImpl.java) | L370 | `private SysRoleVo toVo(SysRole role)` |
| [SysPermissionServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysPermissionServiceImpl.java) | L195 | `private SysPermissionVo toVo(SysPermission perm)` |
| [SysSupplierServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysSupplierServiceImpl.java) | L172 | `private SysSupplierVo toVo(SysSupplier supplier)` |
| [SysConfigServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysConfigServiceImpl.java) | L98 | `private SysConfigVo toVo(SysConfig config)` |
| [SysDeptServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysDeptServiceImpl.java) | L175 | `private SysDeptVo toVo(SysDepartment dept)` |

**违规说明**：根据规则 2.3「使用独立Converter类」，禁止在 Service 中使用 private toVo 方法做转换。

**建议修复**：为 wms-system 模块创建对应的 Converter 类（如 `SysUserConverter`、`SysRoleConverter`、`SysPermissionConverter` 等），将 toVo 逻辑移入 Converter。

---

### ⚠️ 建议项

#### S2.1 Service 返回 Entity 的情况

| 文件 | 行号 | 方法 |
|------|------|------|
| [SysUserServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysUserServiceImpl.java) | L51 | `public SysUser getByUsername(String username)` |

**说明**：该方法返回 `SysUser` 实体（含密码哈希），被 `AuthServiceImpl` 内部调用用于认证。虽然该方法返回了 Entity，但它是认证流程中的必要内部方法，不直接暴露给 Controller。建议评估是否可以通过内部 VO 封装或将该方法标记为仅供认证模块内部使用。

---

## 三、数据操作审查

### ✅ 全部通过

1. **无物理删除**：所有 ServiceImpl 的 `delete()` 方法均正确使用逻辑删除模式（`entity.setDelFlag(DelFlagConstants.DELETED)` + `mapper.updateById(entity)`）。未发现 `mapper.delete`、`mapper.deleteById`、`mapper.deleteBatchIds`、`removeById` 等物理删除调用。

   - Redis 中的 `stringRedisTemplate.delete()` 调用（LoginLockServiceImpl、AuthorizeServiceImpl、TokenServiceImpl）操作的是缓存数据，非数据库表，属于正常操作。

2. **无冗余 del_flag 条件**：全局未发现 `.eq(Xxx::getDelFlag, 0)` 手动拼接逻辑删除条件的情况。

3. **主键策略正确**：所有 Entity 继承 `BaseEntity`，使用 `@TableId(type = IdType.ASSIGN_ID)`（雪花ID），无 `IdType.AUTO` 使用。

4. **SQL建表合规**：所有 SQL 建表文件的主键均为 `BIGINT NOT NULL COMMENT '主键(雪花ID)'`，无 `AUTO_INCREMENT`。所有表均包含完整公共字段（`del_flag`、`create_time`、`create_by`、`update_time`、`update_by`）。

---

## 四、业务逻辑审查

### ✅ 通过项

1. **无魔法数字**：生产代码中未发现直接使用数字字面量进行状态/类型比较的情况。代码统一使用常量引用（`BizConstants.STATUS_ENABLED`、`DelFlagConstants.DELETED`、`SysMenuConstants.STATUS_ENABLED` 等）。

2. **常量类使用规范**：各模块正确使用了已有的公共常量类和模块常量类。

3. **异常处理精确**：所有 `BizException` 抛出均有明确的业务场景描述（如"用户不存在"、"用户已删除"、"菜单编码已存在"等）。

---

### ⚠️ 建议项

#### S4.1 SysUserServiceImpl 中存在 Dto→Entity 的属性拷贝方法

| 文件 | 行号 | 方法 |
|------|------|------|
| [SysUserServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysUserServiceImpl.java) | L382 | `private void copyDtoToEntity(SysUserDto dto, SysUser entity)` |
| [SysMenuServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysMenuServiceImpl.java) | L270 | `private void copyDtoToEntity(SysMenuDto dto, SysMenu entity)` |

**说明**：两个 ServiceImpl 中有 Dto→Entity 属性拷贝的私有方法。虽然规则没有明确禁止，但建议统一移至对应的 Converter 类中，保持转换逻辑集中管理。

---

## 五、性能审查

### ✅ 通过项

1. **无N+1查询**：经审查，主要列表查询方法（如 [ItemServiceImpl.page()](file:///d:/Codes/WMS_code/wms-server/wms-item/src/main/java/com/wms/item/service/impl/ItemServiceImpl.java#L70-L142)）使用了批量查询 + Map 构建的模式，避免了 N+1 查询。

2. **批量操作使用**：用户角色分配操作中使用了 `Db.saveBatch()` 和 `Db.updateBatchById()` 等批量操作 API。

3. **编号生成**：项目使用 `IdType.ASSIGN_ID`（雪花算法）生成主键；业务编号生成使用 `SequenceGenerator`（Redis自增），符合规范。

---

## 六、注释规范审查

### ✅ 通过项

1. **Entity字段注释完整**：所有 Entity 类的每个字段均有 `@Schema(description = "...")` 注解。

2. **Controller类有JavaDoc**：所有 Controller 类有类级别 JavaDoc 注释。

3. **Controller方法有JavaDoc**：所有 Controller 的 public 方法均有 JavaDoc 注释。

---

### ⚠️ 建议项

#### S6.1 部分 Entity 类 JavaDoc 过于简略

| 文件 | 当前注释 |
|------|----------|
| [SysUser.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/domain/entity/SysUser.java) | `/** 系统用户实体，对应用户表sys_user */` |

**建议**：完善为更详细的描述，例如：

```java
/**
 * 系统用户实体
 * 对应表 sys_user，存储用户账号、密码哈希、个人信息等
 */
```

#### S6.2 SysLoginLog 表缺少 user_id 字段

| 文件 | 问题 |
|------|------|
| [sys_log_tables.sql](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/resources/sql/sys_log_tables.sql) | `sys_login_log` 表缺少 `user_id` 字段 |
| [SysLoginLog.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/domain/entity/SysLoginLog.java) | Entity 中定义了 `userId` 字段 |

**建议**：在 `sys_login_log` 建表 SQL 中补充 `user_id BIGINT DEFAULT NULL COMMENT '用户ID'` 字段。

---

## 七、配置与SQL建表规范审查

### ✅ 通过项

1. **SQL建表规范**：所有模块的 SQL 建表文件均符合规范：主键为 `BIGINT NOT NULL`、包含完整公共字段、无 `AUTO_INCREMENT`。

2. **Controller参数校验**：Controller 中新增/更新方法的 DTO 参数均使用了 `@Valid` 注解。

3. **生产环境日志配置**：[application-prod.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-prod.yml) L35 正确配置了 `log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl`。

---

### ❌ 违规项

#### 7.1 SysUserServiceImpl 中使用 @Value 读取默认密码

| 文件 | 行号 | 代码 |
|------|------|------|
| [SysUserServiceImpl.java](file:///d:/Codes/WMS_code/wms-server/wms-system/src/main/java/com/wms/system/service/impl/SysUserServiceImpl.java) | L41-L42 | `@Value("${wms.default-password:Wms@2024}") private String defaultPassword;` |

**违规说明**：虽然使用了 `${...:default}` 模式从配置读取，但默认值 `Wms@2024` 在 Java 代码中明文硬编码，违反规则 1.1。

**建议修复**：

```java
@Value("${wms.default-password}")
private String defaultPassword;
```

在配置文件中设置默认值（但使用占位符）：

```yaml
wms:
  default-password: ${WMS_DEFAULT_PASSWORD:dev_placeholder}
```

---

### ⚠️ 建议项

#### S7.1 配置文件结构不统一

[application-dev.yml](file:///d:/Codes/WMS_code/wms-server/wms-app/src/main/resources/application-dev.yml) 中存在两个 `wms:` 顶级配置块（L19-L30 和 L32-L38），建议合并为一个。

---

## 八、命名规范审查

### ✅ 全部通过

1. **DTO类**：全部以 `Dto` 结尾（如 `SysUserDto`、`InboundOrderDto`）
2. **VO类**：全部以 `Vo` 结尾（小写 o，如 `SysUserVo`、`InboundOrderVo`）
3. **Converter类**：全部以 `Converter` 结尾（如 `ItemConverter`、`SysMenuConverter`）
4. **状态常量**：全部使用全大写下划线命名（如 `STATUS_ENABLED`、`DEL_FLAG_NORMAL`）
5. **前后端参数命名一致**：DTO/VO 字段均使用小驼峰（camelCase），与前端 API 参数一致

---

## 九、PR自查清单对照

| 检查项 | 结果 |
|--------|------|
| 配置文件无硬编码密钥/密码？ | ⚠️ dev配置有明文默认值 |
| Controller查询方法有@DataScope？ | ✅ |
| 所有方法有@PreAuthorize？ | ✅ |
| 写操作有@OperLog？ | ✅ |
| Controller不含业务逻辑？ | ✅ |
| 返回VO而非Entity？ | ⚠️ SysUserService.getByUsername返回Entity |
| 删除用逻辑删除？ | ✅ |
| 无.eq(::getDelFlag, 0)冗余条件？ | ✅ |
| 无物理删除？ | ✅ |
| 无魔法数字？ | ✅ |
| 无N+1查询？ | ✅ |
| 无全表查询+内存过滤？ | ✅ |
| Entity字段有@Schema+JavaDoc注释？ | ✅ |
| 类有JavaDoc？ | ✅ |
| public方法有JavaDoc？ | ✅ |
| 关键逻辑有行内注释？ | ✅ |
| 主键策略为ASSIGN_ID？ | ✅ |
| Controller参数有校验注解？ | ✅ |
| 状态值使用常量类/枚举？ | ✅ |
| ServiceImpl中无private static final局部常量？ | ✅ |
| 逻辑删除使用DelFlagConstants？ | ✅ |
| VO类以Vo结尾？ | ✅ |
| 前后端参数命名一致？ | ✅ |

---

## 十、总结

### 整体评价

项目整体代码质量**良好**，大部分开发规则得到了严格遵守。主要问题集中在以下三个方面：

1. **开发环境配置存在明文默认密码**（安全规则 1.1）
2. **wms-system 模块 ServiceImpl 使用私有 toVo 方法**（分层架构 2.3）
3. **SysUserServiceImpl 中 @Value 默认密码硬编码**（安全规则 1.1）

### 优先修复建议

| 优先级 | 问题 | 影响范围 | 修复难度 |
|--------|------|----------|----------|
| 🔴 高 | dev配置明文默认密码 | 安全 | 低 |
| 🔴 高 | @Value 默认密码硬编码 | 安全 | 低 |
| 🟡 中 | 私有 toVo 方法移至 Converter | 架构一致性 | 中 |
| 🟢 低 | Entity 类 JavaDoc 完善 | 可维护性 | 低 |
| 🟢 低 | sys_login_log 表补充 user_id | 数据一致性 | 低 |
| 🟢 低 | 配置文件 wms 顶级块合并 | 可维护性 | 低 |

---

*报告由自动化代码审查工具生成，基于 [AGENTS.md](file:///d:/Codes/WMS_code/AGENTS.md) 开发规则。*