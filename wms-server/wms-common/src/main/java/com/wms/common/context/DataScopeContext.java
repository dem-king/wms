package com.wms.common.context;

import com.wms.common.datascope.DataScopeCondition;

/**
 * 数据范围上下文。
 */
public final class DataScopeContext {

    private static final ThreadLocal<DataScopeCondition> HOLDER = new ThreadLocal<>();

    private DataScopeContext() {
    }

    /**
     * 设置当前线程的数据范围条件。
     *
     * @param condition 数据范围条件
     */
    public static void set(DataScopeCondition condition) {
        HOLDER.set(condition);
    }

    /**
     * 获取当前线程的数据范围条件。
     *
     * @return 数据范围条件
     */
    public static DataScopeCondition get() {
        return HOLDER.get();
    }

    /**
     * 清理当前线程的数据范围条件，避免线程复用导致权限串扰。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
