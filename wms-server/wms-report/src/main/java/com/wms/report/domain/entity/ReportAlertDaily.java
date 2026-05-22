package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 预警日聚合实体
 * 对应表 report_alert_daily，存储按日+库房+预警类型聚合的预警统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_alert_daily")
public class ReportAlertDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 预警类型(STOCK_LOW/STOCK_HIGH) */
    @Schema(description = "预警类型(STOCK_LOW/STOCK_HIGH)")
    private String alertType;

    /** 预警触发次数 */
    @Schema(description = "预警触发次数")
    private Integer triggerCount;

    /** 涉及物品种类数 */
    @Schema(description = "涉及物品种类数")
    private Integer affectedItemCount;

    /** 已处理次数 */
    @Schema(description = "已处理次数")
    private Integer resolvedCount;
}
