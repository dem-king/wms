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
 * 多级审批策略
 * 有多个审批节点(按stepOrder顺序执行)，创建审批单后从第一个节点开始等待审批
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MultiApprovalStrategy implements ApprovalStrategy {

    private final WmsApprovalOrderMapper wmsApprovalOrderMapper;
    private final ApprovalOrderConverter approvalOrderConverter;

    /**
     * 多级审批执行：校验至少2个审批节点，创建审批中状态的审批单，从第1步开始
     *
     * @param context 审批上下文
     * @return 审批单VO(状态为审批中)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApprovalOrderVo execute(ApprovalContext context) {
        log.info("多级审批策略执行: bizId={}, bizType={}, nodeCount={}",
                context.getBizId(), context.getBizType(), context.getNodes().size());

        List<WmsApprovalNode> nodes = context.getNodes();
        if (nodes.size() < ApprovalConstants.RESULT_REJECTED) {
            throw new BizException("多级审批配置应至少有2个审批节点，当前有" + nodes.size() + "个");
        }

        WmsApprovalOrder order = new WmsApprovalOrder();
        order.setBizId(context.getBizId());
        order.setBizType(context.getBizType());
        order.setStatus(ApprovalConstants.STATUS_APPROVING);
        order.setApplicantId(context.getApplicantId());
        // 从第1个节点开始审批
        order.setCurrentStep(ApprovalConstants.STATUS_APPROVED);
        order.setTotalSteps(nodes.size());
        wmsApprovalOrderMapper.insert(order);

        return approvalOrderConverter.toVo(order);
    }
}
