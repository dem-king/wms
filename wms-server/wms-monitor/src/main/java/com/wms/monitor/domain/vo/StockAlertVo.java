package com.wms.monitor.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存预警视图对象
 */
@Data
@Schema(description = "库存预警信息")
public class StockAlertVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 预警类型 */
    @Schema(description = "预警类型")
    private String alertType;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 物品编码 */
    @Schema(description = "物品编码")
    private String itemCode;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 库房名称 */
    @Schema(description = "库房名称")
    private String warehouseName;

    /** 当前库存量 */
    @Schema(description = "当前库存量")
    private Integer currentQuantity;

    /** 触发阈值 */
    @Schema(description = "触发阈值")
    private Integer thresholdValue;

    /** 处理状态 */
    @Schema(description = "处理状态")
    private String status;

    /** 触发时间 */
    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;
}
