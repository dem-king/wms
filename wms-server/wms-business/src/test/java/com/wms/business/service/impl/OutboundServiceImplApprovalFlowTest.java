package com.wms.business.service.impl;

import com.wms.business.converter.OutboundOrderConverter;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.enums.BizTypeEnum;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.event.ApprovalRequestEvent;
import com.wms.common.util.SequenceGenerator;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 出库单审批流接入测试
 * 验证提交出库单后应进入真实审批流，而不是直接扣减库存
 */
@DisplayName("OutboundServiceImpl 审批流测试")
@ExtendWith(MockitoExtension.class)
class OutboundServiceImplApprovalFlowTest {

    @Mock
    private WmsOutboundOrderMapper wmsOutboundOrderMapper;

    @Mock
    private WmsOutboundDetailMapper wmsOutboundDetailMapper;

    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;

    @Mock
    private WmsItemMapper wmsItemMapper;

    @Mock
    private WmsBinMapper wmsBinMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private OutboundOrderConverter outboundOrderConverter;

    @InjectMocks
    private OutboundServiceImpl outboundService;

    @Test
    @DisplayName("提交出库单后应只发起审批请求而不直接同步库存")
    void shouldPublishApprovalRequestInsteadOfStockSyncWhenSubmittingOutboundOrder() {
        WmsOutboundOrder order = new WmsOutboundOrder();
        order.setId(9002L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        when(wmsOutboundOrderMapper.selectById(9002L)).thenReturn(order);

        outboundService.submitOrder(9002L);

        ArgumentCaptor<WmsOutboundOrder> orderCaptor = ArgumentCaptor.forClass(WmsOutboundOrder.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(wmsOutboundOrderMapper, times(1)).updateById(orderCaptor.capture());
        assertEquals(OrderStatusEnum.PENDING.getCode(), orderCaptor.getValue().getStatus());
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        ApprovalRequestEvent approvalRequestEvent = assertInstanceOf(ApprovalRequestEvent.class, eventCaptor.getValue());
        assertEquals(9002L, approvalRequestEvent.getBizId());
        assertEquals(BizTypeEnum.OUTBOUND.getCode(), approvalRequestEvent.getBizType());
        verify(eventPublisher, never()).publishEvent(any(StockSyncEvent.class));
    }
}
