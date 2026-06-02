package com.wms.business.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundDetail;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.entity.WmsScrapDetail;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.entity.WmsTransferDetail;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnDetailMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapDetailMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.business.domain.constant.OrderConstants;
import com.wms.common.constant.BizConstants;
import com.wms.common.enums.BizTypeEnum;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.event.ApprovalResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 审批结果事件监听器
 * 监听审批模块发布的ApprovalResultEvent，审批通过后执行库存同步逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalResultEventListener {

    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsInboundDetailMapper wmsInboundDetailMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsOutboundDetailMapper wmsOutboundDetailMapper;
    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsScrapDetailMapper wmsScrapDetailMapper;
    private final WmsTransferOrderMapper wmsTransferOrderMapper;
    private final WmsTransferDetailMapper wmsTransferDetailMapper;
    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsReturnDetailMapper wmsReturnDetailMapper;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 处理审批结果事件
     * 审批通过后根据业务类型分发库存同步逻辑，驳回后将单据状态回退
     *
     * @param event 审批结果事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleApprovalResult(ApprovalResultEvent event) {
        log.info("收到审批结果事件: bizId={}, bizType={}, approved={}",
                event.getBizId(), event.getBizType(), event.isApproved());

        if (event.isApproved()) {
            handleApproved(event);
        } else {
            handleRejected(event);
        }
    }

    /**
     * 审批通过处理
     * 根据业务类型执行对应的库存同步逻辑
     *
     * @param event 审批结果事件
     */
    private void handleApproved(ApprovalResultEvent event) {
        BizTypeEnum bizType = BizTypeEnum.of(event.getBizType());
        if (bizType == null) {
            log.warn("未知的业务类型: {}", event.getBizType());
            return;
        }

        switch (bizType) {
            case INBOUND:
                handleInboundApproved(event.getBizId());
                break;
            case OUTBOUND:
                handleOutboundApproved(event.getBizId());
                break;
            case SCRAP:
                handleScrapApproved(event.getBizId());
                break;
            case TRANSFER:
                handleTransferApproved(event.getBizId());
                break;
            case RETURN:
                handleReturnApproved(event.getBizId());
                break;
            default:
                log.warn("未知的业务类型: {}", bizType.getDesc());
                break;
        }
    }

    /**
     * 入库单审批通过
     * 标记入库单为已完成，发布库存同步事件(入库)
     *
     * @param bizId 入库单ID
     */
    private void handleInboundApproved(Long bizId) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("入库单不存在: {}", bizId);
            return;
        }
        // 标记为已完成
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        wmsInboundOrderMapper.updateById(order);

        // 入库完成后增加库存(入库)
        List<WmsInboundDetail> details = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>()
                        .eq(WmsInboundDetail::getOrderId, bizId));
        for (WmsInboundDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), detail.getBinId(),
                    detail.getQuantity(), BizConstants.STOCK_SYNC_IN));
        }
    }

    /**
     * 出库单审批通过
     * 标记出库单为已完成，发布库存扣减事件(出库)
     *
     * @param bizId 出库单ID
     */
    private void handleOutboundApproved(Long bizId) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("出库单不存在: {}", bizId);
            return;
        }
        // 标记为已完成
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        wmsOutboundOrderMapper.updateById(order);

        // 出库完成后扣减库存(出库)
        List<WmsOutboundDetail> details = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>()
                        .eq(WmsOutboundDetail::getOrderId, bizId));
        for (WmsOutboundDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), detail.getBinId(),
                    -detail.getQuantity(), BizConstants.STOCK_SYNC_OUT));
        }
    }

    /**
     * 报废单审批通过
     * 标记报废单为已完成，发布库存扣减事件(出库)
     *
     * @param bizId 报废单ID
     */
    private void handleScrapApproved(Long bizId) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("报废单不存在: {}", bizId);
            return;
        }
        // 标记为已完成
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        wmsScrapOrderMapper.updateById(order);

        // 报废完成后扣减库存(出库)
        List<WmsScrapDetail> details = wmsScrapDetailMapper.selectList(
                new LambdaQueryWrapper<WmsScrapDetail>()
                        .eq(WmsScrapDetail::getOrderId, bizId));
        for (WmsScrapDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), detail.getBinId(),
                    -detail.getQuantity(), BizConstants.STOCK_SYNC_OUT));
        }
    }

    /**
     * 调拨单审批通过
     * 标记调拨单为已完成，发布库存同步事件(调出库房出库、调入库房入库)
     *
     * @param bizId 调拨单ID
     */
    private void handleTransferApproved(Long bizId) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("调拨单不存在: {}", bizId);
            return;
        }
        // 标记为已完成
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        wmsTransferOrderMapper.updateById(order);

        // 调拨完成后：调出库房出库(负数)，调入库房入库(正数)
        List<WmsTransferDetail> details = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, bizId));
        for (WmsTransferDetail detail : details) {
            // 调出库房出库
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getFromWarehouseId(), detail.getFromBinId(),
                    -detail.getQuantity(), BizConstants.STOCK_SYNC_OUT));
            // 调入库房入库
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getToWarehouseId(), detail.getToBinId(),
                    detail.getQuantity(), BizConstants.STOCK_SYNC_IN));
        }
    }

    /**
     * 归还单审批通过
     * 标记归还单为已完成，查关联出库单获取warehouseId，发布库存同步事件(归还入库)
     * 异常归还处理：损坏/丢失的明细不入库，数量不符的按实际归还数量入库
     *
     * @param bizId 归还单ID
     */
    private void handleReturnApproved(Long bizId) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("归还单不存在: {}", bizId);
            return;
        }
        // 标记为已完成
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        wmsReturnOrderMapper.updateById(order);

        // 归还入库：查关联出库单获取库房ID
        WmsOutboundOrder outboundOrder = wmsOutboundOrderMapper.selectById(order.getOutboundOrderId());
        if (outboundOrder == null) {
            log.warn("关联出库单不存在: {}", order.getOutboundOrderId());
            return;
        }
        List<WmsReturnDetail> details = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, bizId));
        for (WmsReturnDetail detail : details) {
            // 异常归还：损坏或丢失的物品不归还入库
            if (detail.getConditionStatus() != null
                    && (detail.getConditionStatus() == OrderConstants.RETURN_CONDITION_DAMAGED
                    || detail.getConditionStatus() == OrderConstants.RETURN_CONDITION_LOST)) {
                log.info("异常归还不入库: itemId={}, conditionStatus={}", detail.getItemId(), detail.getConditionStatus());
                continue;
            }
            // 数量不符时按实际归还数量入库，否则按原数量入库
            int returnQty = detail.getQuantity();
            if (detail.getConditionStatus() != null
                    && detail.getConditionStatus() == OrderConstants.RETURN_CONDITION_MISMATCH
                    && detail.getActualQuantity() != null) {
                returnQty = detail.getActualQuantity();
            }
            if (returnQty > 0) {
                eventPublisher.publishEvent(new StockSyncEvent(
                        detail.getItemId(), outboundOrder.getWarehouseId(), detail.getBinId(),
                        returnQty, BizConstants.STOCK_SYNC_IN));
            }
        }
    }

    /**
     * 审批驳回处理
     * 将业务单据状态回退为草稿，允许重新编辑提交
     *
     * @param event 审批结果事件
     */
    private void handleRejected(ApprovalResultEvent event) {
        BizTypeEnum bizType = BizTypeEnum.of(event.getBizType());
        if (bizType == null) {
            log.warn("未知的业务类型: {}", event.getBizType());
            return;
        }

        switch (bizType) {
            case INBOUND:
                handleInboundRejected(event.getBizId());
                break;
            case OUTBOUND:
                handleOutboundRejected(event.getBizId());
                break;
            case SCRAP:
                handleScrapRejected(event.getBizId());
                break;
            case TRANSFER:
                handleTransferRejected(event.getBizId());
                break;
            case RETURN:
                handleReturnRejected(event.getBizId());
                break;
            default:
                log.warn("未知的业务类型: {}", bizType.getDesc());
                break;
        }
    }

    /**
     * 入库单审批驳回
     * 将入库单状态回退为草稿
     *
     * @param bizId 入库单ID
     */
    private void handleInboundRejected(Long bizId) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("入库单不存在: {}", bizId);
            return;
        }
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        wmsInboundOrderMapper.updateById(order);
    }

    /**
     * 出库单审批驳回
     * 将出库单状态回退为草稿
     *
     * @param bizId 出库单ID
     */
    private void handleOutboundRejected(Long bizId) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("出库单不存在: {}", bizId);
            return;
        }
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        wmsOutboundOrderMapper.updateById(order);
    }

    /**
     * 报废单审批驳回
     * 将报废单状态回退为草稿
     *
     * @param bizId 报废单ID
     */
    private void handleScrapRejected(Long bizId) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("报废单不存在: {}", bizId);
            return;
        }
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        wmsScrapOrderMapper.updateById(order);
    }

    /**
     * 调拨单审批驳回
     * 将调拨单状态回退为草稿
     *
     * @param bizId 调拨单ID
     */
    private void handleTransferRejected(Long bizId) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("调拨单不存在: {}", bizId);
            return;
        }
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        wmsTransferOrderMapper.updateById(order);
    }

    /**
     * 归还单审批驳回
     * 将归还单状态回退为草稿
     *
     * @param bizId 归还单ID
     */
    private void handleReturnRejected(Long bizId) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(bizId);
        if (order == null) {
            log.warn("归还单不存在: {}", bizId);
            return;
        }
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        wmsReturnOrderMapper.updateById(order);
    }
}
