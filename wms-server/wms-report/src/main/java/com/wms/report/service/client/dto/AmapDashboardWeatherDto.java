package com.wms.report.service.client.dto;

import lombok.Data;

/**
 * 高德实时天气结果 DTO
 * 仅保留首页头部展示需要的实时天气字段
 */
@Data
public class AmapDashboardWeatherDto {

    /** 天气现象 */
    private String weather;

    /** 温度 */
    private String temperature;

    /** 风向 */
    private String windDirection;

    /** 风力 */
    private String windPower;

    /** 湿度 */
    private String humidity;

    /** 发布时间 */
    private String reportTime;
}
