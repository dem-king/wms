# Warehouse Visual Layout Editor Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 `wms-web` 可视化工作台补齐布局编辑和保存流程，并为 `wms-server` 布局保存接口补齐校验、返回结构和测试。

**Architecture:** 前端在 `warehouse/visual` 下新增纯逻辑层，独立收敛编辑模式、待保存布局、保存动作参数和回写逻辑，页面只负责编排现有组件；后端在 `wms-warehouse` 中补齐布局保存 DTO 校验、区域归属校验和返回结构，保持现有 CRUD 与查询接口兼容。验证采用 TDD，先补前后端失败测试，再以最小实现让测试转绿，最后执行构建与真实联调。

**Tech Stack:** Vue 3, TypeScript, Vitest, Spring Boot 3, Java 17, JUnit 5, Mockito

---

### Task 1: 后端布局保存校验与返回结构

**Files:**
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/test/java/com/wms/warehouse/service/impl/CabinetServiceImplTest.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/CabinetLayoutBatchSaveDto.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/dto/CabinetLayoutItemDto.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/domain/vo/CabinetLayoutSaveVo.java`
- Modify: `d:/Codes/WMS_code/wms-server/wms-warehouse/src/main/java/com/wms/warehouse/service/impl/CabinetServiceImpl.java`

**Step 1: Write the failing test**

```java
@Test
void shouldRejectCabinetOutsideAreaWhenSaveLayout() {
    when(wmsCabinetMapper.selectBatchIds(List.of(101L))).thenReturn(List.of(
        buildCabinet(101L, 11L, "A柜", null, null)
    ));

    BizException exception = assertThrows(BizException.class, () -> cabinetService.saveLayout(dto));

    assertEquals("存放柜不属于当前区域", exception.getMessage());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: FAIL，原因是区域归属校验或返回结构未完整实现。

**Step 3: Write minimal implementation**

```java
if (!dto.getAreaId().equals(cabinet.getAreaId())) {
    throw new BizException("存放柜不属于当前区域");
}
result.setAreaId(dto.getAreaId());
result.setCabinets(savedCabinets);
```

**Step 4: Run test to verify it passes**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

### Task 2: 前端布局编辑纯逻辑

**Files:**
- Create: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/layout-editor.ts`
- Create: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/layout-editor.spec.ts`
- Modify: `d:/Codes/WMS_code/wms-web/src/types/warehouse.d.ts`
- Modify: `d:/Codes/WMS_code/wms-web/src/api/warehouse/cabinet.ts`

**Step 1: Write the failing test**

```ts
it('tracks pending cabinet positions and builds save payload for one area', () => {
  const editor = createLayoutEditorState(model)
  const next = reduceLayoutEditorState(model, editor, {
    type: 'move-cabinet',
    cabinetId: 101,
    positionX: 220,
    positionY: 160,
  })

  expect(next.pendingCabinetIds).toEqual([101])
  expect(buildLayoutSavePayload(model, next, 10)).toEqual({
    areaId: 10,
    cabinets: [{ id: 101, positionX: 220, positionY: 160, sortOrder: 10 }],
  })
})
```

**Step 2: Run test to verify it fails**

Run: `npm run test -- src/views/warehouse/visual/layout-editor.spec.ts`
Expected: FAIL，原因是纯逻辑文件和类型尚不存在。

**Step 3: Write minimal implementation**

```ts
export function reduceLayoutEditorState(...) {
  if (action.type === 'move-cabinet') {
    return {
      ...state,
      draftPositions: {
        ...state.draftPositions,
        [action.cabinetId]: { positionX: action.positionX, positionY: action.positionY },
      },
    }
  }
}
```

**Step 4: Run test to verify it passes**

Run: `npm run test -- src/views/warehouse/visual/layout-editor.spec.ts`
Expected: PASS

### Task 3: 前端工作台接入编辑与保存流程

**Files:**
- Modify: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/index.spec.ts`
- Modify: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/visual-state.spec.ts`
- Modify: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/components/VisualStage.vue`
- Modify: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/components/VisualToolbar.vue`
- Modify: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/components/VisualSummary.vue`
- Modify: `d:/Codes/WMS_code/wms-web/src/views/warehouse/visual/index.vue`

**Step 1: Write the failing test**

```ts
it('contains layout edit orchestration markers', () => {
  expect(source).toContain('isEditMode')
  expect(source).toContain('pendingLayoutCount')
  expect(source).toContain('handleSaveLayout')
  expect(source).toContain('handleCabinetPositionChange')
})
```

**Step 2: Run test to verify it fails**

Run: `npm run test -- src/views/warehouse/visual/index.spec.ts src/views/warehouse/visual/visual-state.spec.ts`
Expected: FAIL，原因是页面尚未编排编辑态与保存动作。

**Step 3: Write minimal implementation**

```ts
const isEditMode = computed(() => layoutEditor.value.isEditMode)
async function handleSaveLayout() {
  const payload = buildLayoutSavePayload(visualModel.value!, layoutEditor.value, selectedArea.value!.id)
  const response = await saveCabinetLayout(payload)
  applySavedLayout(...)
}
```

**Step 4: Run test to verify it passes**

Run: `npm run test -- src/views/warehouse/visual/index.spec.ts src/views/warehouse/visual/visual-state.spec.ts`
Expected: PASS

### Task 4: 完整验证与真实联调

**Files:**
- Verify only

**Step 1: Run focused tests**

Run: `mvn -pl wms-server/wms-warehouse -Dtest=CabinetServiceImplTest test`
Expected: PASS

Run: `npm run test -- src/views/warehouse/visual/layout-editor.spec.ts src/views/warehouse/visual/index.spec.ts src/views/warehouse/visual/visual-layout.spec.ts src/views/warehouse/visual/visual-state.spec.ts src/api/warehouse/visual-api.spec.ts`
Expected: PASS

**Step 2: Run build**

Run: `npm run build`
Expected: PASS

**Step 3: Start local services and record behavior**

Run: `mvn -pl wms-server/wms-app spring-boot:run`
Run: `npm run dev -- --host 0.0.0.0`
Expected: 前后端均启动成功，可访问可视化工作台页面进行真实联调。

**Step 4: Manual verification checklist**

```text
1. 切换编辑模式后，柜体可拖拽并出现待保存数量。
2. 保存成功后出现成功反馈，刷新后位置保持一致。
3. 保存失败时出现错误反馈，待保存布局仍保留。
```
