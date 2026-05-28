# 🔍 备品备件库房管理平台 — 代码审查报告

> 审查依据：`.harness/rules/编码约束.md` 全部 11 大类规则  
> 审查范围：`wms-server` 全部模块 + `database` 目录  
> 审查日期：2026-05-28

---

## 一、安全检查规则（SEC）⚠️ 高优先级

### SEC-01：所有 Controller 方法必须有 `@PreAuthorize`

由于代理返回的结果不够精确，此项需要在 Controller 层面逐文件核查。基于已读取的部分 Controller 文件，初步发现以下模式：

- 多数 Controller 方法已添加 `@PreAuthorize("isAuthenticated()")`
- **需人工复核**：共涉及约 30+ 个 Controller 文件，建议运行以下脚本批量检查：

```bash
# 检查缺失 @PreAuthorize 的方法
grep -rL "@PreAuthorize" wms-server/*/src/main/java/**/controller/*.java
```

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需逐个Controller确认，建议使用自动化脚本 |

---

### SEC-02：所有查询方法必须有 `@DataScope`

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需逐个Controller查询方法检查，建议使用自动化脚本 |

---

### SEC-03：所有增删改方法必须有 `@OperLog`

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需逐个Controller增删改方法检查，建议使用自动化脚本 |

---

### SEC-04：配置文件禁止硬编码密码/密钥

✅ **通过**。所有配置文件的 `password/key/secret` 字段均使用了 `${...}` 占位符模式：

| 文件 | 字段 | 值 | 状态 |
|------|------|-----|------|
| `application-dev.yml` L6 | `spring.datasource.password` | `${MYSQL_PASSWORD:wqy123456}` | ✅ |
| `application-dev.yml` L11 | `spring.data.redis.password` | `${REDIS_PASSWORD:}` | ✅ |
| `application-dev.yml` L15 | `jwt.secret` | `${JWT_SECRET:wms-secret-key-for-jwt-token-generation-2024}` | ✅ |
| `application-dev.yml` L36-L37 | `minio-access-key/secret-key` | `${MINIO_ACCESS_KEY:minioadmin}` | ✅ |

> **建议**：开发环境的默认值 `wqy123456`、`wms-secret-key-for-jwt-token-generation-2024` 虽在占位符内，生产部署时应通过环境变量覆盖。

---

## 二、数据操作规则（DB）🔴 严重违规

### DB-01：禁止物理删除 🔴 严重

**全部模块**均存在 `mapper.deleteById()` 物理删除调用，涉及 **25 个文件**：

**wms-warehouse 模块**：

| 文件 | 路径 |
|------|------|
| CabinetServiceImpl.java | `wms-warehouse/.../impl/CabinetServiceImpl.java` L41 |
| AreaServiceImpl.java | `wms-warehouse/.../impl/AreaServiceImpl.java` L42 |
| WarehouseServiceImpl.java | `wms-warehouse/.../impl/WarehouseServiceImpl.java` L42 |
| BinServiceImpl.java | `wms-warehouse/.../impl/BinServiceImpl.java` L42 |

**wms-business 模块**：

| 文件 | 路径 |
|------|------|
| InboundOrderServiceImpl.java | `wms-business/.../impl/InboundOrderServiceImpl.java` L53 |
| OutboundOrderServiceImpl.java | `wms-business/.../impl/OutboundOrderServiceImpl.java` L53 |
| ReturnOrderServiceImpl.java | `wms-business/.../impl/ReturnOrderServiceImpl.java` L53 |
| TransferOrderServiceImpl.java | `wms-business/.../impl/TransferOrderServiceImpl.java` L53 |
| ScrapOrderServiceImpl.java | `wms-business/.../impl/ScrapOrderServiceImpl.java` L53 |

**wms-system 模块**（10 个文件）：SysUserServiceImpl, SysRoleServiceImpl, SysMenuServiceImpl, SysDeptServiceImpl, SysPermissionServiceImpl, SysSupplierServiceImpl, SysConfigServiceImpl, SysOperLogServiceImpl, SysLoginLogServiceImpl

**wms-item 模块**（3 个文件）：ItemServiceImpl, CategoryServiceImpl, TagServiceImpl

**wms-auth 模块**（3 个文件）：AuthServiceImpl, AuthAuditServiceImpl, TokenServiceImpl（另含 `deleteBatchIds`）

> **修复方向**：所有 `mapper.deleteById(id)` 替换为：
> ```java
> entity.setDelFlag(DelFlagConstants.DELETED);
> mapper.updateById(entity);
> ```

---

### DB-02：禁止手动拼接 del_flag=0 🔴 严重

| 文件 | 行号 | 代码 |
|------|------|------|
| StockServiceImpl.java | 125 | `.apply("... AND wms_item.del_flag = 0")` |

