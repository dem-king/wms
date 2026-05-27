package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.converter.CabinetConverter;
import com.wms.warehouse.domain.dto.CabinetDto;
import com.wms.warehouse.domain.dto.CabinetLayoutBatchSaveDto;
import com.wms.warehouse.domain.dto.CabinetLayoutItemDto;
import com.wms.warehouse.domain.dto.AreaDto;
import com.wms.warehouse.domain.dto.WarehouseDto;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.AreaVo;
import com.wms.warehouse.domain.vo.CabinetLayoutSaveVo;
import com.wms.warehouse.domain.vo.CabinetVo;
import com.wms.warehouse.domain.vo.WarehouseVo;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 存放柜服务单元测试
 * 验证布局排序、默认排序和批量保存布局能力
 */
@DisplayName("CabinetServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class CabinetServiceImplTest {

    @Mock
    private WmsCabinetMapper wmsCabinetMapper;

    @Mock
    private WmsAreaMapper wmsAreaMapper;

    @Mock
    private WmsBinMapper wmsBinMapper;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Spy
    private CabinetConverter cabinetConverter;

    @InjectMocks
    private CabinetServiceImpl cabinetService;

    @Test
    @DisplayName("按区域查询时应按排序号和ID稳定排序返回布局结果")
    void shouldReturnCabinetsByStableSortOrderWhenListByAreaId() {
        WmsCabinet sortLater = buildCabinet(2002L, 10L, "B柜", 30, 40);
        WmsCabinet sortFirst = buildCabinet(2001L, 10L, "A柜", 10, 20);
        sortLater.setSortOrder(2);
        sortFirst.setSortOrder(1);
        when(wmsCabinetMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(sortFirst, sortLater));

        List<CabinetVo> result = cabinetService.listByAreaId(10L);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getSortOrder());
        assertEquals(10, result.get(0).getPositionX());
        assertEquals(20, result.get(0).getPositionY());
        assertEquals(2, result.get(1).getSortOrder());
    }

    @Test
    @DisplayName("存放柜转换器应映射布局宽高和旋转角")
    void shouldMapCabinetLayoutFieldsBetweenDtoEntityAndVo() {
        CabinetDto dto = new CabinetDto();
        dto.setAreaId(10L);
        dto.setWarehouseId(20L);
        dto.setCabinetName("测试柜");
        dto.setLayoutWidth(240);
        dto.setLayoutHeight(80);
        dto.setRotation(30);

        WmsCabinet entity = cabinetConverter.toEntity(dto);

        assertEquals(240, entity.getLayoutWidth());
        assertEquals(80, entity.getLayoutHeight());
        assertEquals(30, entity.getRotation());

        entity.setId(101L);
        CabinetVo vo = cabinetConverter.toVo(entity, "A区", 2);

        assertEquals(240, vo.getLayoutWidth());
        assertEquals(80, vo.getLayoutHeight());
        assertEquals(30, vo.getRotation());
    }

    @Test
    @DisplayName("库房和区域对象应暴露布局字段")
    void shouldExposeWarehouseAndAreaLayoutFields() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setLayoutWidth(1200);
        warehouseDto.setLayoutHeight(800);
        warehouseDto.setLayoutScale(new java.math.BigDecimal("1.25"));
        warehouseDto.setLayoutBackgroundVersion("v1");
        assertEquals(1200, warehouseDto.getLayoutWidth());
        assertEquals(800, warehouseDto.getLayoutHeight());
        assertEquals(new java.math.BigDecimal("1.25"), warehouseDto.getLayoutScale());
        assertEquals("v1", warehouseDto.getLayoutBackgroundVersion());

        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setLayoutWidth(1200);
        warehouse.setLayoutHeight(800);
        warehouse.setLayoutScale(new java.math.BigDecimal("1.25"));
        warehouse.setLayoutBackgroundVersion("v1");
        assertEquals(1200, warehouse.getLayoutWidth());
        assertEquals(800, warehouse.getLayoutHeight());
        assertEquals(new java.math.BigDecimal("1.25"), warehouse.getLayoutScale());
        assertEquals("v1", warehouse.getLayoutBackgroundVersion());

        WarehouseVo warehouseVo = new WarehouseVo();
        warehouseVo.setLayoutWidth(1200);
        warehouseVo.setLayoutHeight(800);
        warehouseVo.setLayoutScale(new java.math.BigDecimal("1.25"));
        warehouseVo.setLayoutBackgroundVersion("v1");
        assertEquals(1200, warehouseVo.getLayoutWidth());
        assertEquals(800, warehouseVo.getLayoutHeight());
        assertEquals(new java.math.BigDecimal("1.25"), warehouseVo.getLayoutScale());
        assertEquals("v1", warehouseVo.getLayoutBackgroundVersion());

        AreaDto areaDto = new AreaDto();
        areaDto.setShapeType("polygon");
        areaDto.setPolygonPoints("[[0,0],[10,10],[20,0]]");
        areaDto.setLabelX(32);
        areaDto.setLabelY(64);
        assertEquals("polygon", areaDto.getShapeType());
        assertEquals("[[0,0],[10,10],[20,0]]", areaDto.getPolygonPoints());
        assertEquals(32, areaDto.getLabelX());
        assertEquals(64, areaDto.getLabelY());

        WmsArea area = new WmsArea();
        area.setShapeType("polygon");
        area.setPolygonPoints("[[0,0],[10,10],[20,0]]");
        area.setLabelX(32);
        area.setLabelY(64);
        assertEquals("polygon", area.getShapeType());
        assertEquals("[[0,0],[10,10],[20,0]]", area.getPolygonPoints());
        assertEquals(32, area.getLabelX());
        assertEquals(64, area.getLabelY());

        AreaVo areaVo = new AreaVo();
        areaVo.setShapeType("polygon");
        areaVo.setPolygonPoints("[[0,0],[10,10],[20,0]]");
        areaVo.setLabelX(32);
        areaVo.setLabelY(64);
        assertEquals("polygon", areaVo.getShapeType());
        assertEquals("[[0,0],[10,10],[20,0]]", areaVo.getPolygonPoints());
        assertEquals(32, areaVo.getLabelX());
        assertEquals(64, areaVo.getLabelY());
    }

    @Test
    @DisplayName("新增存放柜时未传排序号应使用默认排序号")
    void shouldDefaultSortOrderWhenCreateCabinetWithoutSortOrder() {
        CabinetDto dto = new CabinetDto();
        dto.setAreaId(10L);
        dto.setWarehouseId(20L);
        dto.setCabinetName("测试柜");

        WmsArea area = new WmsArea();
        area.setId(10L);
        area.setWarehouseId(20L);
        area.setDelFlag(DelFlagConstants.NORMAL);
        when(wmsAreaMapper.selectById(10L)).thenReturn(area);
        when(sequenceGenerator.next(any())).thenReturn("CG202605210001");

        CabinetVo result = cabinetService.create(dto);

        assertEquals(BizConstants.DEFAULT_SORT_ORDER, result.getSortOrder());
    }

    @Test
    @DisplayName("批量保存布局时应返回保存后位置并更新排序号")
    void shouldBatchSaveCabinetLayoutAndReturnSavedPositions() {
        CabinetLayoutBatchSaveDto dto = new CabinetLayoutBatchSaveDto();
        dto.setAreaId(10L);

        CabinetLayoutItemDto first = new CabinetLayoutItemDto();
        first.setId(101L);
        first.setPositionX(100);
        first.setPositionY(120);
        first.setSortOrder(1);
        CabinetLayoutItemDto second = new CabinetLayoutItemDto();
        second.setId(102L);
        second.setPositionX(200);
        second.setPositionY(220);
        second.setSortOrder(2);
        dto.setCabinets(List.of(first, second));

        when(wmsCabinetMapper.selectBatchIds(List.of(101L, 102L))).thenReturn(List.of(
                buildCabinet(101L, 10L, "A柜", null, null),
                buildCabinet(102L, 10L, "B柜", null, null)
        ));

        CabinetLayoutSaveVo result = cabinetService.saveLayout(dto);

        assertEquals(10L, result.getAreaId());
        assertEquals(2, result.getCabinets().size());
        assertEquals(100, result.getCabinets().get(0).getPositionX());
        assertEquals(120, result.getCabinets().get(0).getPositionY());
        assertEquals(1, result.getCabinets().get(0).getSortOrder());
        assertEquals(200, result.getCabinets().get(1).getPositionX());
        assertEquals(220, result.getCabinets().get(1).getPositionY());
        assertEquals(2, result.getCabinets().get(1).getSortOrder());

        ArgumentCaptor<WmsCabinet> captor = ArgumentCaptor.forClass(WmsCabinet.class);
        verify(wmsCabinetMapper, times(2)).updateById(captor.capture());
        List<WmsCabinet> updateEntities = captor.getAllValues();
        assertEquals(101L, updateEntities.get(0).getId());
        assertEquals(100, updateEntities.get(0).getPositionX());
        assertEquals(1, updateEntities.get(0).getSortOrder());
        assertEquals(102L, updateEntities.get(1).getId());
        assertEquals(220, updateEntities.get(1).getPositionY());
        assertEquals(2, updateEntities.get(1).getSortOrder());
    }

    @Test
    @DisplayName("批量保存布局时柜体不属于目标区域应抛出业务异常")
    void shouldThrowWhenSaveLayoutContainsCabinetOutsideArea() {
        CabinetLayoutBatchSaveDto dto = new CabinetLayoutBatchSaveDto();
        dto.setAreaId(10L);

        CabinetLayoutItemDto item = new CabinetLayoutItemDto();
        item.setId(101L);
        item.setPositionX(100);
        item.setPositionY(120);
        item.setSortOrder(1);
        dto.setCabinets(List.of(item));

        when(wmsCabinetMapper.selectBatchIds(List.of(101L))).thenReturn(List.of(
                buildCabinet(101L, 11L, "A柜", null, null)
        ));

        BizException exception = assertThrows(BizException.class, () -> cabinetService.saveLayout(dto));

        assertEquals("存放柜不属于当前区域", exception.getMessage());
    }

    private WmsCabinet buildCabinet(Long id, Long areaId, String cabinetName, Integer positionX, Integer positionY) {
        WmsCabinet cabinet = new WmsCabinet();
        cabinet.setId(id);
        cabinet.setAreaId(areaId);
        cabinet.setCabinetName(cabinetName);
        cabinet.setCabinetCode("CG-" + id);
        cabinet.setPositionX(positionX);
        cabinet.setPositionY(positionY);
        cabinet.setDelFlag(DelFlagConstants.NORMAL);
        return cabinet;
    }
}
