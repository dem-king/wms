package com.wms.business.converter;

import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.vo.InboundOrderVo;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import com.wms.system.domain.entity.SysSupplier;
import com.wms.system.mapper.SysSupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 入库单转换器
 * 负责WmsInboundOrder实体与InboundOrderVo之间的转换，关联信息从预查询的Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class InboundOrderConverter {

    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SysSupplierMapper sysSupplierMapper;
    private final WmsItemMapper wmsItemMapper;
    private final WmsBinMapper wmsBinMapper;

    /**
     * WmsInboundOrder实体转InboundOrderVo
     *
     * @param order 入库单实体
     * @param warehouseMap 库房ID到实体的映射
     * @param supplierMap 供应商ID到实体的映射
     * @return 入库单VO
     */
    public InboundOrderVo toVo(WmsInboundOrder order, Map<Long, WmsWarehouse> warehouseMap,
                                Map<Long, SysSupplier> supplierMap) {
        InboundOrderVo vo = new InboundOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setWarehouseId(order.getWarehouseId());
        vo.setSupplierId(order.getSupplierId());
        vo.setOrderType(order.getOrderType());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        if (order.getWarehouseId() != null) {
            WmsWarehouse warehouse = warehouseMap.get(order.getWarehouseId());
            if (warehouse == null) {
                warehouse = wmsWarehouseMapper.selectById(order.getWarehouseId());
            }
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        if (order.getSupplierId() != null) {
            SysSupplier supplier = supplierMap.get(order.getSupplierId());
            if (supplier == null) {
                supplier = sysSupplierMapper.selectById(order.getSupplierId());
            }
            if (supplier != null) {
                vo.setSupplierName(supplier.getSupplierName());
            }
        }
        return vo;
    }

    /**
     * WmsInboundDetail实体转InboundDetailVo
     *
     * @param detail 入库明细实体
     * @return 入库明细VO
     */
    public InboundOrderVo.InboundDetailVo toDetailVo(WmsInboundDetail detail) {
        InboundOrderVo.InboundDetailVo vo = new InboundOrderVo.InboundDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        vo.setUnitPrice(detail.getUnitPrice());
        vo.setAmount(detail.getAmount());
        vo.setBinId(detail.getBinId());
        if (detail.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        if (detail.getBinId() != null) {
            WmsBin bin = wmsBinMapper.selectById(detail.getBinId());
            if (bin != null) {
                vo.setBinCode(bin.getBinCode());
            }
        }
        return vo;
    }

    /**
     * 批量转换入库明细列表
     *
     * @param details 入库明细实体列表
     * @return 入库明细VO列表
     */
    public List<InboundOrderVo.InboundDetailVo> toDetailVoList(List<WmsInboundDetail> details) {
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }
}