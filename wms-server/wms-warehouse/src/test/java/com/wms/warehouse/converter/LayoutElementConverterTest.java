package com.wms.warehouse.converter;

import com.wms.common.constant.BizConstants;
import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.dto.LayoutElementUpdateItemDto;
import com.wms.warehouse.domain.entity.WmsLayoutElement;
import com.wms.warehouse.domain.vo.LayoutElementVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库房布局元素转换器单元测试
 * 验证Entity/Vo/Dto之间的转换正确性
 */
@DisplayName("LayoutElementConverter 测试")
class LayoutElementConverterTest {

    private LayoutElementConverter converter;

    @BeforeEach
    void setUp() {
        converter = new LayoutElementConverter();
    }

    @Test
    @DisplayName("Entity转Vo应正确映射所有字段")
    void shouldConvertEntityToVo() {
        WmsLayoutElement entity = new WmsLayoutElement();
        entity.setId(1L);
        entity.setWarehouseId(100L);
        entity.setAreaId(200L);
        entity.setElementCode("LE202606020001");
        entity.setElementName("墙体1");
        entity.setElementType("wall");
        entity.setShapeType("rect");
        entity.setPositionX(100);
        entity.setPositionY(200);
        entity.setLayoutWidth(300);
        entity.setLayoutHeight(50);
        entity.setRotation(45);
        entity.setPointData("[{\"x\":0,\"y\":0}]");
        entity.setStyleData("{\"fillColor\":\"#4a4a4a\"}");
        entity.setLabelText("主墙");
        entity.setSortOrder(10);
        entity.setStatus(BizConstants.STATUS_ENABLED);
        entity.setRemark("测试备注");

        LayoutElementVo vo = converter.toVo(entity);

        assertEquals(1L, vo.getId());
        assertEquals(100L, vo.getWarehouseId());
        assertEquals(200L, vo.getAreaId());
        assertEquals("LE202606020001", vo.getElementCode());
        assertEquals("墙体1", vo.getElementName());
        assertEquals("wall", vo.getElementType());
        assertEquals("rect", vo.getShapeType());
        assertEquals(100, vo.getPositionX());
        assertEquals(200, vo.getPositionY());
        assertEquals(300, vo.getLayoutWidth());
        assertEquals(50, vo.getLayoutHeight());
        assertEquals(45, vo.getRotation());
        assertEquals("[{\"x\":0,\"y\":0}]", vo.getPointData());
        assertEquals("{\"fillColor\":\"#4a4a4a\"}", vo.getStyleData());
        assertEquals("主墙", vo.getLabelText());
        assertEquals(10, vo.getSortOrder());
        assertEquals(BizConstants.STATUS_ENABLED, vo.getStatus());
        assertEquals("测试备注", vo.getRemark());
    }

    @Test
    @DisplayName("Entity列表转Vo列表应保持顺序和数量")
    void shouldConvertEntityListToVoList() {
        WmsLayoutElement e1 = new WmsLayoutElement();
        e1.setId(1L);
        e1.setElementName("墙体");
        WmsLayoutElement e2 = new WmsLayoutElement();
        e2.setId(2L);
        e2.setElementName("通道");

        List<LayoutElementVo> voList = converter.toVoList(List.of(e1, e2));

        assertEquals(2, voList.size());
        assertEquals("墙体", voList.get(0).getElementName());
        assertEquals("通道", voList.get(1).getElementName());
    }

    @Test
    @DisplayName("Dto转Entity应正确映射字段（不含编码和状态）")
    void shouldConvertDtoToEntity() {
        LayoutElementDto dto = new LayoutElementDto();
        dto.setWarehouseId(100L);
        dto.setAreaId(200L);
        dto.setElementName("预留区");
        dto.setElementType("reserved");
        dto.setShapeType("polygon");
        dto.setPositionX(50);
        dto.setPositionY(80);
        dto.setLayoutWidth(200);
        dto.setLayoutHeight(150);
        dto.setRotation(0);
        dto.setPointData("[{\"x\":0,\"y\":0},{\"x\":100,\"y\":0},{\"x\":50,\"y\":80}]");
        dto.setStyleData("{\"fillColor\":\"#fff7e6\"}");
        dto.setLabelText("预留A");
        dto.setSortOrder(5);
        dto.setRemark("预留区备注");

        WmsLayoutElement entity = converter.toEntity(dto);

        assertEquals(100L, entity.getWarehouseId());
        assertEquals(200L, entity.getAreaId());
        assertEquals("预留区", entity.getElementName());
        assertEquals("reserved", entity.getElementType());
        assertEquals("polygon", entity.getShapeType());
        assertEquals(50, entity.getPositionX());
        assertEquals(80, entity.getPositionY());
        assertEquals(200, entity.getLayoutWidth());
        assertEquals(150, entity.getLayoutHeight());
        assertEquals(0, entity.getRotation());
        assertNotNull(entity.getPointData());
        assertNotNull(entity.getStyleData());
        assertEquals("预留A", entity.getLabelText());
        assertEquals(5, entity.getSortOrder());
        assertEquals("预留区备注", entity.getRemark());
        // 编码和状态不应被设置（由Service层处理）
        assertNull(entity.getElementCode());
        assertNull(entity.getStatus());
    }

    @Test
    @DisplayName("UpdateItemDto属性拷贝到Entity应正确覆盖字段")
    void shouldCopyUpdateItemDtoToEntity() {
        LayoutElementUpdateItemDto dto = new LayoutElementUpdateItemDto();
        dto.setId(1L);
        dto.setWarehouseId(100L);
        dto.setAreaId(200L);
        dto.setElementName("更新墙体");
        dto.setElementType("wall");
        dto.setShapeType("line");
        dto.setPositionX(150);
        dto.setPositionY(250);
        dto.setLayoutWidth(400);
        dto.setLayoutHeight(60);
        dto.setRotation(90);
        dto.setPointData("[{\"x\":0,\"y\":0},{\"x\":400,\"y\":0}]");
        dto.setStyleData("{\"strokeColor\":\"#333\"}");
        dto.setLabelText("侧墙");
        dto.setSortOrder(20);
        dto.setRemark("更新备注");

        WmsLayoutElement entity = new WmsLayoutElement();
        entity.setId(1L);
        entity.setElementName("旧墙体");
        converter.copyToEntity(dto, entity);

        assertEquals(100L, entity.getWarehouseId());
        assertEquals("更新墙体", entity.getElementName());
        assertEquals("line", entity.getShapeType());
        assertEquals(150, entity.getPositionX());
        assertEquals(250, entity.getPositionY());
        assertEquals(400, entity.getLayoutWidth());
        assertEquals(90, entity.getRotation());
        assertEquals("侧墙", entity.getLabelText());
        assertEquals(20, entity.getSortOrder());
        assertEquals("更新备注", entity.getRemark());
    }

    @Test
    @DisplayName("空列表转Vo列表应返回空列表")
    void shouldReturnEmptyListForEmptyInput() {
        List<LayoutElementVo> voList = converter.toVoList(List.of());
        assertTrue(voList.isEmpty());
    }
}