package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.warehouse.converter.BinConverter;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.BinVo;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
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
 * 库位服务单元测试
 * 验证库位分页查询结果映射与总数返回
 */
@DisplayName("BinServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class BinServiceImplTest {

    @Mock
    private WmsBinMapper wmsBinMapper;

    @Mock
    private WmsCabinetMapper wmsCabinetMapper;

    private BinServiceImpl binService;

    @BeforeEach
    void setUp() {
        BinConverter binConverter = new BinConverter();
        binService = new BinServiceImpl(wmsBinMapper, wmsCabinetMapper, binConverter);
    }

    @Test
    @DisplayName("按存放柜分页查询时应返回分页结果和库位列表")
    void shouldReturnBinPageByCabinetId() {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setSize(20);

        WmsBin bin = new WmsBin();
        bin.setId(101L);
        bin.setCabinetId(10L);
        bin.setBinCode("CG202605280001-01-01");
        bin.setRowNum(1);
        bin.setColNum(1);
        bin.setBinStatus(1);

        Page<WmsBin> page = new Page<>(1, 20, 1);
        page.setRecords(List.of(bin));
        when(wmsBinMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        WmsCabinet cabinet = new WmsCabinet();
        cabinet.setId(10L);
        cabinet.setCabinetName("A柜");
        when(wmsCabinetMapper.selectBatchIds(anyCollection())).thenReturn(List.of(cabinet));

        PageResult<BinVo> result = binService.page(pageParam, 10L);

        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getRecords().size());
        assertEquals("CG202605280001-01-01", result.getRecords().get(0).getBinCode());
        assertEquals("A柜", result.getRecords().get(0).getCabinetName());
    }
}
