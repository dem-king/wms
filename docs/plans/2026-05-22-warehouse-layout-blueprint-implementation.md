# Warehouse Layout Blueprint Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将甲方手绘库房布局在系统中落成“结构化库位模型 + 平面底图图层”的可维护方案，既支持规则货架与库位管理，也支持异形边界、通道、预留区、旋转货架和尺寸标注展示。

**Architecture:** 继续以 `warehouse -> area -> cabinet -> bin` 作为库存主模型，不把通道、墙体、尺寸箭头等辅助元素伪装成货架或库位。后端扩展 `warehouse/area/cabinet` 的布局字段，并新增独立的 `layout element` 模块承载底图、通道、文字和点位；前端在现有 `warehouse/visual` 工作台上增加“底图层 + 多边形/旋转渲染 + 配置入口”，先支持只读展示，再补布局编辑。

**Tech Stack:** Vue 3、TypeScript、Vue Konva、Vitest、Spring Boot 3、Java 17、MyBatis-Plus、JUnit 5、Mockito、MySQL

---

## Scope

- **In scope**
  - 在现有库房可视化基础上支持异形库房底图。
  - 支持区域多边形、旋转货架、辅助图元、尺寸标注和预留区。
  - 支持将甲方图纸拆成结构化数据后在系统中展示。
  - 支持后续继续扩展为拖拽编辑与批量保存。
- **Out of scope**
  - 不实现完整 CAD 级绘图器。
  - 不做图片自动识别生成布局。
  - 第一阶段不做复杂吸附、碰撞检测、自动排布。

## Delivery Strategy

- **Phase 1: 结构化展示版**
  - 先把规则货架、库位、区域和预留区在系统中展示出来。
  - 旋转货架先支持固定角度渲染，不开放自由旋转编辑。
- **Phase 2: 底图增强版**
  - 加入异形轮廓、尺寸箭头、文字标注、点位图元。
  - 支持与甲方平面图更接近的视觉表达。
- **Phase 3: 布局配置版**
  - 提供后台配置入口，支持底图元素与货架坐标维护。
  - 复用现有布局保存能力扩展到更多对象。

## Data Model Decisions

- `wms_warehouse` 新增布局元数据字段，作为整张画布的坐标基准。
- `wms_area` 新增形状和标注字段，用于表达矩形区、多边形区、预留区。
- `wms_cabinet` 新增宽高与旋转角字段，用于表达横放、竖放、斜放货架。
- 新建 `wms_layout_element`，专门存储墙体、通道、文字、尺寸箭头、立柱点位等非库存承载对象。
- 规则货架仍沿用 `wms_bin` 管理库位，不规则区域不强行生成库位。
- 所有状态值、元素类型、形状类型都放在常量类中，禁止在 ServiceImpl 内写魔法数字。

## Table And Field Proposal

- `wms_warehouse`
  - `layout_width` INT COMMENT '布局画布宽度'
  - `layout_height` INT COMMENT '布局画布高度'
  - `layout_scale` DECIMAL(10,2) COMMENT '布局比例尺'
  - `layout_background_version` VARCHAR(32) COMMENT '底图版本号'
- `wms_area`
  - `shape_type` VARCHAR(16) COMMENT '区域形状(rect/polygon)'
  - `polygon_points` TEXT COMMENT '多边形点位JSON'
  - `label_x` INT COMMENT '标题X坐标'
  - `label_y` INT COMMENT '标题Y坐标'
- `wms_cabinet`
  - `layout_width` INT COMMENT '渲染宽度'
  - `layout_height` INT COMMENT '渲染高度'
  - `rotation` INT COMMENT '旋转角度'
