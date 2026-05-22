package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 借还统计报表视图对象
 * 包含汇总、趋势、分类分布三种统计结果，汇总额外含正常/损坏归还比例
 */
@Data
@Schema(description = "借还统计报表")
public class ReturnReportVo {

    /**
     * 借还汇总统计
     */
    @Data
    @Schema(description = "借还汇总统计")
    public static class SummaryVo {

        /** 归还总数量 */
        @Schema(description = "归还总数量")
        private Integer totalQuantity;

        /** 正常归还数量 */
        @Schema(description = "正常归还数量")
        private Integer normalQuantity;

        /** 损坏归还数量 */
        @Schema(description = "损坏归还数量")
        private Integer damagedQuantity;

        /** 正常归还率(%) */
        @Schema(description = "正常归还率(%)")
        private BigDecimal normalRate;

        /** 归还单据数 */
        @Schema(description = "归还单据数")
        private Integer orderCount;

        /** 按分类汇总列表 */
        @Schema(description = "按分类汇总列表")
        private List<CommonReportVo.CategorySummaryItem> categorySummaryList;
    }

    /**
     * 借还趋势统计
     */
    @Data
    @Schema(description = "借还趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<CommonReportVo.TrendItem> trendList;
    }

    /**
     * 借还分类分布统计
     */
    @Data
    @Schema(description = "借还分类分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<CommonReportVo.DistributionItem> distributionList;
    }
}
