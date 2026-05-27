package com.wms.report.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "首页工作台聚合数据VO")
public class DashboardDataVo {

    @Schema(description = "指标数据")
    private List<MetricItemVo> metrics;

    @Schema(description = "待办任务")
    private List<TaskItemVo> tasks;

    @Schema(description = "预警提醒")
    private List<AlertItemVo> alerts;

    @Schema(description = "图表数据")
    private ChartDataVo charts;

    @Schema(description = "快捷入口")
    private List<QuickActionItemVo> quickActions;

    @Data
    @Schema(description = "指标项")
    public static class MetricItemVo {
        private String key;
        private String name;
        private BigDecimal value;
        private String unit;
        private BigDecimal changeRate;
        private String trend; // up, down, flat
        private String icon;
        private String color;
    }

    @Data
    @Schema(description = "待办任务项")
    public static class TaskItemVo {
        private String id;
        private String type;
        private String title;
        private String description;
        private String priority; // high, medium, low
        private String createTime;
        private String actionUrl;
    }

    @Data
    @Schema(description = "预警项")
    public static class AlertItemVo {
        private String id;
        private String type;
        private String title;
        private String content;
        private String level; // warning, danger, info
        private String createTime;
    }

    @Data
    @Schema(description = "图表数据")
    public static class ChartDataVo {
        private List<TrendData> trend;
        private List<CompareData> compare;
        private List<DistributionData> distribution;

        @Data
        public static class TrendData {
            private String date;
            private BigDecimal quantity;
            private BigDecimal amount;
        }

        @Data
        public static class CompareData {
            private String date;
            private Integer inbound;
            private Integer outbound;
        }

        @Data
        public static class DistributionData {
            private String name;
            private Integer value;
        }
    }

    @Data
    @Schema(description = "快捷入口")
    public static class QuickActionItemVo {
        private String id;
        private String name;
        private String icon;
        private String url;
        private String color;
    }
}