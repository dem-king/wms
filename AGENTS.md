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

### 3.2 逻辑删除

`delFlag` 字段标注了 `@TableLogic`，MyBatis-Plus 对逻辑删除字段有特殊处理。**禁止**通过 `setDelFlag(DELETED) + updateById` 或 `Db.updateBatchById` 执行删除，这类更新可能不会真正写入 `del_flag=1`，会导致前端点击删除后数据仍然存在。

```java
// 正确：使用统一逻辑删除工具，显式更新 del_flag 字段
LogicDeleteHelper.markDeleted(mapper, XxxEntity.class, id);
LogicDeleteHelper.markDeletedEntities(mapper, XxxEntity.class, entities);
```

如确实不能使用工具类，必须使用 `UpdateWrapper.set("del_flag", DelFlagConstants.DELETED)` 或专用 Mapper SQL 显式更新 `del_flag`。

```java
// 正确：显式 set del_flag，避免 @TableLogic 字段被普通更新跳过
mapper.update(null, Wrappers.<XxxEntity>lambdaUpdate()
        .set(XxxEntity::getDelFlag, DelFlagConstants.DELETED)
        .eq(XxxEntity::getId, id));

// 错误：逻辑删除字段可能不会被写入数据库
entity.setDelFlag(DelFlagConstants.DELETED);
mapper.updateById(entity);
Db.updateBatchById(updateList);
```

该规则适用于主表、关联表、单据明细、报表旧记录清理等所有需要逻辑删除的数据。

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

状态值、类型值、标志位等**禁止直接写数字或字符串字面量**，必须使用常量类或枚举引用：

#### 4.3.1 常量类规范

- **公共常量**（多模块复用）放在 `wms-common` 的 `com.wms.common.constant` 包下
- **模块常量**（单模块使用）放在各模块 `domain.constant` 包下
- 常量类以 `Constants` 结尾，使用 `public final class` + 私有构造函数
- 每个常量必须有中文JavaDoc注释

```java
/**
 * 逻辑删除常量类
 */
public final class DelFlagConstants {
    private DelFlagConstants() {}
    /** 逻辑删除：正常（未删除） */
    public static final int NORMAL = 0;
    /** 逻辑删除：已删除 */
    public static final int DELETED = 1;
}
```

#### 4.3.2 现有公共常量类

| 常量类 | 模块 | 包含常量 |
|--------|------|---------|
| `DelFlagConstants` | wms-common | NORMAL=0, DELETED=1 |
| `BizConstants` | wms-common | STATUS_ENABLED/DISABLED, DEFAULT_SORT_ORDER, TOP_PARENT_ID, STOCK_SYNC_IN/OUT |
| `WarehouseConstants` | wms-warehouse | 编码前缀(KF/QY/CG), IS_OCCUPIED_NO/YES |
| `ItemConstants` | wms-item | 编码前缀(WP), IS_CONSUMABLE_NO, IS_RETURNABLE_YES, DEFAULT_STOCK_QTY, QUICK_SEARCH_LIMIT |
| `LabelConstants` | wms-item | 编码前缀(BQ), 打印状态, RFID类型, 闲置阈值 |
| `TagConstants` | wms-item | SCOPE_TYPE_GLOBAL |
| `OrderConstants` | wms-business | 单据编号前缀(RK/CK/GH/BF/DB) |
| `AuthConstants` | wms-auth | 验证码参数, 限流窗口, 缓存过期, 操作结果, UA长度 |
| `TokenConstants` | wms-auth | TOKEN_TYPE_BEARER, HMAC_KEY_LENGTH, REVOKE_FLAG, TOKEN_TYPE_REFRESH |
| `SysMenuConstants` | wms-system | 菜单状态/可见性/类型常量 |

#### 4.3.3 使用示例

```java
// 正确：使用常量类引用
updateWrapper.set("del_flag", DelFlagConstants.DELETED);
order.setStatus(OrderStatusEnum.DRAFT.getCode());
warehouse.setStatus(BizConstants.STATUS_ENABLED);
event.setDirection(BizConstants.STOCK_SYNC_IN);

// 错误：魔法数字
updateWrapper.set("del_flag", 1);
order.setStatus(0);
warehouse.setStatus(1);
event.setDirection("IN");
```

#### 4.3.4 禁止在ServiceImpl中定义局部常量

ServiceImpl中**禁止**使用 `private static final` 定义业务常量，必须提取到对应的常量类或枚举中：

```java
// 错误：ServiceImpl中的局部常量
public class InboundServiceImpl {
    private static final int STATUS_DRAFT = 0;
    private static final String ORDER_NO_PREFIX = "RK";
}

// 正确：引用常量类/枚举
public class InboundServiceImpl {
    // 直接使用 OrderStatusEnum.DRAFT.getCode() 和 OrderConstants.INBOUND_NO_PREFIX
}
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

### 8.1 前后端参数命名一致

前后端接口的请求参数和响应字段命名必须保持一致，避免前后端因命名不匹配导致数据绑定失败或字段丢失：

- 后端DTO/VO的字段名必须与前端API请求/响应的参数名一致
- 后端字段使用小驼峰（camelCase），前端对应参数也使用小驼峰
- 禁止后端用`warehouseId`而前端用`warehouse_id`等命名不一致的情况

```java
// 后端DTO
public class InboundOrderDto {
    private Long warehouseId;    // 后端：小驼峰
    private String orderNo;
}
```

```typescript
// 前端API参数：命名与后端保持一致
interface InboundOrderParams {
    warehouseId: number;    // 前端：与后端一致，使用小驼峰
    orderNo: string;
}
```

---

## 九、PR自查清单（提交前必须逐项确认）

```
□ 配置文件无硬编码密钥/密码？
□ Controller查询方法有@DataScope？
□ 所有方法有@PreAuthorize？
□ 写操作有@OperLog？
□ Controller不含业务逻辑（无Entity转换/SecurityUtil）？
□ 返回VO而非Entity？
□ 删除用显式逻辑删除（LogicDeleteHelper 或 UpdateWrapper.set("del_flag", DELETED)）？
□ 无 setDelFlag(DELETED) + updateById/Db.updateBatchById 删除模式？
□ 无.eq(::getDelFlag, 0)冗余条件？
□ 无物理删除（mapper.delete/deleteById）？
□ 无魔法数字？
□ 无N+1查询？
□ 无全表查询+内存过滤？
□ Entity字段有@Schema+JavaDoc注释？
│  类有JavaDoc？
│  public方法有JavaDoc？
│  关键逻辑有行内注释？
□ 主键策略为ASSIGN_ID（雪花ID）？
□ Controller参数有校验注解？
│  状态值使用常量类/枚举而非魔法数字？
│  ServiceImpl中无private static final局部常量？
│  逻辑删除使用DelFlagConstants而非直接写1？
│  VO类以Vo结尾（小写o）？
□ 前后端参数命名一致（小驼峰，无下划线混用）？
```
