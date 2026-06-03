package com.wms.business.service.impl;

import com.wms.business.converter.InboundOrderConverter;
import com.wms.business.converter.OutboundOrderConverter;
import com.wms.business.converter.TransferOrderConverter;
import com.wms.business.domain.constant.OrderConstants;
import com.wms.business.domain.dto.InboundOrderDto;
import com.wms.business.domain.dto.OutboundOrderDto;
import com.wms.business.domain.dto.TransferOrderDto;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.business.service.support.BinWarehouseValidator;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.service.ItemService;
import com.wms.system.mapper.SysSupplierMapper;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Order warehouse status validation tests.
 */
@DisplayName("Order warehouse status validation")
@ExtendWith(MockitoExtension.class)
class OrderWarehouseStatusValidationTest {

    @Mock
    private WmsInboundOrderMapper wmsInboundOrderMapper;
    @Mock
    private WmsInboundDetailMapper wmsInboundDetailMapper;
    @Mock
    private WmsOutboundOrderMapper wmsOutboundOrderMapper;
    @Mock
    private WmsOutboundDetailMapper wmsOutboundDetailMapper;
    @Mock
    private WmsTransferOrderMapper wmsTransferOrderMapper;
    @Mock
    private WmsTransferDetailMapper wmsTransferDetailMapper;
    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;
    @Mock
    private SysSupplierMapper sysSupplierMapper;
    @Mock
    private WmsItemMapper wmsItemMapper;
    @Mock
    private WmsBinMapper wmsBinMapper;
    @Mock
    private ItemService itemService;
    @Mock
    private BinWarehouseValidator binWarehouseValidator;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private SequenceGenerator sequenceGenerator;
    @Mock
    private InboundOrderConverter inboundOrderConverter;
    @Mock
    private OutboundOrderConverter outboundOrderConverter;
    @Mock
    private TransferOrderConverter transferOrderConverter;

    @Test
    @DisplayName("inbound create should reject disabled warehouse")
    void shouldRejectDisabledWarehouseWhenCreatingInboundOrder() {
        InboundServiceImpl service = newInboundService();
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.createOrder(inboundDto(10L)));

