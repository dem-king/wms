package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.item.converter.StockConverter;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.domain.vo.StockVo;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * StockServiceImpl 单元测试
 * 验证库存查询使用批量物品加载，避免逐条查库和冗余逻辑删除条件。
 */
@DisplayName("StockServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private WmsStockMapper wmsStockMapper;

    @Mock
    private WmsItemMapper wmsItemMapper;

    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockServiceImpl(wmsStockMapper, wmsItemMapper, new StockConverter());
    }

    @Test
    @DisplayName("查询预警库存时应批量加载物品并避免逐条 selectById")
    void shouldBatchLoadItemsWhenQueryAlertList() {
        WmsStock stock = createStock(1L, 100L, 3);
        WmsItem item = createItem(100L, "ITEM-100", "轴承", 5);
        when(wmsStockMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(stock));
        when(wmsItemMapper.selectBatchIds(any())).thenReturn(List.of(item));

        List<StockVo> result = stockService.getAlertList();

        assertEquals(1, result.size());
        assertEquals("轴承", result.get(0).getItemName());
        assertTrue(result.get(0).getAlert());
        verify(wmsItemMapper).selectBatchIds(any());
        verify(wmsItemMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("按物品查询库存时应复用批量映射而不是在转换中查库")
    void shouldReuseBatchItemMapWhenQueryByItemId() {
        WmsStock stock = createStock(2L, 200L, 8);
        WmsItem item = createItem(200L, "ITEM-200", "电机", 2);
        when(wmsStockMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(stock));
        when(wmsItemMapper.selectBatchIds(any())).thenReturn(List.of(item));

        List<StockVo> result = stockService.getByItemId(200L);

        assertEquals(1, result.size());
        assertEquals("电机", result.get(0).getItemName());
        verify(wmsItemMapper).selectBatchIds(any());
        verify(wmsItemMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("查询预警库存时不应手动拼接 del_flag 条件")
    void shouldNotAppendManualDelFlagConditionWhenQueryAlertList() {
        when(wmsStockMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        stockService.getAlertList();

        ArgumentCaptor<LambdaQueryWrapper<WmsStock>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(wmsStockMapper).selectList(captor.capture());
        String sqlSegment = captor.getValue().getSqlSegment();
        assertFalse(sqlSegment.contains("del_flag"));
    }

    @Test
    @DisplayName("分页查询预警库存时应在结果与总数中只保留预警记录")
    void shouldKeepAlertOnlyResultAndTotalConsistent() {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setSize(10);
        WmsStock alertStock = createStock(3L, 300L, 2);
        WmsStock normalStock = createStock(4L, 400L, 8);
        WmsItem alertItem = createItem(300L, "ITEM-300", "链条", 5);
        WmsItem normalItem = createItem(400L, "ITEM-400", "螺栓", 3);
        when(wmsStockMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenAnswer(this::buildPageByAlertFilter);
        when(wmsItemMapper.selectBatchIds(any())).thenReturn(List.of(alertItem, normalItem));

        PageResult<StockVo> result = stockService.page(pageParam, null, null, true);

        assertEquals(1, result.getRecords().size());
        assertEquals("链条", result.getRecords().get(0).getItemName());
        assertEquals(1L, result.getTotal());
    }

    private WmsStock createStock(Long id, Long itemId, Integer quantity) {
        WmsStock stock = new WmsStock();
        stock.setId(id);
        stock.setItemId(itemId);
        stock.setQuantity(quantity);
        return stock;
    }

    @SuppressWarnings("unchecked")
    private Page<WmsStock> buildPageByAlertFilter(InvocationOnMock invocation) {
        LambdaQueryWrapper<WmsStock> wrapper = invocation.getArgument(1);
        boolean alertOnly = !wrapper.getExpression().getNormal().isEmpty();
        if (alertOnly) {
            Page<WmsStock> page = new Page<>(1, 10, 1);
            page.setRecords(List.of(createStock(3L, 300L, 2)));
            return page;
        }
        Page<WmsStock> page = new Page<>(1, 10, 2);
        page.setRecords(List.of(createStock(3L, 300L, 2), createStock(4L, 400L, 8)));
        return page;
    }

    private WmsItem createItem(Long id, String itemCode, String itemName, Integer lowerLimit) {
        WmsItem item = new WmsItem();
        item.setId(id);
        item.setItemCode(itemCode);
        item.setItemName(itemName);
        item.setStockLowerLimit(lowerLimit);
        return item;
    }
}
