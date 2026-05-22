package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报表通用视图对象
 * 提供分类汇总、趋势、分布等共用内部类
 */
@Data
@Schema(description = "报表通用视图对象")
public class CommonReportVo {

    /**
     * 分类汇总项
     */
    @Data
    @Schema(description = "分类汇总项")
    public static class CategorySummaryItem {

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
     * 趋势项
     */
    @Data
    @Schema(description = "趋势项")
    public static class TrendItem {

        /** 日期标识(日:2026-05-22, 月:2026-05) */
        @Schema(description = "日期标识")
        private String date;

        /** 数量 */
        @Schema(description = "数量")
        private Integer quantity;

        /** 金额 */
        @Schema(description = "金额")
        private BigDecimal amount;
    }

    /**
     * 分布项
     */
    @Data
    @Schema(description = "分布项")
    public static class DistributionItem {

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

        /** 占比(%) */
        @Schema(description = "占比(%)")
        private BigDecimal percentage;
    }
}
