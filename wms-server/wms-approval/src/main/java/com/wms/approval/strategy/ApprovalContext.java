package com.wms.approval.strategy;

import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 审批上下文
 * 封装审批流程执行所需的全部上下文信息
 */
@Getter
@Builder
public class ApprovalContext {

    /** 业务单据ID */
    private final Long bizId;

    /** 业务类型 */
    private final int bizType;

    /** 申请人ID */
    private final Long applicantId;

    /** 审批配置 */
    private final WmsApprovalConfig config;

    /** 审批节点列表(按stepOrder升序) */
    private final List<WmsApprovalNode> nodes;
}
