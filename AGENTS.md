# 备品备件库房管理平台 - 开发规则

> 本规则基于代码审查总结，所有开发必须严格遵守，立即生效。

---

## 一、安全规则（红线，零容忍）

### 1.1 禁止硬编码敏感信息

- 配置文件中密钥/密码必须使用环境变量：`${MYSQL_PASSWORD:默认值}`
- Java代码中禁止硬编码密码/密钥，使用 `@Value` 从配置读取
- JWT密钥、数据库密码、MinIO密钥等一律通过环境变量注入

```yaml
# 正确
password: ${MYSQL_PASSWORD:dev_default}
jwt-secret: ${JWT_SECRET:dev_default}
```

```java
// 正确
@Value("${wms.default-password:Wms@2024}")
private String defaultPassword;
```

### 1.2 查询接口必须加 @DataScope

所有Controller的查询方法（page/list/getById/search等）必须添加 `@DataScope` 注解，实现数据隔离。

```java
@DataScope
@PreAuthorize("isAuthenticated()")
@GetMapping
public R<PageResult<XxxVo>> page(...) { ... }
```

### 1.3 所有接口必须加 @PreAuthorize

所有Controller方法必须添加权限注解，最小粒度为 `@PreAuthorize("isAuthenticated()")`。

### 1.4 写操作必须加 @OperLog

增删改操作必须标注操作日志注解：

```java
@OperLog(module = "business", type = "新增", desc = "新增入库单")
@PreAuthorize("isAuthenticated()")
@PostMapping
public R<XxxVo> create(...) { ... }
```

---

## 二、分层架构规则

### 2.1 Controller不含业务逻辑

Controller仅做参数校验（`@Valid`）和调用Service，禁止包含：
- Entity→Vo转换逻辑
- `SecurityUtil` 调用（移到Service层）
- 任何业务判断或数据组装

### 2.2 禁止返回Entity实体

- Controller和Service的公共方法必须返回VO，禁止返回Entity
- 每个模块必须有对应的XxxVo类
- Entity→Vo转换在Service内部完成或使用Converter类

### 2.3 使用独立Converter类

禁止在Service中使用private toVo方法做转换，必须创建独立Converter类（以`Converter`结尾），便于复用。

```java
@Component
public class ItemConverter {
    public ItemVo toVo(WmsItem entity) { ... }
    public List<ItemVo> toVoList(List<WmsItem> entities) { ... }
}
```

---

## 三、数据操作规则

### 3.1 禁止物理删除

所有删除操作必须使用逻辑删除，严禁调用：
- `mapper.delete(wrapper)`
- `mapper.deleteById(id)`
- `mapper.deleteBatchIds(ids)`

### 3.2 逻辑删除必须设置lastOperType

```java
// 正确：手动设置 + updateById
entity.setDelFlag(1);
entity.setLastOperType("d");
mapper.updateById(entity);
```

```java
// 错误：只设置delFlag
entity.setDelFlag(1);
mapper.updateById(entity);
```

> `lastOperType` 取值：`"i"` 新增、`"u"` 更新、`"d"` 删除
> MetaObjectHandler 在 insert 时自动填充 `"i"`，update 时自动填充 `"u"`
> 手动设置的值不会被覆盖（strictFill 仅填充null值）

### 3.3 禁止手动拼接del_flag=0条件

MyBatis-Plus全局配置了逻辑删除，查询自动拼接 `del_flag=0`，手动添加会导致条件重复：

```java
// 错误
.eq(Xxx::getDelFlag, 0)

// 正确：不写，自动拼接
```

### 3.4 主键使用雪花ID

```java
@TableId(type = IdType.ASSIGN_ID)  // 正确
@TableId(type = IdType.AUTO)       // 错误
```

SQL建表不写 `AUTO_INCREMENT`，主键由应用层雪花算法生成。

---

## 四、业务逻辑规则

### 4.1 库存不足必须抛BizException

禁止静默修正库存为负的情况，必须抛出业务异常：

```java
// 正确
if (stock.getQuantity() < 0) {
    throw new BizException("库存不足: itemId=" + itemId);
}

// 错误
if (stock.getQuantity() < 0) {
    stock.setQuantity(0);
    log.warn("库存异常");
}
```

### 4.2 库存同步事件在审批后发布

归还/报废等业务单据，库存同步事件必须在 `submitOrder` 或审批通过后发布，禁止在 `createOrder` 时直接发布。

### 4.3 禁止魔法数字

状态值必须使用常量：

```java
private static final int STATUS_DRAFT = 0;
private static final int STATUS_PENDING = 1;
private static final int STATUS_COMPLETED = 5;

order.setStatus(STATUS_DRAFT);  // 正确
order.setStatus(0);             // 错误
```

### 4.4 异常信息必须精确

校验时区分不同业务场景，抛出不同异常信息：

