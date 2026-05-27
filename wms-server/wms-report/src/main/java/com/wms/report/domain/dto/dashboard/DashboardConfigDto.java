package com.wms.report.domain.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "首页工作台配置保存DTO")
public class DashboardConfigDto {
    private List<String> metrics;
    private List<String> quickActions;
    private String chartTimeRange;
    private List<Integer> layoutRatio;
    private String theme;
}