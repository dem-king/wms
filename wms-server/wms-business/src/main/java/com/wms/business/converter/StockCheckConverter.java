package com.wms.business.converter;

import com.wms.business.domain.entity.WmsStockCheckDetail;
import com.wms.business.domain.entity.WmsStockCheckOrder;
import com.wms.business.domain.vo.pda.StockCheckDiffDetailVo;
import com.wms.business.domain.vo.pda.StockCheckResultVo;
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
 * 盘点单转换器
 * 负责WmsStockCheckOrder/WmsStockCheckDetail实体与对应VO之间的转换
 */
@Component
@RequiredArgsConstructor
public class StockCheckConverter {

    private final WmsItemMapper wmsItemMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;

    /**
     * 盘点单实体转盘点结果VO
     *
     * @param order 盘点单实体
     * @return 盘点结果VO
     */
    public StockCheckResultVo toResultVo(WmsStockCheckOrder order) {
        StockCheckResultVo vo = new StockCheckResultVo();
        vo.setCheckId(order.getId());
        vo.setStatus(order.getStatus());
        return vo;
    }

    /**
     * 盘点差异明细实体转VO
     * 关联查询物品名称和编码
     *
     * @param detail 盘点差异明细实体
     * @return 盘点差异明细VO
     */
    public StockCheckDiffDetailVo toDiffDetailVo(WmsStockCheckDetail detail) {
        StockCheckDiffDetailVo vo = new StockCheckDiffDetailVo();
        vo.setLabelNo(detail.getLabelNo());
        vo.setItemId(detail.getItemId());
        vo.setDiffType(detail.getDiffType());
        vo.setSystemQty(detail.getSystemQty());
        vo.setActualQty(detail.getActualQty());
        // 关联查询物品信息
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
     * 批量转换盘点差异明细列表
     * 使用预查询的物品Map避免N+1查询
     *
     * @param details   盘点差异明细实体列表
     * @param itemMap   物品ID到实体的映射
     * @return 盘点差异明细VO列表
     */
    public List<StockCheckDiffDetailVo> toDiffDetailVoList(List<WmsStockCheckDetail> details,
                                                            Map<Long, WmsItem> itemMap) {
        return details.stream().map(detail -> {
            StockCheckDiffDetailVo vo = new StockCheckDiffDetailVo();
            vo.setLabelNo(detail.getLabelNo());
            vo.setItemId(detail.getItemId());
            vo.setDiffType(detail.getDiffType());
            vo.setSystemQty(detail.getSystemQty());
            vo.setActualQty(detail.getActualQty());
            // 从预查询Map中获取物品信息
            if (detail.getItemId() != null) {
                WmsItem item = itemMap.get(detail.getItemId());
                if (item != null) {
                    vo.setItemName(item.getItemName());
                    vo.setItemCode(item.getItemCode());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }
}