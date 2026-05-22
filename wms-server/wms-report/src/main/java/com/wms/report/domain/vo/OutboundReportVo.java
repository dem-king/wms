package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果
 */
@Data
@Schema(description = "出库统计报表")
public class OutboundReportVo {

    /**
     * 出库汇总统计
     */
    @Data
    @Schema(description = "出库汇总统计")
    public static class SummaryVo {

        /** 出库总数量 */
        @Schema(description = "出库总数量")
        private Integer totalQuantity;

        /** 出库总金额 */
        @Schema(description = "出库总金额")
        private BigDecimal totalAmount;

        /** 出库单据数 */
        @Schema(description = "出库单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 出库趋势统计
     */
    @Data
    @Schema(description = "出库趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 出库分类分布统计
     */
    @Data
    @Schema(description = "出库分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
