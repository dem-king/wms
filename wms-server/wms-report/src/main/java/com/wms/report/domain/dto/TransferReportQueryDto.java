package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 调拨报表查询参数
 * 扩展基础查询参数，增加调出/调入库房筛选维度
 */
@Data
@Schema(description = "调拨报表查询参数")
public class TransferReportQueryDto {

    /** 开始日期 */
    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /** 结束日期 */
    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /** 调出库房ID(可选) */
    @Schema(description = "调出库房ID")
    private Long fromWarehouseId;

    /** 调入库房ID(可选) */
    @Schema(description = "调入库房ID")
    private Long toWarehouseId;

    /** 物品分类ID(可选) */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 趋势类型(可选，DAILY/MONTHLY) */
    @Schema(description = "趋势类型(DAILY/MONTHLY)")
    private String trendType;
}
