package com.wms.approval.service;

import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;

/**
 * 审批数据可见性服务。
 * 校验审批人是否具备处理具体业务单据的权限与数据范围。
 */
public interface ApprovalVisibilityService {

    /**
     * 判断审批节点是否至少存在一个可见该业务单据的审批人。
     *
     * @param node    审批节点
     * @param bizId   业务单据ID
     * @param bizType 业务类型
     * @return 是否存在可见审批人
     */
    boolean hasVisibleApproverForNode(WmsApprovalNode node, Long bizId, Integer bizType);

    /**
     * 判断指定用户是否可审批该审批单。
     *
     * @param userId 审批人ID
     * @param order  审批单
     * @return 是否可审批
     */
    boolean canApprove(Long userId, WmsApprovalOrder order);
}
