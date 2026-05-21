package com.wms.common.event;

import lombok.Getter;

/**
 * 审批结果事件
 * 审批模块审批通过/驳回后发布此事件，业务模块监听并执行后续逻辑
 */
@Getter
public class ApprovalResultEvent {

    /** 业务单据ID */
    private final Long bizId;

    /** 业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还) */
    private final int bizType;

    /** 审批结果(true-通过 false-驳回) */
    private final boolean approved;

    /**
     * 构造审批结果事件
     *
     * @param bizId 业务单据ID
     * @param bizType 业务类型
     * @param approved 审批结果
     */
    public ApprovalResultEvent(Long bizId, int bizType, boolean approved) {
        this.bizId = bizId;
        this.bizType = bizType;
        this.approved = approved;
    }
}
