package com.wms.business.event;

import lombok.Getter;

/**
 * 库存同步事件
 * 当入库、出库、归还、报废、调拨等业务操作完成后发布此事件，触发库存数据同步
 */
@Getter
public class StockSyncEvent {

    /** 物品ID */
    private final Long itemId;

    /** 库房ID */
    private final Long warehouseId;

    /** 库位ID */
    private final Long binId;

    /** 变动数量(正数入库,负数出库) */
    private final Integer quantity;

    /** 同步类型(IN-入库,OUT-出库) */
    private final String type;

    /**
     * 构造库存同步事件
     *
     * @param itemId      物品ID
     * @param warehouseId 库房ID
     * @param binId       库位ID
     * @param quantity    变动数量(正数入库,负数出库)
     * @param type        同步类型(IN-入库,OUT-出库)
     */
    public StockSyncEvent(Long itemId, Long warehouseId, Long binId, Integer quantity, String type) {
        this.itemId = itemId;
        this.warehouseId = warehouseId;
        this.binId = binId;
        this.quantity = quantity;
        this.type = type;
    }
}
