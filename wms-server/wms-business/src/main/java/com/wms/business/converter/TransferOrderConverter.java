package com.wms.business.converter;

import com.wms.business.domain.entity.WmsTransferDetail;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.domain.vo.TransferOrderVo;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调拨单转换器
 * 负责WmsTransferOrder实体与TransferOrderVo之间的转换，关联信息从预查询的Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class TransferOrderConverter {

    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final WmsItemMapper wmsItemMapper;

    /**
     * WmsTransferOrder实体转TransferOrderVo(填充调出/调入库房名称)
     *
     * @param order 调拨单实体
     * @param warehouseMap 库房ID到实体的映射
     * @return 调拨单VO
     */
    public TransferOrderVo toVo(WmsTransferOrder order, Map<Long, WmsWarehouse> warehouseMap) {
        TransferOrderVo vo = new TransferOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setFromWarehouseId(order.getFromWarehouseId());
        vo.setToWarehouseId(order.getToWarehouseId());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        // 从Map中填充调出库房名称
        if (order.getFromWarehouseId() != null) {
            WmsWarehouse fromWarehouse = warehouseMap.get(order.getFromWarehouseId());
            if (fromWarehouse == null) {
                fromWarehouse = wmsWarehouseMapper.selectById(order.getFromWarehouseId());
            }
            if (fromWarehouse != null) {
                vo.setFromWarehouseName(fromWarehouse.getWarehouseName());
            }
        }
        // 从Map中填充调入库房名称
        if (order.getToWarehouseId() != null) {
            WmsWarehouse toWarehouse = warehouseMap.get(order.getToWarehouseId());
            if (toWarehouse == null) {
                toWarehouse = wmsWarehouseMapper.selectById(order.getToWarehouseId());
            }
            if (toWarehouse != null) {
                vo.setToWarehouseName(toWarehouse.getWarehouseName());
            }
        }
        return vo;
    }

    /**
     * WmsTransferDetail实体转TransferDetailVo(填充物品名称)
     *
     * @param detail 调拨明细实体
     * @return 调拨明细VO
     */
    public TransferOrderVo.TransferDetailVo toDetailVo(WmsTransferDetail detail) {
        TransferOrderVo.TransferDetailVo vo = new TransferOrderVo.TransferDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        if (detail.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        return vo;
    }

    /**
     * 批量转换调拨明细列表
     *
     * @param details 调拨明细实体列表
     * @return 调拨明细VO列表
     */
    public List<TransferOrderVo.TransferDetailVo> toDetailVoList(List<WmsTransferDetail> details) {
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }
}
