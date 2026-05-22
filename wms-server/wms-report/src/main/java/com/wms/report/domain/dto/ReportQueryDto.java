package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 报表统一查询参数
 * 所有统计报表接口共用此查询参数
 */
@Data
@Schema(description = "报表查询参数")
public class ReportQueryDto {

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

    /** 物品分类ID(可选) */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 趋势类型(可选，DAILY/MONTHLY，仅趋势接口使用) */
    @Schema(description = "趋势类型(DAILY/MONTHLY)")
    private String trendType;
}
