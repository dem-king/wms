package com.wms.report.service;

import com.wms.report.domain.vo.dashboard.DashboardLocationWeatherVo;

/**
 * 首页位置天气服务
 * 根据经纬度聚合行政区与实时天气信息
 */
public interface DashboardLocationWeatherService {

    /**
     * 查询当前位置天气
     *
     * @param latitude 纬度
     * @param longitude 经度
     * @return 首页位置天气 VO
     */
    DashboardLocationWeatherVo getLocationWeather(Double latitude, Double longitude);
}
