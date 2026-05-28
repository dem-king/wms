package com.wms.report.service;

import com.wms.report.domain.vo.dashboard.DashboardLocationWeatherVo;
import com.wms.report.service.client.AmapDashboardClient;
import com.wms.report.service.client.dto.AmapDashboardGeocodeDto;
import com.wms.report.service.client.dto.AmapDashboardWeatherDto;
import com.wms.report.service.impl.DashboardLocationWeatherServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * DashboardLocationWeatherService 单元测试
 * 验证高德逆地理与天气结果能够组装成首页位置天气 VO
 */
@DisplayName("DashboardLocationWeatherService 测试")
@ExtendWith(MockitoExtension.class)
class DashboardLocationWeatherServiceTest {

    @Mock
    private AmapDashboardClient amapDashboardClient;

    @InjectMocks
    private DashboardLocationWeatherServiceImpl dashboardLocationWeatherService;

    @Test
    @DisplayName("位置天气查询应聚合逆地理与天气结果")
    void shouldMapAmapGeocodeAndWeatherToDashboardVo() {
        AmapDashboardGeocodeDto geocodeDto = new AmapDashboardGeocodeDto();
        geocodeDto.setProvince("陕西省");
        geocodeDto.setCity("西安市");
        geocodeDto.setDistrict("雁塔区");
        geocodeDto.setAdcode("610113");

        AmapDashboardWeatherDto weatherDto = new AmapDashboardWeatherDto();
        weatherDto.setWeather("晴");
        weatherDto.setTemperature("31");
        weatherDto.setWindDirection("东北");
        weatherDto.setWindPower("3");
        weatherDto.setHumidity("45");
        weatherDto.setReportTime("2026-05-28 09:30:00");

        when(amapDashboardClient.reverseGeocode(34.2132, 108.8799)).thenReturn(geocodeDto);
        when(amapDashboardClient.getLiveWeather("610113")).thenReturn(weatherDto);

        DashboardLocationWeatherVo result = dashboardLocationWeatherService.getLocationWeather(34.2132, 108.8799);

        assertEquals("陕西省", result.getProvince());
        assertEquals("西安市", result.getCity());
        assertEquals("雁塔区", result.getDistrict());
        assertEquals("610113", result.getAdcode());
        assertEquals("晴", result.getWeather());
        assertEquals("31", result.getTemperature());
        assertEquals("东北", result.getWindDirection());
        assertEquals("3", result.getWindPower());
        assertEquals("45", result.getHumidity());
        assertEquals("2026-05-28 09:30:00", result.getReportTime());
    }
}
