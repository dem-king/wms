package com.wms.business.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.mapper.WmsStockMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StockSyncEventHandler 单元测试
 * 验证库存同步事件处理的核心业务规则
 */
@DisplayName("StockSyncEventHandler 测试")
@ExtendWith(MockitoExtension.class)
class StockSyncEventHandlerTest {

    @Mock
    private WmsStockMapper wmsStockMapper;

    @InjectMocks
    private StockSyncEventHandler handler;

    @Test
    @DisplayName("入库事件 - 库存记录不存在时应新增库存")
    void shouldCreateNewStockWhenNotExists() {
        StockSyncEvent event = new StockSyncEvent(1L, 10L, 100L, 50, BizConstants.STOCK_SYNC_IN);
        when(wmsStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        handler.handleStockSync(event);

        ArgumentCaptor<WmsStock> stockCaptor = ArgumentCaptor.forClass(WmsStock.class);
        verify(wmsStockMapper).insert(stockCaptor.capture());
        WmsStock savedStock = stockCaptor.getValue();
        assertEquals(1L, savedStock.getItemId());
        assertEquals(10L, savedStock.getWarehouseId());
        assertEquals(100L, savedStock.getBinId());
        assertEquals(50, savedStock.getQuantity());
        assertEquals(0, savedStock.getLockedQuantity());
    }

    @Test
    @DisplayName("入库事件 - 已有库存记录应累加数量")
    void shouldIncreaseStockWhenExists() {
        StockSyncEvent event = new StockSyncEvent(1L, 10L, 100L, 30, BizConstants.STOCK_SYNC_IN);
        WmsStock existingStock = new WmsStock();
        existingStock.setId(1L);
        existingStock.setItemId(1L);
        existingStock.setWarehouseId(10L);
        existingStock.setBinId(100L);
        existingStock.setQuantity(100);
        when(wmsStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingStock);

        handler.handleStockSync(event);

        ArgumentCaptor<WmsStock> stockCaptor = ArgumentCaptor.forClass(WmsStock.class);
        verify(wmsStockMapper).updateById(stockCaptor.capture());
        WmsStock updatedStock = stockCaptor.getValue();
        assertEquals(130, updatedStock.getQuantity());
    }

    @Test
    @DisplayName("出库事件 - 库存不足时应抛出BizException")
    void shouldThrowBizExceptionWhenStockInsufficient() {
        StockSyncEvent event = new StockSyncEvent(2L, 10L, 100L, -50, BizConstants.STOCK_SYNC_OUT);
        WmsStock existingStock = new WmsStock();
        existingStock.setId(2L);
        existingStock.setQuantity(20);
        when(wmsStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingStock);

        BizException ex = assertThrows(BizException.class,
                () -> handler.handleStockSync(event));

        assertTrue(ex.getMessage().contains("库存不足"));
        assertTrue(ex.getMessage().contains("itemId=2"));
    }

    @Test
    @DisplayName("出库事件 - 库存刚好充足时应成功扣减")
    void shouldDeductStockWhenSufficient() {
        StockSyncEvent event = new StockSyncEvent(3L, 10L, 100L, -30, BizConstants.STOCK_SYNC_OUT);
        WmsStock existingStock = new WmsStock();
        existingStock.setId(3L);
        existingStock.setQuantity(30);
        when(wmsStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingStock);

        handler.handleStockSync(event);

        ArgumentCaptor<WmsStock> stockCaptor = ArgumentCaptor.forClass(WmsStock.class);
        verify(wmsStockMapper).updateById(stockCaptor.capture());
        assertEquals(0, stockCaptor.getValue().getQuantity());
    }
}