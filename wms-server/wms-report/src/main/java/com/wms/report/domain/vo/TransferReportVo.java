package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 调拨统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果，汇总含调出/调入库房维度
 */
@Data
@Schema(description = "调拨统计报表")
public class TransferReportVo {

    /**
     * 调拨分类汇总项(含调出/调入库房)
     */
    @Data
    @Schema(description = "调拨分类汇总项")
    public static class TransferCategorySummaryItem {

        /** 调出库房ID */
        @Schema(description = "调出库房ID")
        private Long fromWarehouseId;

        /** 调出库房名称 */
        @Schema(description = "调出库房名称")
        private String fromWarehouseName;

        /** 调入库房ID */
        @Schema(description = "调入库房ID")
        private Long toWarehouseId;

        /** 调入库房名称 */
        @Schema(description = "调入库房名称")
        private String toWarehouseName;

        /** 分类ID */
        @Schema(description = "分类ID")
        private Long categoryId;

        /** 分类名称 */
        @Schema(description = "分类名称")
        private String categoryName;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;

        /** 金额 */
        @Schema(description = "金额")
        private BigDecimal amount;
    }

    /**
     * 调拨汇总统计
     */
    @Data
    @Schema(description = "调拨汇总统计")
    public static class SummaryVo {

        /** 调拨总数量 */
        @Schema(description = "调拨总数量")
        private Integer totalQuantity;

        /** 调拨总金额 */
        @Schema(description = "调拨总金额")
        private BigDecimal totalAmount;

        /** 调拨单据数 */
        @Schema(description = "调拨单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<TransferCategorySummaryItem> categorySummaryList;
    }

    /**
     * 调拨趋势统计
     */
    @Data
    @Schema(description = "调拨趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 调拨分类分布统计
     */
    @Data
    @Schema(description = "调拨分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
