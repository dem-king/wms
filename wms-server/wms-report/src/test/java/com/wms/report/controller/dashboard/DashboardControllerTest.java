package com.wms.report.controller.dashboard;

import com.wms.common.domain.R;
import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.report.domain.dto.dashboard.DashboardConfigDto;
import com.wms.report.domain.vo.dashboard.DashboardLocationWeatherVo;
import com.wms.report.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DashboardController 单元测试
 * 验证位置天气接口仅负责参数透传和返回包装
 */
@DisplayName("DashboardController 测试")
@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    @DisplayName("查询位置天气时应委托服务并返回统一响应")
    void shouldDelegateToServiceWhenGetLocationWeather() {
        DashboardLocationWeatherVo weatherVo = new DashboardLocationWeatherVo();
        weatherVo.setProvince("陕西省");
        weatherVo.setCity("西安市");
        weatherVo.setDistrict("雁塔区");
        weatherVo.setWeather("晴");
        when(dashboardService.getLocationWeather(34.2132, 108.8799)).thenReturn(weatherVo);

        R<DashboardLocationWeatherVo> result = dashboardController.getLocationWeather(34.2132, 108.8799);

        assertEquals(200, result.getCode());
        assertEquals("西安市", result.getData().getCity());
        assertEquals("晴", result.getData().getWeather());
        verify(dashboardService).getLocationWeather(34.2132, 108.8799);
    }

    @Test
    @DisplayName("获取首页配置接口应声明数据权限注解")
    void shouldDeclareDataScopeOnGetConfig() throws NoSuchMethodException {
        DataScope dataScope = DashboardController.class
                .getMethod("getConfig")
                .getAnnotation(DataScope.class);

        assertNotNull(dataScope);
    }

    @Test
    @DisplayName("保存首页配置接口应声明操作日志注解")
    void shouldDeclareOperLogOnSaveConfig() throws NoSuchMethodException {
        OperLog operLog = DashboardController.class
                .getMethod("saveConfig", DashboardConfigDto.class)
                .getAnnotation(OperLog.class);

        assertNotNull(operLog);
        assertEquals("report", operLog.module());
    }
}
