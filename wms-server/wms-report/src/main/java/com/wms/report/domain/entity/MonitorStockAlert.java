package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 库存预警实体(P4-38基础版本)
 * 对应表 monitor_stock_alert，供预警日聚合任务使用
 * 注意：此实体为P4-38预警模块的基础版本，完整版本在wms-monitor模块中实现
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("monitor_stock_alert")
public class MonitorStockAlert extends BaseEntity {

    /** 预警类型(STOCK_LOW/STOCK_HIGH) */
    @Schema(description = "预警类型(STOCK_LOW/STOCK_HIGH)")
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

    /** 处理状态(PENDING/RESOLVED) */
    @Schema(description = "处理状态(PENDING/RESOLVED)")
    @TableField("`status`")
    private String status;

    /** 触发时间 */
    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;

    /** 是否已处理(0-未处理 1-已处理,兼容P4-34聚合任务) */
    @Schema(description = "是否已处理(0-未处理 1-已处理)")
    private Integer isResolved;
}
