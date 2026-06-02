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

    /** 首个审批步骤 */
    public static final int FIRST_STEP_ORDER = 1;

    /** 无审批步骤 */
    public static final int EMPTY_STEP_COUNT = 0;

    /** 多级审批最少步骤数 */
    public static final int MIN_MULTI_STEP_COUNT = 2;

    /** 步骤推进增量 */
    public static final int STEP_INCREMENT = 1;

    /** 审批单号前缀 */
    public static final String APPROVAL_NO_PREFIX = "SP";

    /** 免审节点名称 */
    public static final String FREE_APPROVAL_NODE_NAME = "免审";

    /** 审批人类型：指定角色 */
    public static final int APPROVER_TYPE_ROLE = 1;

    /** 审批人类型：指定用户 */
    public static final int APPROVER_TYPE_USER = 2;

    /** 审批人类型：库房管理员 */
    public static final int APPROVER_TYPE_WAREHOUSE_ADMIN = 3;

    /** 审批超时默认阈值(小时) */
    public static final int DEFAULT_TIMEOUT_HOURS = 48;

    /** 审批超时处理：自动提醒 */
    public static final int TIMEOUT_ACTION_REMIND = 1;

    /** 审批超时处理：自动取消 */
    public static final int TIMEOUT_ACTION_CANCEL = 2;

    /** 系统自动处理审批时使用的审批人名称 */
    public static final String SYSTEM_APPROVER_NAME = "系统";

    /** 审批超时提醒记录意见 */
    public static final String TIMEOUT_REMIND_OPINION = "审批超时提醒";

    /** 审批超时自动取消记录意见 */
    public static final String TIMEOUT_CANCEL_OPINION = "审批超时自动取消";

    /** 审批超时站内信标题 */
    public static final String TIMEOUT_MESSAGE_TITLE = "审批超时提醒";

    /** 审批超时站内信业务键前缀 */
    public static final String TIMEOUT_MESSAGE_BUSINESS_KEY_PREFIX = "approval-timeout:";

    /** 审批超时站内信目标地址模板 */
    public static final String TIMEOUT_MESSAGE_TARGET_URL_TEMPLATE = "/approval/pending?approvalId=%d";
}
