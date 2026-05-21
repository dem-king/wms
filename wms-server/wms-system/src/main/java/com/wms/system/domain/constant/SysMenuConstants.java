package com.wms.system.domain.constant;

/**
 * 菜单常量类
 * 定义菜单相关的状态、可见性等常量值
 */
public final class SysMenuConstants {

    private SysMenuConstants() {
    }

    /** 菜单状态：启用 */
    public static final int STATUS_ENABLED = 1;

    /** 菜单状态：禁用 */
    public static final int STATUS_DISABLED = 0;

    /** 菜单是否可见：显示 */
    public static final int VISIBLE_YES = 1;

    /** 菜单是否可见：隐藏 */
    public static final int VISIBLE_NO = 0;

    /** 菜单类型：目录 */
    public static final int MENU_TYPE_DIR = 1;

    /** 菜单类型：菜单 */
    public static final int MENU_TYPE_MENU = 2;

    /** 菜单类型：按钮/操作 */
    public static final int MENU_TYPE_BUTTON = 3;
}
