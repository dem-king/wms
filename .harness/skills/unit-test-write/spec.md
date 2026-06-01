# 单元测试编写 Skill

## 触发条件

编码评审通过（阶段4）后，进入测试执行阶段时自动加载本 Skill。

## 核心原则：改动驱动测试（Change-driven Testing）

改了哪个接口就测哪个接口，而非一刀切测最上层。

## 编写步骤

### Step 1: 识别变更点

从 tasks.md 和代码变更中识别以下测试目标：

| 变更类型 | 测试目标 | 测试层级 |
|---------|---------|---------|
| 新增 Service 方法 | ServiceImpl 方法 | 单元 |
| 修改业务逻辑 | 修改的 ServiceImpl 方法 | 单元 |
| 新增 Controller 方法 | Service 层（不直接测 Controller） | 单元 |
| 新增 Converter | Converter 类 | 单元 |
| 新增/修改 SQL | Mapper 查询方法（可选） | 集成 |

### Step 2: 测试用例设计

每个测试用例必须包含以下要素：

```java
/**
 * {测试场景描述}
 *  Given: {前置条件}
 *  When:  {执行动作}
 *  Then:  {验证结果}
 */
@Test
@DisplayName("测试场景描述")
void testXxx() {
    // Given: 前置条件
    // When: 执行动作
    // Then: 验证结果
}
```

### Step 3: 核心业务路径覆盖

对于 WMS 项目，以下业务路径必须覆盖：

#### 3.1 入库流程
- [ ] 正常路径：创建入库单 → 审核 → 完成
- [ ] 异常路径：库房不存在
- [ ] 异常路径：库房已禁用
- [ ] 异常路径：明细为空

#### 3.2 出库流程
- [ ] 正常路径：创建出库单 → 审核 → 完成（库存减少）
- [ ] 异常路径：库存不足（必须抛 BizException）
- [ ] 异常路径：物品不存在

#### 3.3 库存操作
- [ ] 库存增加（入库完成）
- [ ] 库存减少（出库完成）
- [ ] 库存不足时抛异常（禁止静默设为0）

#### 3.4 审批流程
- [ ] 审批通过 → 状态流转正确
- [ ] 审批拒绝 → 状态回退
- [ ] 审批超时 → 自动处理

### Step 4: Mock 策略

```java
@ExtendWith(MockitoExtension.class)
class XxxServiceImplTest {
    @Mock
    private WmsXxxMapper mapper;
    
    @Mock
    private XxxConverter converter;
    
    @Mock
    private SequenceGenerator sequenceGenerator;
    
    @InjectMocks
    private XxxServiceImpl service;
}
```

**Mock 原则**：
- Mapper 层必须 Mock（不连真实数据库）
- Converter 必须 Mock
- SequenceGenerator 必须 Mock
- 不 Mock 被测试的 Service 本身
- 不 Mock 纯工具类（如 StringUtil）

### Step 5: 断言规范

```java
// 正确：精确断言
assertNotNull(result);
assertEquals(OrderStatusEnum.DRAFT.getCode(), result.getStatus());
assertEquals(1, result.getItems().size());

// 正确：异常断言
BizException ex = assertThrows(BizException.class, () -> service.create(dto));
assertEquals("库存不足: itemId=123", ex.getMessage());

// 错误：模糊断言
assertTrue(result != null);
assertNotEquals(0, result.getItems().size());
```

### Step 6: 测试数据构造

```java
// 正确：使用有意义的测试数据
@Test
void testCreateInboundOrder() {
    WmsInboundOrderDto dto = new WmsInboundOrderDto();
    dto.setWarehouseId(1L);
    dto.setSupplierId(100L);
    // ...
}

// 错误：无意义的数据
@Test
void testCreate() {
    dto.setId(0L);
    dto.setName("test");
}
```

**数据原则**：
- ID 用有意义的数字（1L=正常实体，0L=边界值，-1L=异常值）
- 状态值用枚举（OrderStatusEnum.DRAFT.getCode()）
- 字符串用业务语义描述的名称

### Step 7: 测试互不依赖

```java
// 正确：每个测试独立
@Test
void testCreateOrder() { /* 独立设置 */ }

@Test
void testApproveOrder() { /* 独立设置 */ }

// 错误：测试间共享状态
@BeforeEach
void setUp() { sharedOrder = service.create(dto); } // 测试耦合
```

### Step 8: 命名规范

```java
// 正确：Given_When_Then 格式
@Test
void createInboundOrder_warehouseExists_returnsOrder() { }

@Test
void createInboundOrder_warehouseNotFound_throwsBizException() { }

// 错误：无意义命名
@Test
void test1() { }

@Test
void testCreate() { } // 过于笼统
```

## 质量门禁

| 门禁项 | 要求 |
|--------|------|
| 编译通过 | `mvn test-compile` exit 0 |
| 测试通过 | `mvn test` exit 0 |
| 测试数 > 0 | total_tests > 0 |
| 核心路径覆盖 | spec.md 中每个验收标准至少有一个测试用例 |

## 输出路径

- 测试文件 → `wms-server/{模块}/src/test/java/com/wms/{模块}/service/impl/XxxServiceImplTest.java`
- 测试报告 → `.harness/changes/{变更名}/testing/test_report.md`
