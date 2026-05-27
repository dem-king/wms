package com.wms.business.service.impl;

import com.wms.business.converter.InboundOrderConverter;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.enums.BizTypeEnum;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.event.ApprovalRequestEvent;
import com.wms.common.util.SequenceGenerator;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.system.mapper.SysSupplierMapper;
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
 * 入库单审批流接入测试
 * 验证提交入库单后应进入真实审批流，而不是直接同步库存
 */
@DisplayName("InboundServiceImpl 审批流测试")
@ExtendWith(MockitoExtension.class)
class InboundServiceImplApprovalFlowTest {

    @Mock
    private WmsInboundOrderMapper wmsInboundOrderMapper;

    @Mock
    private WmsInboundDetailMapper wmsInboundDetailMapper;

    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;

    @Mock
    private SysSupplierMapper sysSupplierMapper;

    @Mock
    private WmsItemMapper wmsItemMapper;

    @Mock
    private WmsBinMapper wmsBinMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private InboundOrderConverter inboundOrderConverter;

    @InjectMocks
    private InboundServiceImpl inboundService;

    @Test
    @DisplayName("提交入库单后应只发起审批请求而不直接同步库存")
    void shouldPublishApprovalRequestInsteadOfStockSyncWhenSubmittingInboundOrder() {
        WmsInboundOrder order = new WmsInboundOrder();
        order.setId(9001L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        when(wmsInboundOrderMapper.selectById(9001L)).thenReturn(order);

        inboundService.submitOrder(9001L);

        ArgumentCaptor<WmsInboundOrder> orderCaptor = ArgumentCaptor.forClass(WmsInboundOrder.class);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(wmsInboundOrderMapper, times(1)).updateById(orderCaptor.capture());
        assertEquals(OrderStatusEnum.PENDING.getCode(), orderCaptor.getValue().getStatus());
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        ApprovalRequestEvent approvalRequestEvent = assertInstanceOf(ApprovalRequestEvent.class, eventCaptor.getValue());
        assertEquals(9001L, approvalRequestEvent.getBizId());
        assertEquals(BizTypeEnum.INBOUND.getCode(), approvalRequestEvent.getBizType());
        verify(eventPublisher, never()).publishEvent(any(StockSyncEvent.class));
    }
}
