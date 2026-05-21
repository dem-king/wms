package com.wms.common.event;

import lombok.Getter;

/**
 * 审批请求事件
 * 业务单据提交后发布此事件，触发审批流程
 */
@Getter
public class ApprovalRequestEvent {

    /** 业务单据ID */
    private final Long bizId;

    /** 业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还) */
    private final int bizType;

    /**
     * 构造审批请求事件
     *
     * @param bizId 业务单据ID
     * @param bizType 业务类型
     */
    public ApprovalRequestEvent(Long bizId, int bizType) {
        this.bizId = bizId;
        this.bizType = bizType;
    }
}
