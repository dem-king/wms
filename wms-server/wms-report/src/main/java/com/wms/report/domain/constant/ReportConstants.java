package com.wms.report.domain.constant;

/**
 * 报表模块常量类
 * 定义报表聚合、趋势类型等常量
 */
public final class ReportConstants {

    private ReportConstants() {
    }

    /** 趋势类型：按日 */
    public static final String TREND_TYPE_DAILY = "DAILY";

    /** 趋势类型：按月 */
    public static final String TREND_TYPE_MONTHLY = "MONTHLY";

    /** 归还物品状态：正常 */
    public static final int CONDITION_NORMAL = 1;

    /** 归还物品状态：损坏 */
    public static final int CONDITION_DAMAGED = 2;

    /** 预警类型：库存不足 */
    public static final String ALERT_TYPE_STOCK_LOW = "STOCK_LOW";

    /** 预警类型：库存超储 */
    public static final String ALERT_TYPE_STOCK_HIGH = "STOCK_HIGH";

    /** 预警类型名称：库存不足 */
    public static final String ALERT_TYPE_NAME_STOCK_LOW = "库存不足";

    /** 预警类型名称：库存超储 */
    public static final String ALERT_TYPE_NAME_STOCK_HIGH = "库存超储";

    /** 预警已处理标志：未处理 */
    public static final int ALERT_RESOLVED_NO = 0;

    /** 预警已处理标志：已处理 */
    public static final int ALERT_RESOLVED_YES = 1;

    /** 图表类型：折线图 */
    public static final String CHART_TYPE_LINE = "line";

    /** 图表类型：柱状图 */
    public static final String CHART_TYPE_BAR = "bar";

    /** 图表类型：饼图 */
    public static final String CHART_TYPE_PIE = "pie";

    /** 报表类型：入库 */
    public static final String REPORT_TYPE_INBOUND = "inbound";

    /** 报表类型：出库 */
    public static final String REPORT_TYPE_OUTBOUND = "outbound";

    /** 报表类型：库存 */
    public static final String REPORT_TYPE_STOCK = "stock";

    /** 报表类型：归还 */
    public static final String REPORT_TYPE_RETURN = "return";

    /** 报表类型：报废 */
    public static final String REPORT_TYPE_SCRAP = "scrap";

    /** 报表类型：调拨 */
    public static final String REPORT_TYPE_TRANSFER = "transfer";

    /** 报表类型：预警 */
    public static final String REPORT_TYPE_ALERT = "alert";

    /** 导出格式：Excel */
    public static final String EXPORT_TYPE_EXCEL = "EXCEL";

    /** 导出格式：PDF */
    public static final String EXPORT_TYPE_PDF = "PDF";

    /** 导出数据量上限 */
    public static final int EXPORT_MAX_ROWS = 100000;

    /** 费用核算配置键：启用开关 */
    public static final String COST_CONFIG_ENABLED_KEY = "cost_account.enabled";

    /** 费用核算配置键：核算年度 */
    public static final String COST_CONFIG_YEAR_KEY = "cost_account.year";

    /** 费用核算配置键：核算周期 */
    public static final String COST_CONFIG_PERIOD_KEY = "cost_account.period";

    /** 费用核算周期：按月 */
    public static final String COST_PERIOD_MONTHLY = "MONTHLY";

    /** 费用核算周期：按季 */
    public static final String COST_PERIOD_QUARTERLY = "QUARTERLY";

    /** 费用核算周期：按年 */
    public static final String COST_PERIOD_YEARLY = "YEARLY";

    /** 费用核算Redis缓存键前缀 */
    public static final String COST_CACHE_PREFIX = "wms:cost:";
}
