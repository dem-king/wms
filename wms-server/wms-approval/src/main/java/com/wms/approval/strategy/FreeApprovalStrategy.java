package com.wms.approval.strategy;

import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.approval.converter.ApprovalOrderConverter;
import com.wms.common.event.ApprovalResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 免审策略
 * 审批配置标记免审时，直接创建已通过状态的审批单并发布审批结果事件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FreeApprovalStrategy implements ApprovalStrategy {

    private final WmsApprovalOrderMapper wmsApprovalOrderMapper;
    private final ApprovalOrderConverter approvalOrderConverter;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 免审执行：创建已通过状态的审批单，发布审批结果事件
     *
     * @param context 审批上下文
     * @return 审批单VO(状态为已通过)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalOrderVo execute(ApprovalContext context) {
        log.info("免审策略执行: bizId={}, bizType={}", context.getBizId(), context.getBizType());

        WmsApprovalOrder order = new WmsApprovalOrder();
        order.setBizId(context.getBizId());
        order.setBizType(context.getBizType());
        order.setStatus(ApprovalConstants.STATUS_APPROVED);
        order.setApplicantId(context.getApplicantId());
        order.setCurrentStep(ApprovalConstants.EMPTY_STEP_COUNT);
        order.setTotalSteps(ApprovalConstants.EMPTY_STEP_COUNT);
        wmsApprovalOrderMapper.insert(order);

        // 免审通过发布审批结果事件，通知业务模块执行后续逻辑
        eventPublisher.publishEvent(new ApprovalResultEvent(context.getBizId(), context.getBizType(), true));

        return approvalOrderConverter.toVo(order);
    }
}
