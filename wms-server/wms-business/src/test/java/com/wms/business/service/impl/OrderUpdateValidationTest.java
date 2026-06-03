package com.wms.business.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.converter.InboundOrderConverter;
import com.wms.business.converter.OutboundOrderConverter;
import com.wms.business.domain.constant.OrderConstants;
import com.wms.business.domain.dto.InboundOrderDto;
import com.wms.business.domain.dto.OutboundOrderDto;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Order update validation tests.
 */
@DisplayName("Order update validation")
@ExtendWith(MockitoExtension.class)
class OrderUpdateValidationTest {

    @Mock
    private WmsInboundOrderMapper wmsInboundOrderMapper;
    @Mock
    private WmsInboundDetailMapper wmsInboundDetailMapper;
    @Mock
    private WmsOutboundOrderMapper wmsOutboundOrderMapper;
    @Mock
    private WmsOutboundDetailMapper wmsOutboundDetailMapper;
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

    @Test
    @DisplayName("inbound update should reject missing detail item")
    void shouldRejectMissingInboundDetailItemWhenUpdating() {
        InboundServiceImpl service = new InboundServiceImpl(wmsInboundOrderMapper, wmsInboundDetailMapper,
                wmsWarehouseMapper, sysSupplierMapper, wmsItemMapper, itemService, binWarehouseValidator,
                eventPublisher, sequenceGenerator, inboundOrderConverter);
        WmsInboundOrder order = new WmsInboundOrder();
        order.setId(9001L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setDelFlag(DelFlagConstants.NORMAL);
        warehouse.setStatus(BizConstants.STATUS_ENABLED);
        InboundOrderDto dto = new InboundOrderDto();
        dto.setWarehouseId(10L);
        dto.setOrderType(OrderConstants.ORDER_TYPE_MIN);
        InboundOrderDto.InboundDetailDto detail = new InboundOrderDto.InboundDetailDto();
        detail.setItemId(404L);
        detail.setBinId(100L);
        detail.setQuantity(OrderConstants.ORDER_DETAIL_QUANTITY_MIN);
        dto.setDetails(List.of(detail));
        when(wmsInboundOrderMapper.selectById(9001L)).thenReturn(order);
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse);
        when(wmsInboundDetailMapper.selectList(any())).thenReturn(List.of());

        try (MockedStatic<Db> db = mockStatic(Db.class)) {
            db.when(() -> Db.saveBatch(any(List.class))).thenReturn(true);
            assertThrows(BizException.class, () -> service.updateOrder(9001L, dto));
        }

        verify(wmsItemMapper).selectById(404L);
    }

    @Test
    @DisplayName("outbound update should reject missing detail item")
    void shouldRejectMissingOutboundDetailItemWhenUpdating() {
        OutboundServiceImpl service = new OutboundServiceImpl(wmsOutboundOrderMapper, wmsOutboundDetailMapper,
                wmsWarehouseMapper, wmsItemMapper, wmsBinMapper, binWarehouseValidator, eventPublisher,
                sequenceGenerator, outboundOrderConverter);
        WmsOutboundOrder order = new WmsOutboundOrder();
        order.setId(9002L);
        order.setDelFlag(DelFlagConstants.NORMAL);
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setDelFlag(DelFlagConstants.NORMAL);
        warehouse.setStatus(BizConstants.STATUS_ENABLED);
        OutboundOrderDto dto = new OutboundOrderDto();
        dto.setWarehouseId(10L);
        dto.setOrderType(OrderConstants.ORDER_TYPE_MIN);
        OutboundOrderDto.OutboundDetailDto detail = new OutboundOrderDto.OutboundDetailDto();
        detail.setItemId(404L);
        detail.setBinId(100L);
        detail.setQuantity(OrderConstants.ORDER_DETAIL_QUANTITY_MIN);
        dto.setDetails(List.of(detail));
        when(wmsOutboundOrderMapper.selectById(9002L)).thenReturn(order);
        when(wmsWarehouseMapper.selectById(10L)).thenReturn(warehouse);
        when(wmsOutboundDetailMapper.selectList(any())).thenReturn(List.of());

        try (MockedStatic<Db> db = mockStatic(Db.class)) {
            db.when(() -> Db.saveBatch(any(List.class))).thenReturn(true);
            assertThrows(BizException.class, () -> service.updateOrder(9002L, dto));
        }

        verify(wmsItemMapper).selectById(404L);
    }
}
