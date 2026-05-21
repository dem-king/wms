package com.wms.warehouse.domain.constant;

/**
 * 库房模块常量类
 * 定义库房、区域、存放柜、库位相关的业务常量
 */
public final class WarehouseConstants {

    private WarehouseConstants() {
    }

    /** 库房编码前缀 */
    public static final String WAREHOUSE_CODE_PREFIX = "KF";

    /** 区域编码前缀 */
    public static final String AREA_CODE_PREFIX = "QY";

    /** 存放柜编码前缀 */
    public static final String CABINET_CODE_PREFIX = "CG";

    /** 库位占用状态：空闲 */
    public static final int IS_OCCUPIED_NO = 0;

    /** 库位占用状态：占用 */
    public static final int IS_OCCUPIED_YES = 1;
}