        assertEquals("库房已禁用", exception.getMessage());
        verify(wmsInboundOrderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("inbound update should reject disabled warehouse")
    void shouldRejectDisabledWarehouseWhenUpdatingInboundOrder() {
        InboundServiceImpl service = newInboundService();
        WmsInboundOrder order = new WmsInboundOrder();
        order.setId(9001L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        when(wmsInboundOrderMapper.selectById(9001L)).thenReturn(order);
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.updateOrder(9001L, inboundDto(10L)));

        assertEquals("库房已禁用", exception.getMessage());
        verify(wmsInboundOrderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("outbound create should reject disabled warehouse")
    void shouldRejectDisabledWarehouseWhenCreatingOutboundOrder() {
        OutboundServiceImpl service = newOutboundService();
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.createOrder(outboundDto(10L)));

        assertEquals("库房已禁用", exception.getMessage());
        verify(wmsOutboundOrderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("outbound update should reject disabled warehouse")
    void shouldRejectDisabledWarehouseWhenUpdatingOutboundOrder() {
        OutboundServiceImpl service = newOutboundService();
        WmsOutboundOrder order = new WmsOutboundOrder();
        order.setId(9002L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        when(wmsOutboundOrderMapper.selectById(9002L)).thenReturn(order);
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.updateOrder(9002L, outboundDto(10L)));

        assertEquals("库房已禁用", exception.getMessage());
        verify(wmsOutboundOrderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("transfer create should reject disabled from warehouse")
    void shouldRejectDisabledFromWarehouseWhenCreatingTransferOrder() {
        TransferServiceImpl service = newTransferService();
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.createOrder(transferDto(10L, 20L)));

        assertEquals("调出库房已禁用", exception.getMessage());
        verify(wmsTransferOrderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("transfer create should reject disabled to warehouse")
    void shouldRejectDisabledToWarehouseWhenCreatingTransferOrder() {
        TransferServiceImpl service = newTransferService();
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_ENABLED));
        when(wmsWarehouseMapper.selectById(20L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.createOrder(transferDto(10L, 20L)));

        assertEquals("调入库房已禁用", exception.getMessage());
        verify(wmsTransferOrderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("transfer update should reject disabled from warehouse")
    void shouldRejectDisabledFromWarehouseWhenUpdatingTransferOrder() {
        TransferServiceImpl service = newTransferService();
        WmsTransferOrder order = transferOrder();
        when(wmsTransferOrderMapper.selectById(9003L)).thenReturn(order);
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.updateOrder(9003L, transferDto(10L, 20L)));

        assertEquals("调出库房已禁用", exception.getMessage());
        verify(wmsTransferOrderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("transfer update should reject disabled to warehouse")
    void shouldRejectDisabledToWarehouseWhenUpdatingTransferOrder() {
        TransferServiceImpl service = newTransferService();
        WmsTransferOrder order = transferOrder();
        when(wmsTransferOrderMapper.selectById(9003L)).thenReturn(order);
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse(BizConstants.STATUS_ENABLED));
        when(wmsWarehouseMapper.selectById(20L)).thenReturn(warehouse(BizConstants.STATUS_DISABLED));

        BizException exception = assertThrows(BizException.class, () -> service.updateOrder(9003L, transferDto(10L, 20L)));

        assertEquals("调入库房已禁用", exception.getMessage());
        verify(wmsTransferOrderMapper, never()).updateById(any());
    }

    private InboundServiceImpl newInboundService() {
        return new InboundServiceImpl(wmsInboundOrderMapper, wmsInboundDetailMapper,
                wmsWarehouseMapper, sysSupplierMapper, wmsItemMapper, itemService, binWarehouseValidator,
                eventPublisher, sequenceGenerator, inboundOrderConverter);
    }

    private OutboundServiceImpl newOutboundService() {
        return new OutboundServiceImpl(wmsOutboundOrderMapper, wmsOutboundDetailMapper,
                wmsWarehouseMapper, wmsItemMapper, wmsBinMapper, binWarehouseValidator, eventPublisher,
                sequenceGenerator, outboundOrderConverter);
    }

    private TransferServiceImpl newTransferService() {
        return new TransferServiceImpl(wmsTransferOrderMapper, wmsTransferDetailMapper,
                wmsWarehouseMapper, wmsItemMapper, itemService, binWarehouseValidator, eventPublisher,
                sequenceGenerator, transferOrderConverter);
    }

    private WmsWarehouse warehouse(Integer status) {
        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setDelFlag(DelFlagConstants.NORMAL);
        warehouse.setStatus(status);
        return warehouse;
    }

    private InboundOrderDto inboundDto(Long warehouseId) {
        InboundOrderDto dto = new InboundOrderDto();
        dto.setWarehouseId(warehouseId);
        dto.setOrderType(OrderConstants.ORDER_TYPE_MIN);
        InboundOrderDto.InboundDetailDto detail = new InboundOrderDto.InboundDetailDto();
        detail.setItemId(100L);
        detail.setBinId(200L);
        detail.setQuantity(OrderConstants.ORDER_DETAIL_QUANTITY_MIN);
        dto.setDetails(List.of(detail));
        return dto;
    }

    private OutboundOrderDto outboundDto(Long warehouseId) {
        OutboundOrderDto dto = new OutboundOrderDto();
        dto.setWarehouseId(warehouseId);
        dto.setOrderType(OrderConstants.ORDER_TYPE_MIN);
        OutboundOrderDto.OutboundDetailDto detail = new OutboundOrderDto.OutboundDetailDto();
        detail.setItemId(100L);
        detail.setBinId(200L);
        detail.setQuantity(OrderConstants.ORDER_DETAIL_QUANTITY_MIN);
        dto.setDetails(List.of(detail));
        return dto;
    }

    private TransferOrderDto transferDto(Long fromWarehouseId, Long toWarehouseId) {
        TransferOrderDto dto = new TransferOrderDto();
        dto.setFromWarehouseId(fromWarehouseId);
        dto.setToWarehouseId(toWarehouseId);
        TransferOrderDto.TransferDetailDto detail = new TransferOrderDto.TransferDetailDto();
        detail.setItemId(100L);
        detail.setFromBinId(200L);
        detail.setToBinId(300L);
        detail.setQuantity(OrderConstants.ORDER_DETAIL_QUANTITY_MIN);
        dto.setDetails(List.of(detail));
        return dto;
    }

    private WmsTransferOrder transferOrder() {
        WmsTransferOrder order = new WmsTransferOrder();
        order.setId(9003L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        return order;
    }
}
