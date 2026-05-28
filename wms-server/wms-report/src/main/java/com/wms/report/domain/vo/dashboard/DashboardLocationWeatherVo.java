package com.wms.report.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 首页位置天气 VO
 * 统一承载首页头部展示所需的行政区与实时天气信息
 */
@Data
@Schema(description = "首页位置天气VO")
public class DashboardLocationWeatherVo {

    /** 省份名称 */
    @Schema(description = "省份名称")
    private String province;

    /** 城市名称 */
    @Schema(description = "城市名称")
    private String city;

    /** 区县名称 */
    @Schema(description = "区县名称")
    private String district;

    /** 行政区编码 */
    @Schema(description = "行政区编码")
    private String adcode;

    /** 天气现象 */
    @Schema(description = "天气现象")
    private String weather;

    /** 温度 */
    @Schema(description = "温度")
    private String temperature;

    /** 风向 */
    @Schema(description = "风向")
    private String windDirection;

    /** 风力 */
    @Schema(description = "风力")
    private String windPower;

    /** 湿度 */
    @Schema(description = "湿度")
    private String humidity;

    /** 发布时间 */
    @Schema(description = "发布时间")
    private String reportTime;
}
