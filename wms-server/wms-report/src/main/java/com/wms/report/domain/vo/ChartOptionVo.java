package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 图表选项视图对象
 * 返回适配ECharts option规范的数据
 */
@Data
@Schema(description = "图表选项数据")
public class ChartOptionVo {

    /** 报表类型 */
    @Schema(description = "报表类型")
    private String reportType;

    /** 图表类型 */
    @Schema(description = "图表类型")
    private String chartType;

    /** ECharts option配置对象 */
    @Schema(description = "ECharts option配置")
    private Map<String, Object> option;
}
