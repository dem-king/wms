package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 报表导出参数
 */
@Data
@Schema(description = "报表导出参数")
public class ExportQueryDto {

    /** 报表类型: inbound/outbound/stock/return/scrap/transfer/alert */
    @NotNull(message = "报表类型不能为空")
    @Schema(description = "报表类型")
    private String reportType;

    /** 导出格式: EXCEL/PDF */
    @NotNull(message = "导出格式不能为空")
    @Schema(description = "导出格式")
    private String exportType;

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
