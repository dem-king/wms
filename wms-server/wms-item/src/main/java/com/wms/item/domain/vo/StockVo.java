package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存视图对象
 * 包含物品和库位关联信息
 */
@Data
@Schema(description = "库存信息")
public class StockVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 物品编号 */
    @Schema(description = "物品编号")
    private String itemCode;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

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

    /** 安全库存(来自物品档案) */
    @Schema(description = "安全库存(来自物品档案)")
    private Integer stockLowerLimit;

    /** 最大库存(来自物品档案) */
    @Schema(description = "最大库存(来自物品档案)")
    private Integer stockUpperLimit;

    /** 是否预警(quantity < stockLowerLimit) */
    @Schema(description = "是否预警(quantity < stockLowerLimit)")
    private Boolean alert;
}
