package com.wms.report.service;

import com.wms.report.domain.dto.dashboard.DashboardConfigDto;
import com.wms.report.domain.vo.dashboard.DashboardConfigVo;
import com.wms.report.domain.vo.dashboard.DashboardDataVo;
import com.wms.report.domain.vo.dashboard.DashboardLocationWeatherVo;

public interface DashboardService {
    
    /**
     * 获取首页聚合数据
     * @param role 角色视图 (storekeeper, admin, leader)
     * @return 首页数据
     */
    DashboardDataVo getDashboardData(String role);

    /**
     * 获取首页位置天气
     *
     * @param latitude 纬度
     * @param longitude 经度
     * @return 位置天气 VO
     */
    DashboardLocationWeatherVo getLocationWeather(Double latitude, Double longitude);

    /**
     * 获取用户工作台配置
     * @return 配置VO
     */
    DashboardConfigVo getConfig();

    /**
     * 保存用户工作台配置
     * @param dto 配置DTO
     */
    void saveConfig(DashboardConfigDto dto);
}
