package com.wms.warehouse.controller;

import com.wms.common.domain.R;
import com.wms.warehouse.domain.dto.LayoutElementBatchSaveDto;
import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.vo.LayoutElementBatchSaveVo;
import com.wms.warehouse.domain.vo.LayoutElementVo;
import com.wms.warehouse.service.LayoutElementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 库房布局元素控制器单元测试
 * 验证Controller层参数转发和返回值封装
 */
@DisplayName("LayoutElementController 测试")
@ExtendWith(MockitoExtension.class)
class LayoutElementControllerTest {

    @Mock
    private LayoutElementService layoutElementService;

    private LayoutElementController controller;

    @BeforeEach
    void setUp() {
        controller = new LayoutElementController(layoutElementService);
    }

    @Nested
    @DisplayName("listByWarehouseId 查询接口测试")
    class ListTest {

        @Test
        @DisplayName("按库房ID查询应调用Service并返回R.ok包装")
        void shouldCallServiceAndWrapResult() {
            LayoutElementVo vo = new LayoutElementVo();
            vo.setId(1L);
            vo.setElementName("墙体1");
            when(layoutElementService.listByWarehouseId(100L, null)).thenReturn(List.of(vo));

            R<List<LayoutElementVo>> result = controller.listByWarehouseId(100L, null);

            assertNotNull(result);
            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().size());
            assertEquals("墙体1", result.getData().get(0).getElementName());
        }

        @Test
        @DisplayName("带区域ID筛选查询应传递areaId参数")
        void shouldPassAreaIdParameter() {
            when(layoutElementService.listByWarehouseId(100L, 200L)).thenReturn(List.of());

            R<List<LayoutElementVo>> result = controller.listByWarehouseId(100L, 200L);

            verify(layoutElementService).listByWarehouseId(100L, 200L);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("create 新增接口测试")
    class CreateTest {

        @Test
        @DisplayName("新增布局元素应调用Service.create并返回结果")
        void shouldCallCreateAndReturnResult() {
            LayoutElementDto dto = new LayoutElementDto();
            dto.setWarehouseId(100L);
            dto.setElementName("墙体");
            dto.setElementType("wall");
            dto.setShapeType("rect");

            LayoutElementVo vo = new LayoutElementVo();
            vo.setId(1L);
            vo.setElementName("墙体");
            when(layoutElementService.create(any(LayoutElementDto.class))).thenReturn(vo);

            R<LayoutElementVo> result = controller.create(dto);

            assertEquals(200, result.getCode());
            assertEquals("墙体", result.getData().getElementName());
        }
    }

    @Nested
    @DisplayName("update 更新接口测试")
    class UpdateTest {

        @Test
        @DisplayName("更新布局元素应调用Service.update并返回结果")
        void shouldCallUpdateAndReturnResult() {
            LayoutElementDto dto = new LayoutElementDto();
            dto.setWarehouseId(100L);
            dto.setElementName("更新墙体");

            LayoutElementVo vo = new LayoutElementVo();
            vo.setId(1L);
            vo.setElementName("更新墙体");
            when(layoutElementService.update(eq(1L), any(LayoutElementDto.class))).thenReturn(vo);

            R<LayoutElementVo> result = controller.update(1L, dto);

            assertEquals(200, result.getCode());
            assertEquals("更新墙体", result.getData().getElementName());
        }
    }

    @Nested
    @DisplayName("delete 删除接口测试")
    class DeleteTest {

        @Test
        @DisplayName("删除布局元素应调用Service.delete并返回成功")
        void shouldCallDeleteAndReturnSuccess() {
            doNothing().when(layoutElementService).delete(1L);

            R<Void> result = controller.delete(1L);

            assertEquals(200, result.getCode());
            verify(layoutElementService).delete(1L);
        }
    }

    @Nested
    @DisplayName("batchSave 批量保存接口测试")
    class BatchSaveTest {

        @Test
        @DisplayName("批量保存应调用Service.batchSave并返回结果")
        void shouldCallBatchSaveAndReturnResult() {
            LayoutElementBatchSaveDto dto = new LayoutElementBatchSaveDto();
            dto.setWarehouseId(100L);

            LayoutElementBatchSaveVo vo = new LayoutElementBatchSaveVo();
            vo.setCreatedCount(2);
            vo.setUpdatedCount(1);
            vo.setDeletedCount(0);
            when(layoutElementService.batchSave(any(LayoutElementBatchSaveDto.class))).thenReturn(vo);

            R<LayoutElementBatchSaveVo> result = controller.batchSave(dto);

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().getCreatedCount());
            assertEquals(1, result.getData().getUpdatedCount());
            assertEquals(0, result.getData().getDeletedCount());
        }
    }
}