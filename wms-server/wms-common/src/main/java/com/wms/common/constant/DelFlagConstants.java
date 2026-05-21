package com.wms.common.constant;

/**
 * 逻辑删除常量类
 * 定义逻辑删除标志位的值，全模块统一使用
 */
public final class DelFlagConstants {

    private DelFlagConstants() {
    }

    /** 逻辑删除：正常（未删除） */
    public static final int NORMAL = 0;

    /** 逻辑删除：已删除 */
    public static final int DELETED = 1;
}
