package com.wms.business.listener;

import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnDetailMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapDetailMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.enums.BizTypeEnum;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.event.ApprovalResultEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 审批结果事件监听器测试。
 * 验证审批通过后的库存同步事件与业务明细保持同一库位口径。
 */
@DisplayName("ApprovalResultEventListener 测试")
@ExtendWith(MockitoExtension.class)
class ApprovalResultEventListenerTest {

    @Mock
    private WmsInboundOrderMapper wmsInboundOrderMapper;
    @Mock
    private WmsInboundDetailMapper wmsInboundDetailMapper;
    @Mock
    private WmsOutboundOrderMapper wmsOutboundOrderMapper;
    @Mock
    private WmsOutboundDetailMapper wmsOutboundDetailMapper;
    @Mock
    private WmsScrapOrderMapper wmsScrapOrderMapper;
    @Mock
    private WmsScrapDetailMapper wmsScrapDetailMapper;
    @Mock
    private WmsTransferOrderMapper wmsTransferOrderMapper;
    @Mock
    private WmsTransferDetailMapper wmsTransferDetailMapper;
    @Mock
    private WmsReturnOrderMapper wmsReturnOrderMapper;
    @Mock
    private WmsReturnDetailMapper wmsReturnDetailMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("入库单审批通过后应按明细库位发布入库库存事件")
    void shouldPublishInboundStockSyncWithDetailBinIdWhenApproved() {
        ApprovalResultEventListener listener = buildListener();
        WmsInboundOrder order = new WmsInboundOrder();
        order.setId(9001L);
        order.setWarehouseId(10L);
        WmsInboundDetail detail = new WmsInboundDetail();
        detail.setItemId(1001L);
        detail.setBinId(2001L);
        detail.setQuantity(5);
        when(wmsInboundOrderMapper.selectById(9001L)).thenReturn(order);
        when(wmsInboundDetailMapper.selectList(any())).thenReturn(List.of(detail));

        listener.handleApprovalResult(new ApprovalResultEvent(9001L, BizTypeEnum.INBOUND.getCode(), true));

        assertEquals(OrderStatusEnum.COMPLETED.getCode(), order.getStatus());
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        StockSyncEvent stockSyncEvent = assertInstanceOf(StockSyncEvent.class, eventCaptor.getValue());
        assertEquals(1001L, stockSyncEvent.getItemId());
        assertEquals(10L, stockSyncEvent.getWarehouseId());
        assertEquals(2001L, stockSyncEvent.getBinId());
        assertEquals(5, stockSyncEvent.getQuantity());
        assertEquals(BizConstants.STOCK_SYNC_IN, stockSyncEvent.getType());
    }

    private ApprovalResultEventListener buildListener() {
        return new ApprovalResultEventListener(
                wmsInboundOrderMapper,
                wmsInboundDetailMapper,
                wmsOutboundOrderMapper,
                wmsOutboundDetailMapper,
                wmsScrapOrderMapper,
                wmsScrapDetailMapper,
                wmsTransferOrderMapper,
                wmsTransferDetailMapper,
                wmsReturnOrderMapper,
                wmsReturnDetailMapper,
                eventPublisher);
    }
}