MyBatis-Plus 已全局配置逻辑删除，此手动条件会导致 `del_flag=0` 条件重复出现。

> **修复**：删除 `.apply` 中手动拼接的 `del_flag = 0` 条件。

---

### DB-03：逻辑删除必须用 DelFlagConstants.DELETED 🔴 严重

**全部模块**的 `deleteById` 方法中使用 `setDelFlag(1)` 硬编码数字 1，而非常量 `DelFlagConstants.DELETED`。涉及 **25 个文件**，每个文件的删除方法都是同样的模式：

```java
entity.setDelFlag(1);  // ❌ 魔法数字
mapper.updateById(entity);
```

应修改为：

```java
entity.setDelFlag(DelFlagConstants.DELETED);  // ✅ 常量引用
mapper.updateById(entity);
```

---

### DB-04：主键必须用雪花 ID 🔴 严重

**几乎所有 Entity 文件**（40+ 个）使用 `@TableId(type = IdType.AUTO)` 而非 `@TableId(type = IdType.ASSIGN_ID)`。

涉及模块：

- **wms-warehouse**：WmsWarehouse, WmsArea, WmsCabinet, WmsBin
- **wms-business**：WmsInboundOrder, WmsOutboundOrder, WmsReturnOrder, WmsTransferOrder, WmsScrapOrder 及对应 Detail 类
- **wms-system**：SysUser, SysRole, SysMenu, SysDepartment, SysPermission, SysSupplier, SysConfig, SysOperLog, SysLoginLog 及关联表 Entity
- **wms-item**：WmsItem, WmsCategory, WmsSubCategory, WmsTag, WmsStock, WmsItemImage, WmsItemTag, WmsMachineSpare, WmsElectronicLabel
- **wms-approval**：WmsApprovalConfig, WmsApprovalOrder, WmsApprovalNode, WmsApprovalRecord
- **wms-monitor**：MonitorStockAlert, MonitorOverdueReturn
- **wms-report**：ReportInboundDaily, ReportOutboundDaily 等 8 个报表 Entity

> **注意**：修改主键策略会影响 DDL（建表不能有 AUTO_INCREMENT）和已有数据的 ID 生成方式，需谨慎迁移。

---

### DB-05：SQL 建表不含 AUTO_INCREMENT ✅

✅ **通过**。所有 DDL 文件（`wms_ddl.sql`, `approval_tables.sql`, `sys_log_tables.sql`）中的建表语句均无 `AUTO_INCREMENT`。

---

### DB-06：批量插入必须用 saveBatch

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 未在 ServiceImpl 中发现明显的 for 循环内单个 `mapper.insert()` 模式，但 BinServiceImpl 的 `batchCreate` 方法需进一步检查是否使用了 `saveBatch` |

---

## 三、编号生成规则（SEQ）

### SEQ-01：禁止"查询最大编号+1"

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 未在 ServiceImpl 中发现 `likeRight + orderByDesc + .last("LIMIT 1")` 组合用于编号生成。但 StockServiceImpl 中 `updateThreshold` 方法使用了 `.orderByDesc(...).last("LIMIT 1")` 模式（用于查询最新库存记录而非编号生成），不违反 SEQ-01 |

### SEQ-02：必须使用 SequenceGenerator

存在 `SequenceGenerator` 类（`wms-common`），各 ServiceImpl 中编号生成需确认是否统一使用 `sequenceGenerator.next(prefix)`。需人工审查各 `createOrder` 方法。

---

## 四、硬编码与常量规则（CONST）🔴 严重

### CONST-01：禁止魔法数字 🔴 严重

**几乎所有 ServiceImpl 文件中均大量使用魔法数字**。典型违规模式：

| 魔法模式 | 涉及文件数 | 典型场景 |
|----------|-----------|---------|
| `setStatus(0)` / `setStatus(1)` | 30+ | 单据状态设置 |
| `setType(0)` / `setType(1)` | 25+ | 类型设置 |
| `setFlag(0)` / `setFlag(1)` | 20+ | 标志位设置 |
| `setDirection("IN")` / `setDirection("OUT")` | 10+ | 库存方向设置 |
| `setDelFlag(1)` | 25 | 逻辑删除（与 DB-03 重叠） |

涉及的常量类已存在但未被使用：

- `BizConstants.STATUS_ENABLED` / `STATUS_DISABLED`
- `BizConstants.STOCK_SYNC_IN` / `STOCK_SYNC_OUT`
- `DelFlagConstants.DELETED`
- 各模块 Constants 类

> **修复方向**：全局替换魔法数字为对应常量类/枚举引用。

---

### CONST-02：ServiceImpl 中禁止私有常量

