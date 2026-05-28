package com.wms.report.service.impl;

import com.wms.report.domain.vo.dashboard.DashboardLocationWeatherVo;
import com.wms.report.service.DashboardLocationWeatherService;
import com.wms.report.service.client.AmapDashboardClient;
import com.wms.report.service.client.dto.AmapDashboardGeocodeDto;
import com.wms.report.service.client.dto.AmapDashboardWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 首页位置天气服务实现
 * 基于高德逆地理编码与实时天气接口聚合首页头部数据
 */
@Service
@RequiredArgsConstructor
public class DashboardLocationWeatherServiceImpl implements DashboardLocationWeatherService {

    private final AmapDashboardClient amapDashboardClient;

    /**
     * 根据经纬度查询当前位置天气
     *
     * @param latitude 纬度
     * @param longitude 经度
     * @return 首页位置天气 VO
     */
    @Override
    public DashboardLocationWeatherVo getLocationWeather(Double latitude, Double longitude) {
        AmapDashboardGeocodeDto geocodeDto = amapDashboardClient.reverseGeocode(latitude, longitude);
        AmapDashboardWeatherDto weatherDto = amapDashboardClient.getLiveWeather(geocodeDto.getAdcode());

        DashboardLocationWeatherVo vo = new DashboardLocationWeatherVo();
        vo.setProvince(geocodeDto.getProvince());
        vo.setCity(geocodeDto.getCity());
        vo.setDistrict(geocodeDto.getDistrict());
        vo.setAdcode(geocodeDto.getAdcode());
        vo.setWeather(weatherDto.getWeather());
        vo.setTemperature(weatherDto.getTemperature());
        vo.setWindDirection(weatherDto.getWindDirection());
        vo.setWindPower(weatherDto.getWindPower());
        vo.setHumidity(weatherDto.getHumidity());
        vo.setReportTime(weatherDto.getReportTime());
        return vo;
    }
}
