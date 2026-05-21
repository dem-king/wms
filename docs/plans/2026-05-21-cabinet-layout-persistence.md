# Cabinet Layout Persistence Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 `wms-server/wms-warehouse` 增加基于 `wms_cabinet` 的最小可用布局持久化能力，支持稳定排序、批量保存布局，并保持现有 CRUD 接口兼容。

**Architecture:** 直接扩展 `WmsCabinet` 的布局字段，不引入独立布局表。通过新增布局批量保存 DTO/VO/Service/Controller 接口，将区域下多个存放柜的位置与排序一次性持久化；列表查询按 `sortOrder + id` 稳定排序返回，详情和现有 CRUD 保持兼容。为满足仓库规则，新增独立 `CabinetConverter` 承担 Entity 与 VO 转换。

**Tech Stack:** Java 17, Spring Boot 3, MyBatis-Plus, JUnit 5, Mockito

---

### Task 1: 建立测试与计划基线

**Files:**
- Create: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/CabinetServiceImplTest.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/pom.xml`

**Step 1: Write the failing test**

```java
@Test
void shouldReturnCabinetsByStableSortOrderWhenListByAreaId() {
    when(wmsCabinetMapper.selectList(any())).thenReturn(List.of(second, first));

    List<CabinetVo> result = cabinetService.listByAreaId(10L);

    assertEquals(List.of(1, 2), result.stream().map(CabinetVo::getSortOrder).toList());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因是缺少测试依赖或实现中未包含 `sortOrder`/批量布局能力。

**Step 3: Write minimal implementation**

```java
// 在模块中补充 spring-boot-starter-test 依赖，并为 CabinetServiceImpl 补出测试可执行基础
```

**Step 4: Run test to verify it still fails for business gap**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因转为业务断言失败，说明 TDD 红灯成立。

### Task 2: 为存放柜补充布局字段与转换器

**Files:**
- Create: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/converter/CabinetConverter.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/entity/WmsCabinet.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/CabinetDto.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/CabinetVo.java`

**Step 1: Write the failing test**

```java
@Test
void shouldDefaultSortOrderWhenCreateCabinetWithoutSortOrder() {
    // assertEquals(BizConstants.DEFAULT_SORT_ORDER, result.getSortOrder());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因是 `CabinetVo` 尚未承载 `sortOrder` 或 create 未设置默认排序。

**Step 3: Write minimal implementation**

```java
entity.setSortOrder(dto.getSortOrder());
if (entity.getSortOrder() == null) {
    entity.setSortOrder(BizConstants.DEFAULT_SORT_ORDER);
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

### Task 3: 增加批量保存布局 DTO/VO/Service 能力

**Files:**
- Create: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/CabinetLayoutItemDto.java`
- Create: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/CabinetLayoutBatchSaveDto.java`
- Create: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/CabinetLayoutSaveVo.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/service/CabinetService.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/service/impl/CabinetServiceImpl.java`
- Test: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/CabinetServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldBatchSaveCabinetLayoutAndReturnSavedPositions() {
    CabinetLayoutSaveVo result = cabinetService.saveLayout(dto);
    assertEquals(2, result.getCabinets().size());
    assertEquals(10, result.getCabinets().get(0).getPositionX());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因是接口不存在或返回结构不匹配。

**Step 3: Write minimal implementation**

```java
for (CabinetLayoutItemDto item : dto.getCabinets()) {
    WmsCabinet updateEntity = new WmsCabinet();
    updateEntity.setId(item.getId());
    updateEntity.setPositionX(item.getPositionX());
    updateEntity.setPositionY(item.getPositionY());
    updateEntity.setSortOrder(item.getSortOrder());
    wmsCabinetMapper.updateById(updateEntity);
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

### Task 4: 暴露 Controller 接口并保持兼容

**Files:**
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/controller/CabinetController.java`
- Test: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/CabinetServiceImplTest.java`

**Step 1: Write the failing test**

```java
@Test
void shouldRejectCabinetOutsideAreaWhenSaveLayout() {
    assertThrows(BizException.class, () -> cabinetService.saveLayout(dto));
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因是未校验区域归属或异常信息不精确。

**Step 3: Write minimal implementation**

```java
@PostMapping("/area/{areaId}/layout")
@PreAuthorize("isAuthenticated()")
@OperLog(module = "warehouse", type = "更新", desc = "批量保存存放柜布局")
public R<CabinetLayoutSaveVo> saveLayout(...) { ... }
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

### Task 5: 完整验证与诊断

**Files:**
- Verify only

**Step 1: Run focused module tests**

Run: `mvn -pl wms-warehouse test`
Expected: PASS

**Step 2: Run diagnostics**

Run tool: `GetDiagnostics` for edited files
Expected: no new compile/linter errors

**Step 3: Summarize compatibility**

```text
现有 CRUD 路径保持不变；新增字段仅追加，不破坏旧请求。
```