```java
// 正确
if (warehouse == null) throw new BizException("库房不存在");
if (warehouse.getStatus() == 0) throw new BizException("库房已禁用");

// 错误
if (warehouse == null || warehouse.getStatus() == 0) throw new BizException("库房不存在");
```

---

## 五、性能规则

### 5.1 禁止N+1查询

列表查询中禁止逐条关联查询，必须批量查询构建Map：

```java
// 正确
Set<Long> categoryIds = items.stream().map(Item::getCategoryId).collect(Collectors.toSet());
Map<Long, Category> categoryMap = categoryMapper.selectBatchIds(categoryIds)
    .stream().collect(Collectors.toMap(Category::getId, Function.identity()));
for (Item item : items) {
    Category cat = categoryMap.get(item.getCategoryId());
}

// 错误
for (Item item : items) {
    Category cat = categoryMapper.selectById(item.getCategoryId());  // N+1
}
```

### 5.2 禁止全表查询后内存过滤

过滤条件必须在SQL层面完成，禁止先查全表再Java内存筛选。

### 5.3 批量插入用saveBatch

```java
// 正确
List<WmsBin> binList = new ArrayList<>();
for (...) { binList.add(bin); }
Db.saveBatch(binList);

// 错误
for (...) { mapper.insert(bin); }
```

### 5.4 编号生成保证并发安全

禁止使用"查询最大编号+1"方式，应使用Redis自增或分布式ID。

---

## 六、注释规则

### 6.1 Entity每个字段必须有中文注释

```java
/** 物品编码 */
@Schema(description = "物品编码")
private String itemCode;
```

### 6.2 类必须有JavaDoc

```java
/**
 * 入库单服务实现类
 * 处理入库单的创建、审核、完成等业务逻辑
 */
public class InboundOrderServiceImpl implements InboundOrderService { ... }
```

### 6.3 public方法必须有JavaDoc

```java
/**
 * 创建入库单
 * 校验库房是否存在且启用，生成入库单号，保存入库单及明细
 *
 * @param dto 入库单创建参数，包含库房ID、供应商ID、明细列表
 * @return 创建后的入库单VO
 */
@Override
@Transactional(rollbackFor = Exception.class)
public InboundOrderVo createOrder(InboundOrderDto dto) { ... }
```

### 6.4 关键逻辑必须有行内注释

注释说明**为什么**而非**是什么**，禁止无意义注释。

```java
// 正确：校验库房是否存在且为启用状态
// 正确：生成入库单号：格式为RK + 年月日 + 4位流水号
// 错误：赋值
// 错误：判断
```

---

## 七、配置与建表规则

### 7.1 生产环境禁止SQL日志输出

```yaml
# 生产
log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
# 开发
log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 7.2 Controller参数必须校验

```java
public R<List<BinVo>> batchCreate(@PathVariable Long cabinetId,
                                  @RequestParam @Min(1) Integer rows,
                                  @RequestParam @Min(1) Integer cols) { ... }
```

### 7.3 SQL建表必须包含完整公共字段

每张表的公共字段必须包含：

```sql
`del_flag`          TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
`create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`create_by`         VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
`update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`update_by`         VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
`last_oper_type`    VARCHAR(10)  DEFAULT NULL            COMMENT '最后操作类型(i-新增 u-更新 d-删除)',
`last_oper_time`    DATETIME     DEFAULT NULL            COMMENT '最后操作时间',
```

### 7.4 主键不使用AUTO_INCREMENT

```sql
`id` BIGINT NOT NULL COMMENT '主键(雪花ID)',
-- 禁止: `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
```

---

## 八、命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| DTO类 | 以`Dto`结尾 | `InboundOrderDto` |
| VO类 | 以`Vo`结尾（小写o） | `InboundOrderVo` |
| Converter类 | 以`Converter`结尾 | `ItemConverter` |
| 状态常量 | 全大写下划线 | `STATUS_DRAFT`、`STATUS_PENDING` |

---

## 九、PR自查清单（提交前必须逐项确认）

```
□ 配置文件无硬编码密钥/密码？
□ Controller查询方法有@DataScope？
□ 所有方法有@PreAuthorize？
□ 写操作有@OperLog？
□ Controller不含业务逻辑（无Entity转换/SecurityUtil）？
□ 返回VO而非Entity？
□ 删除用逻辑删除（setDelFlag+setLastOperType("d")+updateById）？
□ 无.eq(::getDelFlag, 0)冗余条件？
□ 无物理删除（mapper.delete/deleteById）？
□ 无魔法数字？
□ 无N+1查询？
□ 无全表查询+内存过滤？
□ Entity字段有@Schema+JavaDoc注释？
│  类有JavaDoc？
│  public方法有JavaDoc？
│  关键逻辑有行内注释？
□ SQL表有last_oper_type+last_oper_time字段？
□ 主键策略为ASSIGN_ID（雪花ID）？
□ Controller参数有校验注解？
│  状态值使用常量而非魔法数字？
│  VO类以Vo结尾（小写o）？
```
