package com.wms.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 库存预警记录实体
 * 对应表 monitor_stock_alert，存储库存预警扫描结果
 * 与wms-report模块的基础版实体同表，本实体包含完整字段供预警扫描和查询使用
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_stock_alert")
public class MonitorStockAlert extends BaseEntity {

    /** 预警类型: STOCK_LOW/STOCK_HIGH */
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

    /** 处理状态: PENDING/RESOLVED */
    @Schema(description = "处理状态")
    @TableField("`status`")
    private String status;

    /** 触发时间 */
    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;
}