- `wms_layout_element`
  - `id` BIGINT NOT NULL COMMENT '主键(雪花ID)'
  - `warehouse_id` BIGINT NOT NULL COMMENT '所属库房ID'
  - `area_id` BIGINT DEFAULT NULL COMMENT '关联区域ID'
  - `element_code` VARCHAR(64) DEFAULT NULL COMMENT '元素编码'
  - `element_name` VARCHAR(100) NOT NULL COMMENT '元素名称'
  - `element_type` VARCHAR(32) NOT NULL COMMENT '元素类型(wall/aisle/reserved/device/text/dimension)'
  - `shape_type` VARCHAR(16) NOT NULL COMMENT '形状类型(line/rect/polygon/circle/text)'
  - `position_x` INT DEFAULT NULL COMMENT 'X坐标'
  - `position_y` INT DEFAULT NULL COMMENT 'Y坐标'
  - `layout_width` INT DEFAULT NULL COMMENT '宽度'
  - `layout_height` INT DEFAULT NULL COMMENT '高度'
  - `rotation` INT DEFAULT 0 COMMENT '旋转角度'
  - `point_data` TEXT DEFAULT NULL COMMENT '点位数据JSON'
  - `style_data` TEXT DEFAULT NULL COMMENT '样式数据JSON'
  - `label_text` VARCHAR(255) DEFAULT NULL COMMENT '展示文本'
  - `sort_order` INT DEFAULT 0 COMMENT '排序号'
  - `status` TINYINT DEFAULT 1 COMMENT '状态(1-启用 0-禁用)'
  - `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注'
  - 公共字段沿用标准公共字段模板。

## API Design

- 复用现有接口
  - `WarehouseController` 查询/编辑库房时返回和接收布局元数据。
  - `AreaController` 查询/编辑区域时返回和接收形状字段。
  - `CabinetController` 查询/编辑存放柜时返回和接收宽高、旋转角。
- 新增接口
  - `GET /warehouse/layout-element?warehouseId=...`
  - `POST /warehouse/layout-element`
  - `PUT /warehouse/layout-element/{id}`
  - `DELETE /warehouse/layout-element/{id}`
  - `POST /warehouse/layout-element/batch`
- 注解要求
  - 查询接口必须有 `@DataScope` 和 `@PreAuthorize("isAuthenticated()")`
  - 写接口必须有 `@OperLog` 和 `@PreAuthorize("isAuthenticated()")`

## Frontend Design

- 保留现有 `wms-web/src/views/warehouse/visual/index.vue` 工作台编排角色。
- 继续使用 `visual-layout.ts` 组装库存主模型。
- 新增 `layout-blueprint.ts` 负责底图元素、多边形区域、旋转柜体和尺寸标注的纯数据装配。
- `VisualStage.vue` 增加五层渲染顺序：
  - 底图层
  - 区域层
  - 货架层
  - 库位层
  - 标注层
- `VisualToolbar.vue` 增加底图开关、辅助元素开关、比例模式提示。
- `VisualSummary.vue` 增加“区域类型/图元类型/旋转角/预留区说明”摘要。
- `CabinetDetailDialog.vue` 保持库存细节职责，不混入底图编辑逻辑。

## Backend Design

- 领域常量
  - 新建 `LayoutElementConstants.java` 维护元素类型和形状类型常量。
- 实体/DTO/VO
  - 扩展 `WmsWarehouse`、`WarehouseDto`、`WarehouseVo`
  - 扩展 `WmsArea`、`AreaDto`、`AreaVo`
  - 扩展 `WmsCabinet`、`CabinetDto`、`CabinetVo`
  - 新建 `WmsLayoutElement`、`LayoutElementDto`、`LayoutElementVo`
- 服务与转换
  - 新建 `LayoutElementService` 与 `LayoutElementServiceImpl`
  - 新建 `LayoutElementConverter`
  - `CabinetConverter` 扩展宽高与旋转映射
- 控制器
  - 新增 `LayoutElementController`
  - 现有 `WarehouseController`、`AreaController`、`CabinetController` 扩展字段校验和返回

## Testing Strategy

- 后端优先写服务测试，覆盖：
  - 新字段映射
  - 区域归属校验
  - 布局元素按库房查询
  - 逻辑删除行为
- 前端优先写纯函数测试，覆盖：
  - 底图元素装配
  - 多边形区域渲染输入
  - 旋转柜体与规则库位共存
  - 图层顺序
  - 配置开关和空态/错误态
- 最后跑：
  - 后端模块测试
  - 前端可视化相关测试
  - 前端构建
  - 关键文件诊断

### Task 1: 固化数据库脚本与常量基线

**Files:**
- Create: `D:\Codes\WMS_code\database\2026-05-22_warehouse_layout_blueprint.sql`
- Modify: `D:\Codes\WMS_code\database\wms_ddl.sql`
- Modify: `D:\Codes\WMS_code\database\wms_full_init.sql`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\constant\LayoutElementConstants.java`
- Test: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\test\java\com\wms\warehouse\service\impl\CabinetServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldExposeLayoutFieldsWhenSavingCabinetLayout() {
    CabinetVo cabinetVo = new CabinetVo();
    cabinetVo.setLayoutWidth(240);
    cabinetVo.setLayoutHeight(80);
    cabinetVo.setRotation(30);

    assertEquals(240, cabinetVo.getLayoutWidth());
    assertEquals(80, cabinetVo.getLayoutHeight());
    assertEquals(30, cabinetVo.getRotation());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，提示 `CabinetVo` 缺少布局字段或相关映射未补齐。

**Step 3: Write minimal implementation**

```java
public final class LayoutElementConstants {
    private LayoutElementConstants() {}
    public static final String ELEMENT_TYPE_AISLE = "aisle";
    public static final String SHAPE_TYPE_POLYGON = "polygon";
}
```

- 在 `wms_ddl.sql` 和增量脚本中补齐新增列与 `wms_layout_element` 建表语句。
- 保证主键使用雪花 ID，不使用 `AUTO_INCREMENT`。
- 所有表包含标准公共字段和逻辑删除字段。

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

**Step 5: Commit**

```bash
git add database/wms_ddl.sql database/wms_full_init.sql database/2026-05-22_warehouse_layout_blueprint.sql wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/constant/LayoutElementConstants.java wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/CabinetServiceImplTest.java
git commit -m "feat: add warehouse layout blueprint schema"
```

### Task 2: 扩展库房/区域/存放柜后端字段

**Files:**
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\entity\WmsWarehouse.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\dto\WarehouseDto.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\vo\WarehouseVo.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\entity\WmsArea.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\dto\AreaDto.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\vo\AreaVo.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\entity\WmsCabinet.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\dto\CabinetDto.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\vo\CabinetVo.java`
- Modify: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\converter\CabinetConverter.java`
- Test: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\test\java\com\wms\warehouse\service\impl\CabinetServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldReturnCabinetLayoutSizeAndRotation() {
    CabinetVo cabinetVo = cabinetService.getById(101L);
    assertNotNull(cabinetVo);
    assertEquals(240, cabinetVo.getLayoutWidth());
    assertEquals(80, cabinetVo.getLayoutHeight());
    assertEquals(30, cabinetVo.getRotation());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因是实体、DTO、VO 或 Converter 缺少新字段。

**Step 3: Write minimal implementation**

```java
@Schema(description = "渲染宽度")
private Integer layoutWidth;

@Schema(description = "渲染高度")
private Integer layoutHeight;

@Schema(description = "旋转角度")
private Integer rotation;
```

- `WarehouseDto` 增加 `layoutWidth/layoutHeight/layoutScale/layoutBackgroundVersion`
- `AreaDto` 增加 `shapeType/polygonPoints/labelX/labelY`
- `CabinetDto` 增加 `layoutWidth/layoutHeight/rotation`
- DTO 字段要补校验注解，避免非法空值和负值。

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

**Step 5: Commit**

```bash
git add wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/entity/WmsWarehouse.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/WarehouseDto.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/WarehouseVo.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/entity/WmsArea.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/AreaDto.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/AreaVo.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/entity/WmsCabinet.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/CabinetDto.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/CabinetVo.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/converter/CabinetConverter.java wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/CabinetServiceImplTest.java
git commit -m "feat: extend warehouse layout metadata fields"
```

### Task 3: 新增布局元素后端模块

**Files:**
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\entity\WmsLayoutElement.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\dto\LayoutElementDto.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\domain\vo\LayoutElementVo.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\converter\LayoutElementConverter.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\mapper\WmsLayoutElementMapper.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\service\LayoutElementService.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\service\impl\LayoutElementServiceImpl.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\main\java\com\wms\warehouse\controller\LayoutElementController.java`
- Create: `D:\Codes\WMS_code\wms-server\wms-warehouse\src\test\java\com\wms\warehouse\service\impl\LayoutElementServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldListLayoutElementsByWarehouseId() {
    List<LayoutElementVo> result = layoutElementService.listByWarehouseId(10L);
    assertEquals(2, result.size());
    assertEquals("aisle", result.get(0).getElementType());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=LayoutElementServiceImplTest test`
Expected: FAIL，原因是布局元素模块尚不存在。

**Step 3: Write minimal implementation**

```java
@Data
@TableName("wms_layout_element")
public class WmsLayoutElement extends BaseEntity {
    private Long warehouseId;
    private Long areaId;
    private String elementType;
    private String shapeType;
    private String pointData;
}
```

- `LayoutElementController` 的查询方法加 `@DataScope` 和 `@PreAuthorize("isAuthenticated()")`
- 写方法加 `@OperLog` 和 `@PreAuthorize("isAuthenticated()")`
- 删除必须走逻辑删除，不允许物理删除

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=LayoutElementServiceImplTest test`
Expected: PASS

**Step 5: Commit**

```bash
git add wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/entity/WmsLayoutElement.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/LayoutElementDto.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/LayoutElementVo.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/converter/LayoutElementConverter.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/mapper/WmsLayoutElementMapper.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/service/LayoutElementService.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/service/impl/LayoutElementServiceImpl.java wms-server/wms-warehouse/src/main/java/com/wms/warehouse/controller/LayoutElementController.java wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/LayoutElementServiceImplTest.java
git commit -m "feat: add warehouse layout element module"
```

### Task 4: 前端类型与 API 契约接入

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\types\warehouse.d.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\warehouse\warehouse.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\warehouse\area.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\warehouse\cabinet.ts`
- Create: `D:\Codes\WMS_code\wms-web\src\api\warehouse\layout-element.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\api\warehouse\visual-api.spec.ts`

**Step 1: Write the failing test**

```ts
it('exposes layout element api and cabinet rotation fields', () => {
  expect(source).toContain('layoutWidth?: number')
  expect(source).toContain('layoutHeight?: number')
  expect(source).toContain('rotation?: number')
  expect(apiSource).toContain("'/warehouse/layout-element'")
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/api/warehouse/visual-api.spec.ts`
Expected: FAIL，提示类型或 API 文件不存在。

**Step 3: Write minimal implementation**

```ts
export interface WmsLayoutElementVo {
  id: EntityId
  warehouseId: EntityId
  elementType: string
  shapeType: string
  pointData?: string
}
```

- `warehouse.d.ts` 为 `WmsWarehouseVo/WmsAreaVo/WmsCabinetVo` 补齐布局字段
- 新增 `getLayoutElementList/createLayoutElement/updateLayoutElement/deleteLayoutElement`

**Step 4: Run test to verify it passes**

Run: `npm test -- src/api/warehouse/visual-api.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add wms-web/src/types/warehouse.d.ts wms-web/src/api/warehouse/warehouse.ts wms-web/src/api/warehouse/area.ts wms-web/src/api/warehouse/cabinet.ts wms-web/src/api/warehouse/layout-element.ts wms-web/src/api/warehouse/visual-api.spec.ts
git commit -m "feat: add warehouse layout blueprint api contracts"
```

### Task 5: 前端纯模型扩展到底图与异形布局

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\visual-layout.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\visual-layout.spec.ts`
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\layout-blueprint.ts`
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\layout-blueprint.spec.ts`

**Step 1: Write the failing test**

```ts
it('builds polygon areas and layout elements into blueprint model', () => {
  const model = buildLayoutBlueprintModel({
    warehouse,
    areas: [{ ...area, shapeType: 'polygon', polygonPoints: '[[0,0],[100,0],[80,60]]' }],
    cabinets: [{ ...cabinet, rotation: 30, layoutWidth: 220, layoutHeight: 72 }],
    layoutElements,
  })

  expect(model.areaShapes[0].shapeType).toBe('polygon')
  expect(model.cabinetShapes[0].rotation).toBe(30)
  expect(model.backgroundElements).toHaveLength(2)
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/visual-layout.spec.ts src/views/warehouse/visual/layout-blueprint.spec.ts`
Expected: FAIL，原因是新模型和测试夹具不存在。

**Step 3: Write minimal implementation**

```ts
export function buildLayoutBlueprintModel(input: LayoutBlueprintInput): LayoutBlueprintModel {
  return {
    areaShapes: buildAreaShapes(input.areas),
    cabinetShapes: buildCabinetShapes(input.cabinets),
    backgroundElements: buildBackgroundElements(input.layoutElements),
  }
}
```

- `visual-layout.ts` 继续负责库存主模型，不混入底图解析细节
- `layout-blueprint.ts` 负责 `polygonPoints/pointData/styleData` 的 JSON 解析和容错
- 所有纯函数测试必须覆盖错误 JSON 的兜底行为

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/visual-layout.spec.ts src/views/warehouse/visual/layout-blueprint.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add wms-web/src/views/warehouse/visual/visual-layout.ts wms-web/src/views/warehouse/visual/visual-layout.spec.ts wms-web/src/views/warehouse/visual/layout-blueprint.ts wms-web/src/views/warehouse/visual/layout-blueprint.spec.ts
git commit -m "feat: build warehouse layout blueprint model"
```

### Task 6: 扩展画布渲染层与交互编排

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualStage.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualToolbar.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualSummary.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\CabinetDetailDialog.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\index.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\index.spec.ts`

**Step 1: Write the failing test**

```ts
it('contains blueprint rendering and visibility toggles', () => {
  expect(source).toContain('showBlueprint')
  expect(source).toContain('showAuxiliaryElements')
  expect(source).toContain('layoutBlueprintModel')
  expect(stageSource).toContain('v-line')
  expect(stageSource).toContain('v-path')
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: FAIL，提示页面尚未编排底图模型和开关状态。

**Step 3: Write minimal implementation**

```ts
const showBlueprint = ref(true)
const showAuxiliaryElements = ref(true)
const layoutBlueprintModel = computed(() =>
  buildLayoutBlueprintModel({
    warehouse: currentWarehouse.value,
    areas,
    cabinets,
    layoutElements: layoutElementList.value,
  }),
)
```

- `VisualStage.vue` 按层渲染：
  - 墙体/边界
  - 通道/预留区
  - 区域
  - 旋转柜体
  - 库位
  - 文字与尺寸
- `CabinetDetailDialog.vue` 仍只展示柜和库位信息，不展示底图元素详情

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add wms-web/src/views/warehouse/visual/components/VisualStage.vue wms-web/src/views/warehouse/visual/components/VisualToolbar.vue wms-web/src/views/warehouse/visual/components/VisualSummary.vue wms-web/src/views/warehouse/visual/components/CabinetDetailDialog.vue wms-web/src/views/warehouse/visual/index.vue wms-web/src/views/warehouse/visual/index.spec.ts
git commit -m "feat: render warehouse blueprint layers in visual workbench"
```

### Task 7: 增加布局配置入口与保存闭环

**Files:**
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\layout-editor.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\layout-editor.spec.ts`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\index.vue`
- Modify: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\VisualToolbar.vue`
- Create: `D:\Codes\WMS_code\wms-web\src\views\warehouse\visual\components\BlueprintElementDialog.vue`

**Step 1: Write the failing test**

```ts
it('tracks pending blueprint changes separately from cabinet layout changes', () => {
  const next = reduceLayoutEditorState(state, {
    type: 'upsert-blueprint-element',
    elementId: 'temp-1',
    positionX: 120,
    positionY: 88,
  })

  expect(next.pendingBlueprintElementIds).toEqual(['temp-1'])
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/layout-editor.spec.ts`
Expected: FAIL，原因是编辑状态尚不支持底图元素。

**Step 3: Write minimal implementation**

```ts
if (action.type === 'upsert-blueprint-element') {
  return {
    ...state,
    pendingBlueprintElements: {
      ...state.pendingBlueprintElements,
      [action.elementId]: { positionX: action.positionX, positionY: action.positionY },
    },
  }
}
```

- 先支持最小配置能力：
  - 新增/编辑文字标注
  - 新增/编辑矩形区
  - 新增/编辑线段尺寸
- 不在这一轮做自由多边形拖点编辑，可先通过 JSON 点位或表单录入

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/layout-editor.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add wms-web/src/views/warehouse/visual/layout-editor.ts wms-web/src/views/warehouse/visual/layout-editor.spec.ts wms-web/src/views/warehouse/visual/index.vue wms-web/src/views/warehouse/visual/components/VisualToolbar.vue wms-web/src/views/warehouse/visual/components/BlueprintElementDialog.vue
git commit -m "feat: support warehouse blueprint configuration flow"
```

### Task 8: 联调、构建与验收清单

**Files:**
- Verify only

**Step 1: Run backend tests**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest,LayoutElementServiceImplTest test`
Expected: PASS

**Step 2: Run frontend tests**

Run: `npm test -- src/api/warehouse/visual-api.spec.ts src/views/warehouse/visual/visual-layout.spec.ts src/views/warehouse/visual/layout-blueprint.spec.ts src/views/warehouse/visual/layout-editor.spec.ts src/views/warehouse/visual/index.spec.ts`
Expected: PASS

**Step 3: Run frontend build**

Run: `npm run build`
Expected: PASS

**Step 4: Check diagnostics**

Run: VS Code diagnostics for all modified files
Expected: 无新增阻塞错误

**Step 5: Start local services**

Run: `mvn -pl wms-server/wms-app spring-boot:run`
Expected: 后端启动成功

Run: `npm run dev -- --host 0.0.0.0`
Expected: 前端启动成功，可打开 `warehouse/visual`

**Step 6: Manual verification checklist**

```text
1. 选择库房后可同时加载区域、货架、库位和底图元素。
2. 异形区域可显示为多边形，未出现坐标错位。
3. A2/A3 这类斜放货架可按 rotation 正确渲染。
4. 规则货架仍可显示 rows/cols 生成的库位网格。
5. 通道、预留区、尺寸箭头不会被误识别为货架或库位。
6. 快速定位命中库位后，所属货架与区域高亮链路保持正确。
7. 关闭底图显示时，结构化货架和库位仍可独立浏览。
8. 编辑并保存标注或辅助元素后，刷新页面数据保持一致。
```

## Risks And Mitigations

- **风险：甲方图纸是拍照手绘图，透视变形明显**
  - 应对：先把图纸整理成结构化清单和尺寸表，再录入系统。
- **风险：现有 `wms_area` 实体与数据库历史上有过字段不一致**
  - 应对：先补齐 DDL、增量 SQL、实体、DTO、VO 的一致性，再做渲染层开发。
- **风险：底图元素过多会让 `index.vue` 再次膨胀**
  - 应对：把底图解析与渲染逻辑收口在 `layout-blueprint.ts` 和 `VisualStage.vue`，页面只做编排。
- **风险：多边形编辑复杂度膨胀**
  - 应对：第一版只做表单录入或 JSON 点位录入，拖点编辑延后。

## Acceptance Criteria

- 用户能在 `warehouse/visual` 页面看到与甲方平面图基本一致的库房结构。
- 规则货架与库位仍保持现有库存业务能力，不因底图层而失真。
- 通道、预留区、墙体、尺寸标注都以辅助图元展示，不混淆为库存对象。
- 前后端字段命名保持小驼峰一致。
- 新增接口全部满足权限、数据范围、操作日志和逻辑删除规范。

## Rollout Suggestion

- 先用一间试点库房录入真实数据。
- 确认甲方接受“结构化布局 + 底图图层”的表达方式。
- 再批量扩到其他库房，避免先做通用编辑器后发现现场数据不标准。
