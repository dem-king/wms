package com.wms.monitor.domain.constant;

/**
 * 监控模块常量类
 * 定义预警类型、状态、提醒级别等常量
 */
public final class MonitorConstants {

    private MonitorConstants() {
    }

    /** 预警类型：库存不足 */
    public static final String ALERT_TYPE_STOCK_LOW = "STOCK_LOW";

    /** 预警类型：库存超储 */
    public static final String ALERT_TYPE_STOCK_HIGH = "STOCK_HIGH";

    /** 预警状态：未处理 */
    public static final String ALERT_STATUS_PENDING = "PENDING";

    /** 预警状态：已处理 */
    public static final String ALERT_STATUS_RESOLVED = "RESOLVED";

    /** 提醒级别：一般(逾期1-7天) */
    public static final String ALERT_LEVEL_NORMAL = "NORMAL";

    /** 提醒级别：重要(逾期8-30天) */
    public static final String ALERT_LEVEL_IMPORTANT = "IMPORTANT";

    /** 提醒级别：紧急(逾期>30天) */
    public static final String ALERT_LEVEL_URGENT = "URGENT";

    /** 逾期重要级别阈值(天) */
    public static final int OVERDUE_IMPORTANT_THRESHOLD = 7;

    /** 逾期紧急级别阈值(天) */
    public static final int OVERDUE_URGENT_THRESHOLD = 30;

    /** 出库类型：领用出库 */
    public static final int OUTBOUND_TYPE_BORROW = 1;

    /** 出库状态：已完成 */
    public static final int OUTBOUND_STATUS_COMPLETED = 3;
}
