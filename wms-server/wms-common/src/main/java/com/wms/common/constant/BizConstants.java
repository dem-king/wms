package com.wms.common.constant;

/**
 * 业务通用常量类
 * 定义各模块共用的状态、默认值等常量
 */
public final class BizConstants {

    private BizConstants() {
    }

    /** 通用状态：启用 */
    public static final int STATUS_ENABLED = 1;

    /** 通用状态：禁用 */
    public static final int STATUS_DISABLED = 0;

    /** 默认排序号 */
    public static final int DEFAULT_SORT_ORDER = 0;

    /** 顶级父节点ID（0表示顶级） */
    public static final long TOP_PARENT_ID = 0L;

    /** 库存同步方向：入库 */
    public static final String STOCK_SYNC_IN = "IN";

    /** 库存同步方向：出库 */
    public static final String STOCK_SYNC_OUT = "OUT";
}
