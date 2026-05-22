package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预警统计报表视图对象
 * 包含汇总、趋势、类型分布三种统计结果
 */
@Data
@Schema(description = "预警统计报表")
public class AlertReportVo {

    /**
     * 预警汇总项
     */
    @Data
    @Schema(description = "预警汇总项")
    public static class AlertSummaryItem {

        /** 预警类型 */
        @Schema(description = "预警类型")
        private String alertType;

        /** 预警类型名称 */
        @Schema(description = "预警类型名称")
        private String alertTypeName;

        /** 触发次数 */
        @Schema(description = "触发次数")
        private Integer triggerCount;

        /** 涉及物品种类数 */
        @Schema(description = "涉及物品种类数")
        private Integer affectedItemCount;

        /** 已处理次数 */
        @Schema(description = "已处理次数")
        private Integer resolvedCount;
    }

    /**
     * 预警汇总统计
     */
    @Data
    @Schema(description = "预警汇总统计")
    public static class SummaryVo {

        /** 总触发次数 */
        @Schema(description = "总触发次数")
        private Integer totalTriggerCount;

        /** 总涉及物品种类数 */
        @Schema(description = "总涉及物品种类数")
        private Integer totalAffectedItemCount;

        /** 总已处理次数 */
        @Schema(description = "总已处理次数")
        private Integer totalResolvedCount;

        /** 按预警类型汇总列表 */
        @Schema(description = "按预警类型汇总列表")
        private List<AlertSummaryItem> alertSummaryList;
    }

    /**
     * 预警趋势项
     */
    @Data
    @Schema(description = "预警趋势项")
    public static class AlertTrendItem {

        /** 日期标识 */
        @Schema(description = "日期标识")
        private String date;

        /** 触发次数 */
        @Schema(description = "触发次数")
        private Integer triggerCount;

        /** 涉及物品种类数 */
        @Schema(description = "涉及物品种类数")
        private Integer affectedItemCount;
    }

    /**
     * 预警趋势统计
     */
    @Data
    @Schema(description = "预警趋势统计")
    public static class TrendVo {

        /** 趋势类型 */
        @Schema(description = "趋势类型")
        private String trendType;

        /** 趋势数据列表 */
        @Schema(description = "趋势数据列表")
        private List<AlertTrendItem> trendList;
    }

    /**
     * 预警类型分布项
     */
    @Data
    @Schema(description = "预警类型分布项")
    public static class AlertDistributionItem {

        /** 预警类型 */
        @Schema(description = "预警类型")
        private String alertType;

        /** 预警类型名称 */
        @Schema(description = "预警类型名称")
        private String alertTypeName;

        /** 触发次数 */
        @Schema(description = "触发次数")
        private Integer triggerCount;

        /** 占比(%) */
        @Schema(description = "占比(%)")
        private BigDecimal percentage;
    }

    /**
     * 预警类型分布统计
     */
    @Data
    @Schema(description = "预警类型分布统计")
    public static class DistributionVo {

        /** 分布数据列表 */
        @Schema(description = "分布数据列表")
        private List<AlertDistributionItem> distributionList;
    }
}
