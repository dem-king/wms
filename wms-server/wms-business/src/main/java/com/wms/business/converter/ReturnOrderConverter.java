package com.wms.business.converter;

import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.vo.ReturnOrderVo;
import com.wms.item.domain.entity.WmsItem;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 归还单转换器
 * 负责WmsReturnOrder实体与ReturnOrderVo之间的转换，关联信息从预查询的Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class ReturnOrderConverter {

    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;

    /**
     * WmsReturnOrder实体转ReturnOrderVo(填充出库单号)
     *
     * @param order 归还单实体
     * @param outboundOrderMap 出库单ID到实体的映射
     * @return 归还单VO
     */
    public ReturnOrderVo toVo(WmsReturnOrder order, Map<Long, WmsOutboundOrder> outboundOrderMap) {
        ReturnOrderVo vo = new ReturnOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOutboundOrderId(order.getOutboundOrderId());
        vo.setStatus(order.getStatus());
        vo.setReceiver(order.getReceiver());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        if (order.getOutboundOrderId() != null) {
            WmsOutboundOrder outboundOrder = outboundOrderMap.get(order.getOutboundOrderId());
            if (outboundOrder == null) {
                outboundOrder = wmsOutboundOrderMapper.selectById(order.getOutboundOrderId());
            }
            if (outboundOrder != null) {
                vo.setOutboundOrderNo(outboundOrder.getOrderNo());
            }
        }
        return vo;
    }

    /**
     * WmsReturnDetail实体转ReturnDetailVo(从物品Map中填充名称，避免N+1)
     *
     * @param detail 归还明细实体
     * @param itemMap 物品ID到实体的映射
     * @return 归还明细VO
     */
    public ReturnOrderVo.ReturnDetailVo toDetailVo(WmsReturnDetail detail, Map<Long, WmsItem> itemMap) {
        ReturnOrderVo.ReturnDetailVo vo = new ReturnOrderVo.ReturnDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        vo.setConditionStatus(detail.getConditionStatus());
        vo.setAbnormalRemark(detail.getAbnormalRemark());
        vo.setActualQuantity(detail.getActualQuantity());
        if (detail.getItemId() != null) {
            WmsItem item = itemMap.get(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        return vo;
    }

    /**
     * 批量转换归还明细列表(批量查询物品避免N+1)
     *
     * @param details 归还明细实体列表
     * @param itemMap 物品ID到实体的映射
     * @return 归还明细VO列表
     */
    public List<ReturnOrderVo.ReturnDetailVo> toDetailVoList(List<WmsReturnDetail> details, Map<Long, WmsItem> itemMap) {
        return details.stream()
                .map(detail -> toDetailVo(detail, itemMap))
                .collect(Collectors.toList());
    }
}
