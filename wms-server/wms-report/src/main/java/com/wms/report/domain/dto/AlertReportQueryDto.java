package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 预警报表查询参数
 * 扩展基础查询参数，增加预警类型筛选维度
 */
@Data
@Schema(description = "预警报表查询参数")
public class AlertReportQueryDto {

    /** 开始日期 */
    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /** 结束日期 */
    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /** 库房ID(可选) */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 预警类型(可选，STOCK_LOW/STOCK_HIGH) */
    @Schema(description = "预警类型(STOCK_LOW/STOCK_HIGH)")
    private String alertType;

    /** 趋势类型(可选，DAILY/MONTHLY) */
    @Schema(description = "趋势类型(DAILY/MONTHLY)")
    private String trendType;
}
