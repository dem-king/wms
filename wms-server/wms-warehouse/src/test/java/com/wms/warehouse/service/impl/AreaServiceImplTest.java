package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.warehouse.converter.AreaConverter;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.AreaVo;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

/**
 * 区域服务单元测试
 * 验证区域分页查询结果映射与总数返回
 */
@DisplayName("AreaServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class AreaServiceImplTest {

    @Mock
    private WmsAreaMapper wmsAreaMapper;

    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;

    @Mock
    private com.wms.common.util.SequenceGenerator sequenceGenerator;

    private AreaServiceImpl areaService;

    @BeforeEach
    void setUp() {
        AreaConverter areaConverter = new AreaConverter(wmsWarehouseMapper);
        areaService = new AreaServiceImpl(wmsAreaMapper, wmsWarehouseMapper, sequenceGenerator, areaConverter);
    }

    @Test
    @DisplayName("按库房分页查询时应返回分页结果和区域列表")
    void shouldReturnAreaPageByWarehouseId() {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setSize(20);

        WmsArea area = new WmsArea();
        area.setId(101L);
        area.setWarehouseId(10L);
        area.setAreaCode("QY202605280001");
        area.setAreaName("A区");
        area.setSortOrder(1);
        area.setStatus(1);

        Page<WmsArea> page = new Page<>(1, 20, 1);
        page.setRecords(List.of(area));
        when(wmsAreaMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setId(10L);
        warehouse.setWarehouseName("一号库房");
        when(wmsWarehouseMapper.selectBatchIds(anyCollection())).thenReturn(List.of(warehouse));

        PageResult<AreaVo> result = areaService.page(pageParam, 10L);

        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getRecords().size());
        assertEquals("A区", result.getRecords().get(0).getAreaName());
        assertEquals("一号库房", result.getRecords().get(0).getWarehouseName());
    }
}
