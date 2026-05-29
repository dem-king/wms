package com.wms.common.constant;

/**
 * 数据范围常量类。
 */
public final class DataScopeConstants {

    private DataScopeConstants() {
    }

    /** 数据范围：全部数据。 */
    public static final int SCOPE_ALL = 1;

    /** 数据范围：自定义部门数据。 */
    public static final int SCOPE_CUSTOM = 2;

    /** 数据范围：本部门数据。 */
    public static final int SCOPE_DEPT = 3;

    /** 数据范围：本部门及以下数据。 */
    public static final int SCOPE_DEPT_AND_CHILD = 4;

    /** 数据范围：仅本人数据。 */
    public static final int SCOPE_SELF = 5;
}
