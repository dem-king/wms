package com.wms.business.domain.constant;

/**
 * 业务单据常量类
 * 定义各类单据编号前缀等常量
 */
public final class OrderConstants {

    private OrderConstants() {
    }

    /** 入库单号前缀 */
    public static final String INBOUND_NO_PREFIX = "RK";

    /** 出库单号前缀 */
    public static final String OUTBOUND_NO_PREFIX = "CK";

    /** 归还单号前缀 */
    public static final String RETURN_NO_PREFIX = "GH";

    /** 报废单号前缀 */
    public static final String SCRAP_NO_PREFIX = "BF";

    /** 调拨单号前缀 */
    public static final String TRANSFER_NO_PREFIX = "DB";

    /** 单据类型最小值 */
    public static final int ORDER_TYPE_MIN = 1;

    /** 单据类型最大值 */
    public static final int ORDER_TYPE_MAX = 3;

    /** 单据明细最小条数 */
    public static final int ORDER_DETAIL_MIN_SIZE = 1;

    /** 单据明细数量最小值 */
    public static final int ORDER_DETAIL_QUANTITY_MIN = 1;

    /** 归还物品状态：正常 */
    public static final int RETURN_CONDITION_NORMAL = 1;

    /** 归还物品状态：损坏 */
    public static final int RETURN_CONDITION_DAMAGED = 2;

    /** 归还物品状态：丢失 */
    public static final int RETURN_CONDITION_LOST = 3;

    /** 归还物品状态：数量不符 */
    public static final int RETURN_CONDITION_MISMATCH = 4;

    /** 归还物品状态最小值 */
    public static final int RETURN_CONDITION_MIN = RETURN_CONDITION_NORMAL;

    /** 归还物品状态最大值 */
    public static final int RETURN_CONDITION_MAX = RETURN_CONDITION_MISMATCH;
}
