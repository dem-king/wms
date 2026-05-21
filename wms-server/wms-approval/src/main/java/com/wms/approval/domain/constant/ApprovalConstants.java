package com.wms.approval.domain.constant;

/**
 * 审批常量类
 * 定义审批状态、审批结果、审批人类型等常量值
 */
public final class ApprovalConstants {

    private ApprovalConstants() {
    }

    /** 审批结果：通过 */
    public static final int RESULT_APPROVED = 1;

    /** 审批结果：驳回 */
    public static final int RESULT_REJECTED = 2;

    /** 审批状态：待审批 */
    public static final int STATUS_PENDING = 0;

    /** 审批状态：审批中 */
    public static final int STATUS_APPROVING = 1;

    /** 审批状态：已通过 */
    public static final int STATUS_APPROVED = 2;

    /** 审批状态：已驳回 */
    public static final int STATUS_REJECTED = 3;

    /** 审批状态：已撤回 */
    public static final int STATUS_REVOKED = 4;

    /** 审批人类型：指定角色 */
    public static final int APPROVER_TYPE_ROLE = 1;

    /** 审批人类型：指定用户 */
    public static final int APPROVER_TYPE_USER = 2;

    /** 审批人类型：库房管理员 */
    public static final int APPROVER_TYPE_WAREHOUSE_ADMIN = 3;
}
