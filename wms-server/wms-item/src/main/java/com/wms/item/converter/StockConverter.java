package com.wms.item.converter;

import com.wms.item.domain.entity.WmsStock;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.vo.StockVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存转换器
 * 负责WmsStock实体与StockVo之间的转换，物品信息从预查询的Map中填充以避免N+1
 */
@Component
public class StockConverter {

    /**
     * WmsStock实体转StockVo
     *
     * @param stock 库存实体
     * @param itemMap 物品ID到实体的映射
     * @return 库存VO
     */
    public StockVo toVo(WmsStock stock, Map<Long, WmsItem> itemMap) {
        StockVo vo = new StockVo();
        vo.setId(stock.getId());
        vo.setItemId(stock.getItemId());
        vo.setBinId(stock.getBinId());
        vo.setWarehouseId(stock.getWarehouseId());
        vo.setAreaId(stock.getAreaId());
        vo.setCabinetId(stock.getCabinetId());
        vo.setQuantity(stock.getQuantity());
        vo.setLockedQuantity(stock.getLockedQuantity());
        vo.setAmount(stock.getAmount());
        vo.setLastInboundTime(stock.getLastInboundTime());
        vo.setLastOutboundTime(stock.getLastOutboundTime());
        if (stock.getItemId() != null) {
            WmsItem item = itemMap.get(stock.getItemId());
            if (item != null) {
                vo.setItemCode(item.getItemCode());
                vo.setItemName(item.getItemName());
                vo.setStockLowerLimit(item.getStockLowerLimit());
                vo.setStockUpperLimit(item.getStockUpperLimit());
                vo.setAlert(item.getStockLowerLimit() != null
                        && stock.getQuantity() != null
                        && stock.getQuantity() < item.getStockLowerLimit());
            }
        }
        return vo;
    }

    /**
     * 批量转换库存实体列表
     *
     * @param stocks 库存实体列表
     * @param itemMap 物品ID到实体的映射
     * @return 库存VO列表
     */
    public List<StockVo> toVoList(List<WmsStock> stocks, Map<Long, WmsItem> itemMap) {
        return stocks.stream()
                .map(stock -> toVo(stock, itemMap))
                .collect(Collectors.toList());
    }
}