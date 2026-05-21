# Coding Skill

## 触发条件

进入编码实现阶段时自动加载本 Skill。

## 编码前检查清单

在编写任何代码之前，必须完成以下检查：

1. [ ] 已读取 `.harness/rules/编码约束.md`
2. [ ] 已读取 `.harness/rules/工程结构.md`
3. [ ] 已理解需求（spec.md）
4. [ ] 已确认变更范围（tasks.md）

## 分层编码规范

### 一、Entity 层编码规范

```java
// 必须继承 BaseEntity
// 主键必须用 @TableId(type = IdType.ASSIGN_ID)
// 每个字段必须有 @Schema 中文注释
// 逻辑删除由 MyBatis-Plus 自动处理（@TableLogic）

@Data
@TableName("wms_xxx")
public class WmsXxx extends BaseEntity {
    @Schema(description = "业务字段中文名")
    private String fieldName;
}
```

### 二、DTO 层编码规范

```java
// 以 Dto 结尾
// 字段名使用小驼峰，与前端参数名一致
// 公共入参校验注解

@Data
public class XxxDto {
    @NotNull(message = "字段不能为空")
    private Long warehouseId;
}
```

### 三、Mapper 层编码规范

```java
// 继承 BaseMapper
// 不写物理删除方法
// 不手动拼接 del_flag 条件

public interface WmsXxxMapper extends BaseMapper<WmsXxx> {
}
```

### 四、Converter 层编码规范

```java
// @Component 注解，独立文件
// 以 Converter 结尾
// 提供 toVo(entity, map) 和 toVoList(list, map) 方法
// 关联信息从 Map 中获取，无 Map 时 fallback 到 mapper 单条查询

@Component
@RequiredArgsConstructor
public class XxxConverter {
    private final YyyMapper yyyMapper;

    public XxxVo toVo(WmsXxx entity, Map<Long, Yyy> yyyMap) {
        // 字段映射
        // 关联字段从 Map 获取
    }
}
```

### 五、Service 层编码规范

```java
// 依赖注入：使用 @RequiredArgsConstructor + private final
// 写操作必须 @Transactional(rollbackFor = Exception.class)
// 查询操作必须用批量 selectBatchIds + Map 避免 N+1
// 删除操作：setDelFlag(DELETED) + updateById（逻辑删除）
// 库存同步：库存不足抛 BizException
// 编号生成：调用 sequenceGenerator.next(prefix)
// 状态值：使用枚举常量（如 OrderStatusEnum.DRAFT.getCode()）
// 常量：引用常量类（如 DelFlagConstants.DELETED），禁止 ServiceImpl 内 private static final

@Service
@RequiredArgsConstructor
public class XxxServiceImpl implements XxxService {
    private final WmsXxxMapper mapper;
    private final XxxConverter converter;
    private final SequenceGenerator sequenceGenerator;
}
```

### 六、Controller 层编码规范

```java
// 每个方法必须有 @PreAuthorize
// 查询方法必须有 @DataScope
// 写操作方法必须有 @OperLog
// 方法体仅做参数校验 + 调用 Service + 包装返回
// 参数必须有校验注解（@Min/@NotBlank 等）

@RestController
@RequestMapping("/api/xxx")
public class XxxController {
    private final XxxService xxxService;

    @DataScope
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public R<PageResult<XxxVo>> page(PageParam pageParam, ...) {
        return R.ok(xxxService.page(pageParam, ...));
    }

    @OperLog(module = "module", type = "新增", desc = "新增XXX")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public R<XxxVo> create(@Valid @RequestBody XxxDto dto) {
        return R.ok(xxxService.create(dto));
    }
}
```

## 关键业务约束速查

| 约束项 | 要求 |
|--------|------|
| 编号生成 | `sequenceGenerator.next(prefix)` — Redis INCR 原子操作 |
| 逻辑删除 | `updateEntity.setDelFlag(DelFlagConstants.DELETED); mapper.updateById(updateEntity);` |
| 库存不足 | `throw new BizException("库存不足: ...")` |
| N+1 避免 | `selectBatchIds(ids) → stream → toMap → 循环中 map.get()` |
| 状态常量 | `OrderStatusEnum.DRAFT.getCode()`，不能用数字 0 |
| 异常信息 | 分场景精确异常，不可合并 |