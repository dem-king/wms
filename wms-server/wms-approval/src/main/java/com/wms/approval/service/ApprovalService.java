package com.wms.approval.service;

import com.wms.approval.domain.dto.ApprovalActionDto;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 审批服务接口
 * 提供审批流程的发起、审批通过、驳回、撤回等功能
 */
public interface ApprovalService {

    /**
     * 发起审批
     * 查询审批配置，若免审则直接返回已通过状态，否则创建审批单
     *
     * @param bizId   业务单据ID
     * @param bizType 业务类型
     * @return 审批单VO
     */
    ApprovalOrderVo startApproval(Long bizId, int bizType);

    /**
     * 审批通过
     * 校验审批单状态，记录审批结果，若为最后节点则标记审批通过
     *
     * @param approvalId 审批单ID
     * @param dto        审批操作参数
     */
    void approve(Long approvalId, ApprovalActionDto dto);

    /**
     * 审批驳回
     * 校验审批单状态，记录审批结果，标记审批单为已驳回
     *
     * @param approvalId 审批单ID
     * @param dto        审批操作参数
     */
    void reject(Long approvalId, ApprovalActionDto dto);

    /**
     * 撤回审批
     * 校验审批单状态为待审批或审批中时允许撤回
     *
     * @param approvalId 审批单ID
     */
    void revoke(Long approvalId);

    /**
     * 根据业务单据查询审批单
     *
     * @param bizId   业务单据ID
     * @param bizType 业务类型
     * @return 审批单VO
     */
    ApprovalOrderVo getByBiz(Long bizId, int bizType);

    /**
     * 分页查询审批单
     *
     * @param pageParam 分页参数
     * @param bizType   业务类型(可选)
     * @param status    审批状态(可选)
     * @return 分页结果
     */
    PageResult<ApprovalOrderVo> pageApprovals(PageParam pageParam, Integer bizType, Integer status);

    /**
     * 根据ID获取审批单详情(含记录列表)
     *
     * @param id 审批单ID
     * @return 审批单详情VO
     */
    ApprovalOrderVo getApprovalById(Long id);
}
