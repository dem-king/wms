package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 图表查询参数
 */
@Data
@Schema(description = "图表查询参数")
public class ChartQueryDto {

    /** 报表类型: inbound/outbound/stock/return/scrap/transfer/alert */
    @NotNull(message = "报表类型不能为空")
    @Schema(description = "报表类型")
    private String reportType;

    /** 图表类型: line/bar/pie */
    @NotNull(message = "图表类型不能为空")
    @Schema(description = "图表类型")
    private String chartType;

    /** 趋势粒度: DAILY/MONTHLY */
    @Schema(description = "趋势粒度")
    private String trendType;

    /** 开始日期(必填) */
    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /** 结束日期(必填) */
    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /** 库房ID(可选) */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID(可选) */
    @Schema(description = "物品分类ID")
    private Long categoryId;
}
