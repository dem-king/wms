package com.wms.approval.strategy;

import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.approval.converter.ApprovalOrderConverter;
import com.wms.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单级审批策略
 * 仅有一个审批节点，创建审批单后等待审批人操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SingleApprovalStrategy implements ApprovalStrategy {

    private final WmsApprovalOrderMapper wmsApprovalOrderMapper;
    private final ApprovalOrderConverter approvalOrderConverter;

    /**
     * 单级审批执行：校验仅一个审批节点，创建审批中状态的审批单
     *
     * @param context 审批上下文
     * @return 审批单VO(状态为审批中)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalOrderVo execute(ApprovalContext context) {
        log.info("单级审批策略执行: bizId={}, bizType={}", context.getBizId(), context.getBizType());

        List<WmsApprovalNode> nodes = context.getNodes();
        if (nodes.size() != ApprovalConstants.FIRST_STEP_ORDER) {
            throw new BizException("单级审批配置应有且仅有1个审批节点，当前有" + nodes.size() + "个");
        }

        WmsApprovalOrder order = new WmsApprovalOrder();
        order.setBizId(context.getBizId());
        order.setBizType(context.getBizType());
        order.setConfigId(context.getConfig().getId());
        order.setStatus(ApprovalConstants.STATUS_APPROVING);
        order.setApplicantId(context.getApplicantId());
        order.setCurrentStep(ApprovalConstants.FIRST_STEP_ORDER);
        order.setTotalSteps(nodes.size());
        wmsApprovalOrderMapper.insert(order);

        return approvalOrderConverter.toVo(order);
    }
}
