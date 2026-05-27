package com.wms.report.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "首页工作台配置VO")
public class DashboardConfigVo {
    private List<String> metrics;
    private List<String> quickActions;
    private String chartTimeRange;
    private List<Integer> layoutRatio;
    private String theme;
}