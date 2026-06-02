package com.wms.warehouse.service.impl;

import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.converter.LayoutElementConverter;
import com.wms.warehouse.domain.constant.LayoutElementConstants;
import com.wms.warehouse.domain.dto.LayoutElementBatchSaveDto;
import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.dto.LayoutElementUpdateItemDto;
import com.wms.warehouse.domain.entity.WmsLayoutElement;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.LayoutElementBatchSaveVo;
import com.wms.warehouse.domain.vo.LayoutElementVo;
import com.wms.warehouse.mapper.WmsLayoutElementMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 库房布局元素服务实现类单元测试
 * 覆盖CRUD、批量保存、校验逻辑等核心业务路径
 */
@DisplayName("LayoutElementServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class LayoutElementServiceImplTest {

    @Mock
    private WmsLayoutElementMapper layoutElementMapper;

    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;

    @Mock
    private SequenceGenerator sequenceGenerator;

    private LayoutElementConverter layoutElementConverter;
    private LayoutElementServiceImpl layoutElementService;

    @BeforeEach
    void setUp() {
        layoutElementConverter = new LayoutElementConverter();
        layoutElementService = new LayoutElementServiceImpl(
                layoutElementMapper,
                wmsWarehouseMapper,
                sequenceGenerator,
                layoutElementConverter
        );
    }

    // ==================== listByWarehouseId ====================

    @Nested
    @DisplayName("listByWarehouseId 查询测试")
    class ListByWarehouseIdTest {

        @Test
        @DisplayName("按库房ID查询应返回布局元素VO列表")
        void shouldReturnVoListByWarehouseId() {
            WmsLayoutElement entity = createTestElement(1L, 100L, "墙体1", "wall", "rect");
            when(layoutElementMapper.selectList(any())).thenReturn(List.of(entity));

            List<LayoutElementVo> result = layoutElementService.listByWarehouseId(100L, null);

            assertEquals(1, result.size());
            assertEquals("墙体1", result.get(0).getElementName());
            assertEquals("wall", result.get(0).getElementType());
        }

        @Test
        @DisplayName("按库房ID和区域ID筛选应返回对应元素")
        void shouldFilterByAreaId() {
            WmsLayoutElement entity = createTestElement(1L, 100L, "通道1", "aisle", "rect");
            when(layoutElementMapper.selectList(any())).thenReturn(List.of(entity));

            List<LayoutElementVo> result = layoutElementService.listByWarehouseId(100L, 200L);

            assertEquals(1, result.size());
            assertEquals("通道1", result.get(0).getElementName());
        }

        @Test
        @DisplayName("库房无布局元素时应返回空列表")
        void shouldReturnEmptyListWhenNoElements() {
            when(layoutElementMapper.selectList(any())).thenReturn(List.of());

            List<LayoutElementVo> result = layoutElementService.listByWarehouseId(100L, null);

            assertTrue(result.isEmpty());
        }
    }

    // ==================== create ====================

    @Nested
    @DisplayName("create 新增测试")
    class CreateTest {

        @Test
        @DisplayName("新增布局元素应校验库房存在且启用，自动生成编码")
        void shouldCreateElementWithAutoCode() {
            WmsWarehouse warehouse = createEnabledWarehouse(100L);
            when(wmsWarehouseMapper.selectById(100L)).thenReturn(warehouse);
            when(sequenceGenerator.next(LayoutElementConstants.ELEMENT_CODE_PREFIX)).thenReturn("LE202606020001");
            when(layoutElementMapper.insert(any(WmsLayoutElement.class))).thenReturn(1);

            LayoutElementDto dto = createTestDto(100L, "墙体1", "wall", "rect");
            LayoutElementVo result = layoutElementService.create(dto);

            assertNotNull(result);
            assertEquals("墙体1", result.getElementName());
            assertEquals("wall", result.getElementType());
            assertEquals("LE202606020001", result.getElementCode());
            assertEquals(BizConstants.STATUS_ENABLED, result.getStatus());
        }

        @Test
        @DisplayName("新增时库房不存在应抛出BizException")
        void shouldThrowWhenWarehouseNotFound() {
            when(wmsWarehouseMapper.selectById(anyLong())).thenReturn(null);

            LayoutElementDto dto = createTestDto(999L, "墙体1", "wall", "rect");
            BizException ex = assertThrows(BizException.class, () -> layoutElementService.create(dto));
            // selectById已自动过滤逻辑删除记录，无法区分"不存在"和"已删除"，使用合并消息
            assertEquals("库房不存在或已删除", ex.getMessage());
        }

        @Test
        @DisplayName("新增时库房已删除（selectById返回null）应抛出BizException")
        void shouldThrowWhenWarehouseDeleted() {
            // MyBatis-Plus @TableLogic使selectById自动过滤delFlag=1的记录，返回null
            when(wmsWarehouseMapper.selectById(100L)).thenReturn(null);

            LayoutElementDto dto = createTestDto(100L, "墙体1", "wall", "rect");
            BizException ex = assertThrows(BizException.class, () -> layoutElementService.create(dto));
            assertEquals("库房不存在或已删除", ex.getMessage());
        }

        @Test
        @DisplayName("新增时库房已禁用应抛出BizException")
        void shouldThrowWhenWarehouseDisabled() {
            WmsWarehouse warehouse = createEnabledWarehouse(100L);
            warehouse.setStatus(BizConstants.STATUS_DISABLED);
            when(wmsWarehouseMapper.selectById(100L)).thenReturn(warehouse);

            LayoutElementDto dto = createTestDto(100L, "墙体1", "wall", "rect");
            BizException ex = assertThrows(BizException.class, () -> layoutElementService.create(dto));
            assertEquals("库房已禁用", ex.getMessage());
        }

        @Test
        @DisplayName("新增时未指定状态应默认为启用")
        void shouldDefaultToEnabledStatus() {
            WmsWarehouse warehouse = createEnabledWarehouse(100L);
            when(wmsWarehouseMapper.selectById(100L)).thenReturn(warehouse);
            when(sequenceGenerator.next(any())).thenReturn("LE202606020001");
            when(layoutElementMapper.insert(any(WmsLayoutElement.class))).thenReturn(1);

            LayoutElementDto dto = createTestDto(100L, "通道1", "aisle", "rect");
            // 不设置status，验证默认值
            LayoutElementVo result = layoutElementService.create(dto);

            assertEquals(BizConstants.STATUS_ENABLED, result.getStatus());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update 更新测试")
    class UpdateTest {

        @Test
        @DisplayName("更新布局元素应正确拷贝属性并返回VO")
        void shouldUpdateElementAndReturnVo() {
            WmsLayoutElement existing = createTestElement(1L, 100L, "旧墙体", "wall", "rect");
            when(layoutElementMapper.selectById(1L)).thenReturn(existing);
            when(layoutElementMapper.updateById(any(WmsLayoutElement.class))).thenReturn(1);

            LayoutElementDto dto = createTestDto(100L, "新墙体", "wall", "line");
            LayoutElementVo result = layoutElementService.update(1L, dto);

            assertNotNull(result);
            assertEquals("新墙体", result.getElementName());
            assertEquals("line", result.getShapeType());
        }

        @Test
        @DisplayName("更新不存在的元素应抛出BizException")
        void shouldThrowWhenElementNotFound() {
            when(layoutElementMapper.selectById(anyLong())).thenReturn(null);

            LayoutElementDto dto = createTestDto(100L, "墙体", "wall", "rect");
            BizException ex = assertThrows(BizException.class, () -> layoutElementService.update(999L, dto));
            // selectById已自动过滤逻辑删除记录，使用合并消息
            assertEquals("布局元素不存在或已删除", ex.getMessage());
        }

        @Test
        @DisplayName("更新已删除的元素（selectById返回null）应抛出BizException")
        void shouldThrowWhenElementDeleted() {
            // MyBatis-Plus @TableLogic使selectById自动过滤delFlag=1的记录，返回null
            when(layoutElementMapper.selectById(1L)).thenReturn(null);

            LayoutElementDto dto = createTestDto(100L, "墙体", "wall", "rect");
            BizException ex = assertThrows(BizException.class, () -> layoutElementService.update(1L, dto));
            assertEquals("布局元素不存在或已删除", ex.getMessage());
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete 删除测试")
    class DeleteTest {

        @Test
        @DisplayName("删除布局元素应执行逻辑删除")
        void shouldLogicallyDeleteElement() {
            WmsLayoutElement existing = createTestElement(1L, 100L, "墙体", "wall", "rect");
            when(layoutElementMapper.selectById(1L)).thenReturn(existing);


            try (MockedStatic<LogicDeleteHelper> mockedStatic = mockStatic(LogicDeleteHelper.class)) {
                mockedStatic.when(() -> LogicDeleteHelper.markDeleted(any(), any(), anyLong())).thenReturn(1);
                assertDoesNotThrow(() -> layoutElementService.delete(1L));
                mockedStatic.verify(() -> LogicDeleteHelper.markDeleted(eq(layoutElementMapper), eq(WmsLayoutElement.class), eq(1L)));
            }
        }

        @Test
        @DisplayName("删除不存在的元素应抛出BizException")
        void shouldThrowWhenDeleteNonExistent() {
            when(layoutElementMapper.selectById(anyLong())).thenReturn(null);

            BizException ex = assertThrows(BizException.class, () -> layoutElementService.delete(999L));
            // selectById已自动过滤逻辑删除记录，使用合并消息
            assertEquals("布局元素不存在或已删除", ex.getMessage());
        }

        @Test
        @DisplayName("删除已删除的元素（selectById返回null）应抛出BizException")
        void shouldThrowWhenDeleteAlreadyDeleted() {
            // MyBatis-Plus @TableLogic使selectById自动过滤delFlag=1的记录，返回null
            when(layoutElementMapper.selectById(1L)).thenReturn(null);

            BizException ex = assertThrows(BizException.class, () -> layoutElementService.delete(1L));
            assertEquals("布局元素不存在或已删除", ex.getMessage());
        }
    }

    // ==================== batchSave ====================

    @Nested
    @DisplayName("batchSave 批量保存测试")
    class BatchSaveTest {

        @Test
        @DisplayName("批量保存应正确处理新增、更新、删除操作")
        void shouldBatchSaveCreatedUpdatedDeleted() {
            WmsWarehouse warehouse = createEnabledWarehouse(100L);
            when(wmsWarehouseMapper.selectById(100L)).thenReturn(warehouse);
            when(sequenceGenerator.next(any())).thenReturn("LE202606020001");

            WmsLayoutElement existingElement = createTestElement(10L, 100L, "旧元素", "wall", "rect");
            when(layoutElementMapper.selectById(10L)).thenReturn(existingElement);
            when(layoutElementMapper.insert(any(WmsLayoutElement.class))).thenReturn(1);
            when(layoutElementMapper.updateById(any(WmsLayoutElement.class))).thenReturn(1);


            LayoutElementBatchSaveDto dto = new LayoutElementBatchSaveDto();
            dto.setWarehouseId(100L);

            // 新增1个元素
            LayoutElementDto createDto = createTestDto(100L, "新墙体", "wall", "rect");
            dto.setCreated(List.of(createDto));

            // 更新1个元素
            LayoutElementUpdateItemDto updateDto = createTestUpdateItemDto(10L, 100L, "更新墙体", "wall", "line");
            dto.setUpdated(List.of(updateDto));

            // 删除1个元素
            WmsLayoutElement deleteElement = createTestElement(20L, 100L, "待删除", "aisle", "rect");
            when(layoutElementMapper.selectById(20L)).thenReturn(deleteElement);
            dto.setDeletedIds(List.of(20L));

            try (MockedStatic<LogicDeleteHelper> mockedStatic = mockStatic(LogicDeleteHelper.class)) {
                mockedStatic.when(() -> LogicDeleteHelper.markDeleted(any(), any(), anyLong())).thenReturn(1);
                LayoutElementBatchSaveVo result = layoutElementService.batchSave(dto);

                assertEquals(1, result.getCreatedCount());
                assertEquals(1, result.getUpdatedCount());
                assertEquals(1, result.getDeletedCount());
                assertTrue(result.getFailedItems().isEmpty());
            }
        }

        @Test
        @DisplayName("批量保存新增失败时应记录失败项并继续处理")
        void shouldRecordFailedItemOnCreateError() {
            // 第一个库房不存在导致新增失败
            when(wmsWarehouseMapper.selectById(anyLong())).thenReturn(null);

            LayoutElementBatchSaveDto dto = new LayoutElementBatchSaveDto();
            dto.setWarehouseId(999L);
            LayoutElementDto createDto = createTestDto(999L, "失败元素", "wall", "rect");
            dto.setCreated(List.of(createDto));

            LayoutElementBatchSaveVo result = layoutElementService.batchSave(dto);

            assertEquals(0, result.getCreatedCount());
            assertEquals(1, result.getFailedItems().size());
            assertEquals("失败元素", result.getFailedItems().get(0).getElementName());
            assertEquals("库房不存在或已删除", result.getFailedItems().get(0).getReason());
        }

        @Test
        @DisplayName("批量保存更新失败时应记录失败项")
        void shouldRecordFailedItemOnUpdateError() {
            // 更新的元素不存在
            when(layoutElementMapper.selectById(999L)).thenReturn(null);

            LayoutElementBatchSaveDto dto = new LayoutElementBatchSaveDto();
            dto.setWarehouseId(100L);
            LayoutElementUpdateItemDto updateDto = createTestUpdateItemDto(999L, 100L, "不存在元素", "wall", "rect");
            dto.setUpdated(List.of(updateDto));

            LayoutElementBatchSaveVo result = layoutElementService.batchSave(dto);

            assertEquals(0, result.getUpdatedCount());
            assertEquals(1, result.getFailedItems().size());
            assertEquals("不存在元素", result.getFailedItems().get(0).getElementName());
        }

        @Test
        @DisplayName("批量保存删除失败时应记录失败项")
        void shouldRecordFailedItemOnDeleteError() {
            // 删除的元素不存在
            when(layoutElementMapper.selectById(999L)).thenReturn(null);

            LayoutElementBatchSaveDto dto = new LayoutElementBatchSaveDto();
            dto.setWarehouseId(100L);
            dto.setDeletedIds(List.of(999L));

            LayoutElementBatchSaveVo result = layoutElementService.batchSave(dto);

            assertEquals(0, result.getDeletedCount());
            assertEquals(1, result.getFailedItems().size());
            assertEquals("id=999", result.getFailedItems().get(0).getElementName());
        }

        @Test
        @DisplayName("批量保存空列表应返回全零结果")
        void shouldReturnZeroResultForEmptyLists() {
            LayoutElementBatchSaveDto dto = new LayoutElementBatchSaveDto();
            dto.setWarehouseId(100L);

            LayoutElementBatchSaveVo result = layoutElementService.batchSave(dto);

            assertEquals(0, result.getCreatedCount());
            assertEquals(0, result.getUpdatedCount());
            assertEquals(0, result.getDeletedCount());
            assertTrue(result.getFailedItems().isEmpty());
        }
    }

    // ==================== 辅助方法 ====================

    private WmsLayoutElement createTestElement(Long id, Long warehouseId, String name, String elementType, String shapeType) {
        WmsLayoutElement entity = new WmsLayoutElement();
        entity.setId(id);
        entity.setWarehouseId(warehouseId);
        entity.setElementCode("LE202606020001");
        entity.setElementName(name);
        entity.setElementType(elementType);
        entity.setShapeType(shapeType);
        entity.setPositionX(100);
        entity.setPositionY(200);
        entity.setLayoutWidth(300);
        entity.setLayoutHeight(50);
        entity.setRotation(0);
        entity.setPointData(null);
        entity.setStyleData(null);
        entity.setLabelText(null);
        entity.setSortOrder(0);
        entity.setStatus(BizConstants.STATUS_ENABLED);
        entity.setDelFlag(DelFlagConstants.NORMAL);
        return entity;
    }

    private LayoutElementDto createTestDto(Long warehouseId, String name, String elementType, String shapeType) {
        LayoutElementDto dto = new LayoutElementDto();
        dto.setWarehouseId(warehouseId);
        dto.setElementName(name);
        dto.setElementType(elementType);
        dto.setShapeType(shapeType);
        dto.setPositionX(100);
        dto.setPositionY(200);
        dto.setLayoutWidth(300);
        dto.setLayoutHeight(50);
        dto.setRotation(0);
        return dto;
    }

    private LayoutElementUpdateItemDto createTestUpdateItemDto(Long id, Long warehouseId, String name, String elementType, String shapeType) {
        LayoutElementUpdateItemDto dto = new LayoutElementUpdateItemDto();
        dto.setId(id);
        dto.setWarehouseId(warehouseId);
        dto.setElementName(name);
        dto.setElementType(elementType);
        dto.setShapeType(shapeType);
        dto.setPositionX(100);
        dto.setPositionY(200);
        dto.setLayoutWidth(300);
        dto.setLayoutHeight(50);
        dto.setRotation(0);
        return dto;
    }

    private WmsWarehouse createEnabledWarehouse(Long id) {
        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setId(id);
        warehouse.setStatus(BizConstants.STATUS_ENABLED);
        warehouse.setDelFlag(DelFlagConstants.NORMAL);
        return warehouse;
    }
}