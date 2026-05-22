package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报废统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果
 */
@Data
@Schema(description = "报废统计报表")
public class ScrapReportVo {

    /**
     * 报废汇总统计
     */
    @Data
    @Schema(description = "报废汇总统计")
    public static class SummaryVo {

        /** 报废总数量 */
        @Schema(description = "报废总数量")
        private Integer totalQuantity;

        /** 报废总金额 */
        @Schema(description = "报废总金额")
        private BigDecimal totalAmount;

        /** 报废单据数 */
        @Schema(description = "报废单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 报废趋势统计
     */
    @Data
    @Schema(description = "报废趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 报废分类分布统计
     */
    @Data
    @Schema(description = "报废分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