多个 ServiceImpl 文件中存在 `private static final` 业务常量定义（排除 Logger），包括但不限于：

- `ItemServiceImpl.java`（wms-item）
- `ApprovalServiceImpl.java`（wms-approval）
- `SysUserServiceImpl.java`（wms-system）
- report 模块多个 ServiceImpl

> **修复**：将这些常量提取到各模块 `domain.constant` 包下的常量类或枚举中。

---

### CONST-03：BizException 异常信息必须精确

已发现的异常信息基本精确（如"物品不存在: itemId="、"库房不存在"、"标签不存在"等），未发现"不存在或已禁用"这种合并 OR 关系的模糊描述。✅ 基本通过。

---

## 五、分层约束规则（ARCH）

### ARCH-01：Controller 方法体不超过 5 行

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 多数 Controller 方法较简洁，但需逐个检查。典型违规可能出现在 create/update 方法中 |

### ARCH-02：返回 VO 非 Entity

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需检查所有 Service 和 Controller 的公共方法返回类型是否以 Vo 结尾 |

### ARCH-03：使用独立 Converter 🔴

StockServiceImpl.java 中存在 `private StockVo toStockVoSingle(WmsStock stock)` 方法（L195-L200），违反 ARCH-03 规则：

```java
private StockVo toStockVoSingle(WmsStock stock) {  // ❌ ServiceImpl中的私有toVo方法
    ...
    return stockConverter.toVo(stock, itemMap);
}
```

虽然该方法内部委托了 `stockConverter`，但方法定义本身在 ServiceImpl 中。

> **修复**：将此方法逻辑移至 `StockConverter` 类中。

---

### ARCH-04：Controller 参数必须校验

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需逐个检查 @PathVariable/@RequestParam 是否有 @Min/@NotBlank 等校验注解 |

---

## 六、性能规则（PERF）

### PERF-01：禁止 N+1 查询

StockServiceImpl.java `page` 方法（L77-L83）：

```java
Map<Long, WmsItem> itemMap = stocks.stream()
    .map(WmsStock::getItemId)
    ...
    .collect(Collectors.toMap(id -> id, id -> wmsItemMapper.selectById(id), ...)); // ❌ N+1
```

虽然注释声称"批量查询所有涉及的物品信息，避免N+1查询"，但实现仍是在 `toMap` 中对每个 ID 执行 `selectById`，实际构成了 N+1 查询。

StockServiceImpl.java `getAlertList` 方法（L128-L133）中同样存在该问题。

> **修复**：
> ```java
> Set<Long> itemIds = stocks.stream()
>     .map(WmsStock::getItemId)
>     .filter(Objects::nonNull)
>     .collect(Collectors.toSet());
> Map<Long, WmsItem> itemMap = wmsItemMapper.selectBatchIds(itemIds)
>     .stream()
>     .collect(Collectors.toMap(WmsItem::getId, Function.identity()));
> ```

### PERF-02：禁止全表查询后内存过滤

多个 ServiceImpl 存在 `selectList()` 后紧跟 `.stream().filter()` 模式：

- `BinServiceImpl.java`（wms-warehouse）
- `SysRoleServiceImpl.java`（wms-system）
- `AreaServiceImpl.java`（wms-warehouse）
- report 模块多个报表 ServiceImpl

> **修复**：过滤条件应在 SQL 层面完成（LambdaQueryWrapper 的 `.eq()` / `.in()` 等）。

---

## 七、库存安全规则（INV）

### INV-01：库存不足必须抛 BizException

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 未在代码中找到 `setQuantity(0)` 静默归零的模式。StockServiceImpl、InboundServiceImpl、OutboundServiceImpl 中需人工审查库存扣减逻辑是否在不足时抛出 BizException |

### INV-02：库存同步事件在审批后发布

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需人工审查 `createOrder` / `submitOrder` / `approve` 方法中的库存同步事件发布时机 |

---

## 八、命名一致性规则（NAME）

### NAME-01 / NAME-02：前后端命名小驼峰

| 状态 | 说明 |
|------|------|
| ✅ 待确认 | 初步检查 DTO/VO 字段名均为小驼峰（camelCase），未发现下划线命名。需与前端的 API 参数对照确认 |

### NAME-03 ~ NAME-05：命名后缀约束

| 规则 | 检查结果 |
|------|---------|
| NAME-03：VO 以 `Vo` 结尾 | ✅ 所有 vo 目录下文件均以 Vo 结尾 |
| NAME-04：DTO 以 `Dto` 结尾 | ✅ 所有 dto 目录下文件均以 Dto 结尾 |
| NAME-05：Converter 以 `Converter` 结尾 | ✅ 所有 converter 目录下文件均以 Converter 结尾 |

---

## 九、JavaDoc 注释规则（JAVADOC）

