package com.wms.item.domain.constant;

/**
 * 物品模块常量类
 * 定义物品、分类相关的业务常量
 */
public final class ItemConstants {

    private ItemConstants() {
    }

    /** 物品编号前缀 */
    public static final String ITEM_CODE_PREFIX = "WP";

    /** 物品是否消耗品：否 */
    public static final int IS_CONSUMABLE_NO = 0;

    /** 物品是否可归还：是 */
    public static final int IS_RETURNABLE_YES = 1;

    /** 默认库存量 */
    public static final int DEFAULT_STOCK_QTY = 0;

    /** 快速搜索结果限制条数 */
    public static final int QUICK_SEARCH_LIMIT = 50;
}
