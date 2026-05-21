package com.wms.business.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.entity.WmsScrapDetail;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.entity.WmsTransferDetail;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnDetailMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapDetailMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
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

    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsScrapDetailMapper wmsScrapDetailMapper;
    private final WmsTransferOrderMapper wmsTransferOrderMapper;
    private final WmsTransferDetailMapper wmsTransferDetailMapper;
    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsReturnDetailMapper wmsReturnDetailMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
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
                log.info("业务类型{}暂不需要审批后库存同步", bizType.getDesc());
                break;
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
                    detail.getItemId(), order.getWarehouseId(), null,
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
                    detail.getItemId(), order.getFromWarehouseId(), null,
                    -detail.getQuantity(), BizConstants.STOCK_SYNC_OUT));
            // 调入库房入库
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getToWarehouseId(), null,
                    detail.getQuantity(), BizConstants.STOCK_SYNC_IN));
        }
    }

    /**
     * 归还单审批通过
     * 标记归还单为已完成，查关联出库单获取warehouseId，发布库存同步事件(归还入库)
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
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), outboundOrder.getWarehouseId(), null,
                    detail.getQuantity(), BizConstants.STOCK_SYNC_IN));
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
                log.info("业务类型{}暂不需要审批驳回处理", bizType.getDesc());
                break;
        }
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
