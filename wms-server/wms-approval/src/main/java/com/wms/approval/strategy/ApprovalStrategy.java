package com.wms.approval.strategy;

import com.wms.approval.domain.vo.ApprovalOrderVo;

/**
 * 审批策略接口
 * 定义审批流程的执行策略，不同策略实现免审/单级/多级审批
 */
public interface ApprovalStrategy {

    /**
     * 执行审批流程
     * 根据策略类型决定免审直接通过、单级审批或多级审批
     *
     * @param context 审批上下文，包含业务单据ID、业务类型、审批配置等信息
     * @return 审批单VO
     */
    ApprovalOrderVo execute(ApprovalContext context);
}
