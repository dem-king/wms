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

    /** 归还物品状态：正常 */
    public static final int RETURN_CONDITION_NORMAL = 1;

    /** 归还物品状态：损坏 */
    public static final int RETURN_CONDITION_DAMAGED = 2;

    /** 归还物品状态：丢失 */
    public static final int RETURN_CONDITION_LOST = 3;

    /** 归还物品状态：数量不符 */
    public static final int RETURN_CONDITION_MISMATCH = 4;
}
