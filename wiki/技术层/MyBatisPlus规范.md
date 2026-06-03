# MyBatis-Plus 规范（含 DB-07 红线）

> **状态**: ⚠️ 占位（待补充）— 创建于 2026-06-02
> **负责人**: <TBD>
> **关联模块**: 全局（MyBatis-Plus 是 ORM 基础）
> **优先级**: 🟠 P1 — 影响所有后端代码

## 文档目标

- MyBatis-Plus 3.5.x 在本项目的统一配置（分页、乐观锁、字段填充）
- 9 条 DB-规则（DB-01~07、SEQ-01）的正确用法
- 雪花 ID 落地、动态表名（如有）
- 性能优化（批量操作、避免 N+1）

## 9 条 DB 规则速查

| 规则ID | 红线 | 正确做法 | check-rules.sh |
|--------|------|---------|----------------|
| **DB-01** | ❌ 物理删除 | 全部用 `LogicDeleteHelper.markDeleted(...)` | check_DB01 |
| **DB-02** | ❌ `.eq(::getDelFlag, 0)` 冗余条件 | MyBatis-Plus 全局已自动拼接，删除该行 | check_DB02 |
| **DB-03** | ❌ 逻辑删除只写 `1` | 必须用 `DelFlagConstants.DELETED` 常量 | check_DB03 |
| **DB-04** | ❌ `@TableId(type = IdType.AUTO)` | 必须 `@TableId(type = IdType.ASSIGN_ID)` | check_DB04 |
| **DB-05** | ❌ SQL `AUTO_INCREMENT` | DDL 用 `BIGINT NOT NULL COMMENT '主键(雪花ID)'` | check_DB05 |
| **DB-06** | ❌ 循环内 `mapper.insert(x)` | 用 `Db.saveBatch(list)` 批量 | check_DB06 |
| **DB-07** | ❌ `setDelFlag(DELETED) + updateById` / `Db.updateBatchById` | 用 `LogicDeleteHelper` 或 `UpdateWrapper.set("del_flag", ...)` | check_DB07 |
| **SEQ-01** | ❌ `likeRight + LIMIT 1 + COUNT` 取最大编号 | 用 `SequenceGenerator` Redis INCR | check_SEQ01 |
| **DDL-01** | ❌ 缺 `del_flag/create_time/create_by/update_time/update_by` | 5 字段全加 + COMMENT | check_DDL01 |

## 当前已知要点

- 基础依赖: MyBatis-Plus 3.5.5+（来自 [wms-server/wms-common/pom.xml](../../wms-server/wms-common/pom.xml)）
- 公共工具:
  - `wms-common/src/main/java/com/wms/common/util/LogicDeleteHelper.java` — 逻辑删除统一入口
  - `wms-common/src/main/java/com/wms/common/util/SequenceGenerator.java` — Redis INCR 序列号
- 全局配置位置: `wms-common/src/main/java/com/wms/common/config/MybatisPlusConfig.java`

## 待补内容

### 1. DB-03 / DB-07 详细说明（关键）

`delFlag` 字段标注 `@TableLogic` 后，**`setDelFlag(DELETED) + updateById` 与 `Db.updateBatchById` 这两类路径**会让 MyBatis-Plus 跳过对 `del_flag` 的写入，导致"前端删除 → 数据库 `del_flag` 仍为 0"。

```java
// ✅ 正确：使用 LogicDeleteHelper
LogicDeleteHelper.markDeleted(mapper, XxxEntity.class, id);
LogicDeleteHelper.markDeletedEntities(mapper, XxxEntity.class, entities);

// ✅ 正确：UpdateWrapper.set 显式写 del_flag
mapper.update(null, Wrappers.<XxxEntity>lambdaUpdate()
        .set(XxxEntity::getDelFlag, DelFlagConstants.DELETED)
        .eq(XxxEntity::getId, id));

// ❌ 错误：setDelFlag + updateById 模式
entity.setDelFlag(DelFlagConstants.DELETED);
mapper.updateById(entity);

// ❌ 错误：Db.updateBatchById 同样无效
Db.updateBatchById(updateList);
```

### 2. 雪花 ID 生成

- `IdType.ASSIGN_ID` 使用雪花算法
- 优势：分布式无碰撞、Long 主键索引快
- 运维注意：workerId 配置（如多实例部署）

### 3. 序列号生成（SEQ-01）

```java
// ✅ 正确：SequenceGenerator Redis INCR
@Autowired
private SequenceGenerator sequenceGenerator;

public String generateOrderNo(String prefix) {
    // 格式：prefix + yyyyMMdd + 4 位流水号
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    long seq = sequenceGenerator.nextVal(prefix + ":" + date);
    return prefix + date + String.format("%04d", seq);
}

// ❌ 错误：likeRight + LIMIT 1 + COUNT 查询最大编号+1
// 存在并发问题，多实例部署会重复
```

### 4. 批量操作

```java
// ✅ 正确：批量插入
List<WmsXxx> list = new ArrayList<>();
for (...) { list.add(xxx); }
Db.saveBatch(list);    // 1000 条/批

// ✅ 正确：批量逻辑删除
LogicDeleteHelper.markDeletedEntities(mapper, XxxEntity.class, entities);

// ✅ 正确：批量更新（UpdateWrapper 显式 set del_flag）
Db.update(null, Wrappers.<XxxEntity>lambdaUpdate()
        .set(XxxEntity::getDelFlag, DelFlagConstants.DELETED)
        .in(XxxEntity::getId, ids));
```

### 5. 字段填充（自动填充 createTime / updateTime / createBy / updateBy）

- `MetaObjectHandler` 实现位置: `wms-common/src/main/java/com/wms/common/config/MybatisPlusMetaObjectHandler.java`
- 字段: `createTime` / `updateTime` / `createBy` / `updateBy` / `delFlag`
- 从 `SecurityContextHolder` 拿当前用户写入 createBy/updateBy

### 6. 分页插件

- `PaginationInnerInterceptor`
- 单页最大 1000 条限制

### 7. 乐观锁（如有）

- `@Version` 注解
- `OptimisticLockerInnerInterceptor`

### 8. 动态表名（如有）

- `DynamicTableNameInnerInterceptor`
- 适用场景：按月分表（操作日志、审批历史等）

## 关联规则引用

- [AGENTS.md §3 数据操作规则](../AGENTS.md) — 9 条 DB 规则原文
- [AGENTS.md §5 性能规则](../AGENTS.md) — N+1、saveBatch、SequenceGenerator
- [编码约束.md](../../.harness/rules/%E7%BC%96%E7%A0%81%E7%BA%A6%E6%9D%9F.md) — DB-01~07、SEQ-01、DDL-01 详细说明
- [代码模板.md](../资产层/代码模板.md) — Entity / ServiceImpl 模板（已含 DB-07 正确示例）
- [check-rules.sh](../../scripts/check-rules.sh) — 自动化检查
