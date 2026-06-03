package com.wms.business.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.mapper.WmsStockMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Stock sync event handler tests.
 */
@DisplayName("StockSyncEventHandler tests")
@ExtendWith(MockitoExtension.class)
class StockSyncEventHandlerTest {

    @Mock
    private WmsStockMapper wmsStockMapper;

    @InjectMocks
    private StockSyncEventHandler handler;

    @Test
    @DisplayName("IN event should create stock when record does not exist")
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
    @DisplayName("stock sync should reject missing bin")
    void shouldRejectStockSyncWhenBinIdMissing() {
        StockSyncEvent event = new StockSyncEvent(1L, 10L, null, 50, BizConstants.STOCK_SYNC_IN);

        BizException ex = assertThrows(BizException.class,
                () -> handler.handleStockSync(event));

        assertTrue(ex.getMessage().contains("库位不能为空"));
        verify(wmsStockMapper, never()).selectOne(any(LambdaQueryWrapper.class));
        verify(wmsStockMapper, never()).insert(any(WmsStock.class));
        verify(wmsStockMapper, never()).updateById(any(WmsStock.class));
    }

    @Test
    @DisplayName("IN event should increase existing stock")
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
        assertEquals(130, stockCaptor.getValue().getQuantity());
    }

    @Test
    @DisplayName("OUT event should fail when stock is insufficient")
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
    @DisplayName("OUT event should fail when stock record does not exist")
    void shouldThrowBizExceptionWhenOutboundStockRecordMissing() {
        StockSyncEvent event = new StockSyncEvent(4L, 10L, 100L, -1, BizConstants.STOCK_SYNC_OUT);
        when(wmsStockMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BizException ex = assertThrows(BizException.class,
                () -> handler.handleStockSync(event));

        assertTrue(ex.getMessage().contains("itemId=4"));
        verify(wmsStockMapper, never()).insert(any(WmsStock.class));
        verify(wmsStockMapper, never()).updateById(any(WmsStock.class));
    }

    @Test
    @DisplayName("OUT event should deduct stock when exactly sufficient")
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
