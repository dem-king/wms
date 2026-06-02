package com.wms.business.converter;

import com.wms.business.domain.entity.WmsScrapDetail;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.vo.ScrapOrderVo;
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
 * 报废单转换器
 * 负责WmsScrapOrder实体与ScrapOrderVo之间的转换，关联信息从预查询的Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class ScrapOrderConverter {

    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final WmsItemMapper wmsItemMapper;

    /**
     * WmsScrapOrder实体转ScrapOrderVo(填充库房名称)
     *
     * @param order 报废单实体
     * @param warehouseMap 库房ID到实体的映射
     * @return 报废单VO
     */
    public ScrapOrderVo toVo(WmsScrapOrder order, Map<Long, WmsWarehouse> warehouseMap) {
        ScrapOrderVo vo = new ScrapOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setWarehouseId(order.getWarehouseId());
        vo.setStatus(order.getStatus());
        vo.setScrapReason(order.getScrapReason());
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
        return vo;
    }

    /**
     * WmsScrapDetail实体转ScrapDetailVo(填充物品名称)
     *
     * @param detail 报废明细实体
     * @return 报废明细VO
     */
    public ScrapOrderVo.ScrapDetailVo toDetailVo(WmsScrapDetail detail) {
        ScrapOrderVo.ScrapDetailVo vo = new ScrapOrderVo.ScrapDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setBinId(detail.getBinId());
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
     * 批量转换报废明细列表
     *
     * @param details 报废明细实体列表
     * @return 报废明细VO列表
     */
    public List<ScrapOrderVo.ScrapDetailVo> toDetailVoList(List<WmsScrapDetail> details) {
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }
}
