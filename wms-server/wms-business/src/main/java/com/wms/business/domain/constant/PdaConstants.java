package com.wms.business.domain.constant;

/**
 * PDA专用接口常量类
 * 定义盘点、RFID上报、待办任务等PDA场景的业务常量
 */
public final class PdaConstants {

    private PdaConstants() {
    }

    /** 盘点单号前缀 */
    public static final String STOCK_CHECK_NO_PREFIX = "PD";

    /** 盘点类型：全盘 */
    public static final int CHECK_TYPE_FULL = 1;

    /** 盘点类型：抽盘 */
    public static final int CHECK_TYPE_PARTIAL = 2;

    /** 盘点状态：草稿 */
    public static final int CHECK_STATUS_DRAFT = 0;

    /** 盘点状态：已提交 */
    public static final int CHECK_STATUS_SUBMITTED = 1;

    /** 盘点状态：已确认 */
    public static final int CHECK_STATUS_CONFIRMED = 2;

    /** 差异类型：盘盈(实际有但系统无) */
    public static final String DIFF_TYPE_SURPLUS = "surplus";

    /** 差异类型：盘亏(系统有但实际无) */
    public static final String DIFF_TYPE_DEFICIT = "deficit";

    /** 待办任务类型：待提交入库单 */
    public static final String TASK_TYPE_INBOUND_PENDING = "inbound_pending";

    /** 待办任务类型：待提交出库单 */
    public static final String TASK_TYPE_OUTBOUND_PENDING = "outbound_pending";

    /** 待办任务类型：待提交归还单 */
    public static final String TASK_TYPE_RETURN_PENDING = "return_pending";

    /** 待办任务类型：待审批单据 */
    public static final String TASK_TYPE_APPROVAL_PENDING = "approval_pending";

    /** 待办任务类型：库存预警 */
    public static final String TASK_TYPE_STOCK_ALERT = "stock_alert";

    /** 待办任务类型：超期归还 */
    public static final String TASK_TYPE_OVERDUE_RETURN = "overdue_return";
}