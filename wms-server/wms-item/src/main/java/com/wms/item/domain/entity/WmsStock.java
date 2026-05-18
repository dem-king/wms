package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存实体
 * 对应表 wms_stock，存储物品在各库位的库存信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_stock")
public class WmsStock extends BaseEntity {

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 库位ID */
    @Schema(description = "库位ID")
    private Long binId;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 区域ID */
    @Schema(description = "区域ID")
    private Long areaId;

    /** 存放柜ID */
    @Schema(description = "存放柜ID")
    private Long cabinetId;

    /** 库存数量 */
    @Schema(description = "库存数量")
    private Integer quantity;

    /** 锁定数量(审批中) */
    @Schema(description = "锁定数量(审批中)")
    private Integer lockedQuantity;

    /** 库存金额 */
    @Schema(description = "库存金额")
    private BigDecimal amount;

    /** 最后入库时间 */
    @Schema(description = "最后入库时间")
    private LocalDateTime lastInboundTime;

    /** 最后出库时间 */
    @Schema(description = "最后出库时间")
    private LocalDateTime lastOutboundTime;
}
