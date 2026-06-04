package com.wms.business.converter;

import com.wms.business.domain.entity.WmsOutboundDetail;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.vo.OutboundOrderVo;
import com.wms.item.domain.entity.WmsItem;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 出库单转换器
 * WmsOutboundOrder/WmsOutboundDetail实体与Vo之间的转换逻辑
 */
@Component
public class OutboundOrderConverter {

    /**
     * WmsOutboundOrder实体转OutboundOrderVo(填充库房名称)
     *
     * @param order 出库单实体
     * @param warehouseMap 库房ID到实体的映射
     * @return 出库单VO
     */
    public OutboundOrderVo toOrderVo(WmsOutboundOrder order, Map<Long, WmsWarehouse> warehouseMap) {
        OutboundOrderVo vo = new OutboundOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setWarehouseId(order.getWarehouseId());
        vo.setOrderType(order.getOrderType());
        vo.setStatus(order.getStatus());
        vo.setReceiver(order.getReceiver());
        vo.setPurpose(order.getPurpose());
        vo.setExpectedReturnDate(order.getExpectedReturnDate());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        // 从Map中填充库房名称
        if (order.getWarehouseId() != null) {
            WmsWarehouse warehouse = warehouseMap.get(order.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        return vo;
    }

    /**
     * 批量转换出库单实体列表
     *
     * @param orders 出库单实体列表
     * @param warehouseMap 库房ID到实体的映射
     * @return 出库单VO列表
     */
    public List<OutboundOrderVo> toOrderVoList(List<WmsOutboundOrder> orders, Map<Long, WmsWarehouse> warehouseMap) {
        return orders.stream().map(order -> toOrderVo(order, warehouseMap)).collect(Collectors.toList());
    }

    /**
     * WmsOutboundDetail实体转OutboundDetailVo(使用预查询的物品和库位Map，避免N+1查询)
     *
     * @param detail 出库明细实体
     * @param itemMap 物品ID到实体的映射
     * @param binMap 库位ID到实体的映射
     * @return 出库明细VO
     */
    public OutboundOrderVo.OutboundDetailVo toDetailVo(WmsOutboundDetail detail,
                                                        Map<Long, WmsItem> itemMap,
                                                        Map<Long, WmsBin> binMap) {
        OutboundOrderVo.OutboundDetailVo vo = new OutboundOrderVo.OutboundDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setLabelId(detail.getLabelId());
        vo.setQuantity(detail.getQuantity());
        vo.setBinId(detail.getBinId());
        // 从Map中填充物品信息
        if (detail.getItemId() != null) {
            WmsItem item = itemMap.get(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        // 从Map中填充库位编码
        if (detail.getBinId() != null) {
            WmsBin bin = binMap.get(detail.getBinId());
            if (bin != null) {
                vo.setBinCode(bin.getBinCode());
            }
        }
        return vo;
    }

    /**
     * 批量转换出库明细实体列表
     *
     * @param details 出库明细实体列表
     * @param itemMap 物品ID到实体的映射
     * @param binMap 库位ID到实体的映射
     * @return 出库明细VO列表
     */
    public List<OutboundOrderVo.OutboundDetailVo> toDetailVoList(List<WmsOutboundDetail> details,
                                                                  Map<Long, WmsItem> itemMap,
                                                                  Map<Long, WmsBin> binMap) {
        return details.stream().map(detail -> toDetailVo(detail, itemMap, binMap)).collect(Collectors.toList());
    }
}