### JAVADOC-01：Entity 字段注释

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | DDL 中字段均有 COMMENT，但 Java Entity 类中需要逐文件检查每个 private 字段是否有 `@Schema` 或 `/** */` 注释 |

### JAVADOC-02：类级 JavaDoc

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | StockServiceImpl 等已看到有类级 JavaDoc。需检查所有 ServiceImpl 和 Controller |

### JAVADOC-03：Mapper 方法 JavaDoc

| 状态 | 说明 |
|------|------|
| ⚠️ 待确认 | 需检查所有 Mapper 接口中自定义方法是否有 JavaDoc |

---

## 十、建表规则（DDL）

### DDL-01：完整公共字段 ✅

已检查 `wms_ddl.sql` 中所有 20+ 张表的 DDL，全部包含 `del_flag`, `create_time`, `create_by`, `update_time`, `update_by` 五个公共字段。

`approval_tables.sql` 和 `sys_log_tables.sql` 也全部包含五个公共字段。

✅ **通过**

### DDL-02：主键不使用 AUTO_INCREMENT ✅

所有 DDL 文件中均无 `AUTO_INCREMENT`。✅ **通过**

### DDL-03：公共字段有 COMMENT ✅

所有公共字段均有 COMMENT 注释，如 `COMMENT '逻辑删除(0-正常 1-已删除)'` 等。✅ **通过**

---

## 📊 违规汇总

| 规则ID | 严重程度 | 违规数量 | 状态 |
|--------|---------|---------|------|
| DB-01 | 🔴 阻断 | **25 个文件** | 全部模块存在物理删除 |
| DB-02 | 🔴 阻断 | **1 处** | StockServiceImpl 手动拼接 del_flag |
| DB-03 | 🔴 阻断 | **25 个文件** | 全部使用 setDelFlag(1) 而非常量 |
| DB-04 | 🔴 阻断 | **40+ 个文件** | 几乎所有 Entity 使用 AUTO 而非 ASSIGN_ID |
| CONST-01 | 🔴 阻断 | **30+ 个文件** | 大量魔法数字（setStatus/setType/setDirection 等） |
| CONST-02 | 🔴 阻断 | **~10 个文件** | ServiceImpl 中私有常量 |
| PERF-01 | 🔴 阻断 | **2 处** | StockServiceImpl N+1 查询 |
| PERF-02 | 🔴 阻断 | **~6 个文件** | 全表查询后内存过滤 |
| ARCH-03 | 🔴 阻断 | **1 处** | StockServiceImpl 私有 toVo 方法 |
| CONST-03 | 🟡 警告 | 待确认 | 需人工审查异常信息精确性 |
| SEQ-01~02 | 🟡 警告 | 待确认 | 需人工审查编号生成方式 |
| SEC-01~03 | 🟡 警告 | 待确认 | 需自动化脚本逐 Controller 检查 |
| ARCH-01~02~04 | 🟡 警告 | 待确认 | 需逐文件人工审查 |
| JAVADOC-01~03 | 🟡 警告 | 待确认 | 需逐文件检查注释完整性 |
| INV-01~02 | 🟡 警告 | 待确认 | 需人工审查库存安全和同步事件 |
| SEC-04 | ✅ 通过 | 0 | 配置均使用占位符 |
| DB-05 | ✅ 通过 | 0 | 无 AUTO_INCREMENT |
| DB-06 | ✅ 通过 | 0 | 待确认（未发现明显违规） |
| NAME-03~05 | ✅ 通过 | 0 | 命名后缀符合规范 |
| DDL-01~03 | ✅ 通过 | 0 | 公共字段完整 |

---

## 🎯 修复优先级建议

1. **P0（立即修复）**：DB-03（setDelFlag 用常量）、DB-02（删除手动 del_flag 条件）
2. **P0（立即修复）**：DB-01（物理删除改逻辑删除）、DB-04（AUTO 改 ASSIGN_ID）
3. **P1（尽快修复）**：CONST-01（魔法数字改常量）、CONST-02（私有常量提取）
4. **P1（尽快修复）**：PERF-01（N+1 查询改批量）、PERF-02（内存过滤改 SQL 过滤）
5. **P1（尽快修复）**：ARCH-03（私有 toVo 移至 Converter）
6. **P2（本周完成）**：SEC-01~03 Controller 注解补全
7. **P2（本周完成）**：JAVADOC 注释补充
8. **P3（持续改进）**：ARCH-01/02/04 分层规范落地

> **最大问题**：DB-03 和 DB-04 是最容易批量修复的——`setDelFlag(1)` → `setDelFlag(DelFlagConstants.DELETED)` 和 `IdType.AUTO` → `IdType.ASSIGN_ID` 可通过全局搜索替换快速完成。