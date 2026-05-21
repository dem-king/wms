package com.wms.item.domain.constant;

/**
 * 电子标签常量类
 * 定义标签编号前缀、打印状态、标签状态、标签类型等常量
 */
public final class LabelConstants {

    private LabelConstants() {
    }

    /** 标签编号前缀 */
    public static final String LABEL_NO_PREFIX = "BQ";

    /** 打印状态：未打印 */
    public static final int PRINT_STATUS_NOT = 0;

    /** 打印状态：已打印 */
    public static final int PRINT_STATUS_DONE = 1;

    /** 标签类型：RFID */
    public static final int LABEL_TYPE_RFID = 3;

    /** 闲置判定天数阈值 */
    public static final int IDLE_THRESHOLD_DAYS = 90;

    /** RFID编码随机数下限 */
    public static final int RFID_RANDOM_MIN = 1000;

    /** RFID编码随机数上限(不含) */
    public static final int RFID_RANDOM_MAX = 9000;

    /** 标签编号随机数范围 */
    public static final int LABEL_NO_RANDOM_RANGE = 10000;
}
