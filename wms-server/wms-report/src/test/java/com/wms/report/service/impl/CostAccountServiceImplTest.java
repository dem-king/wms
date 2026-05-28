package com.wms.report.service.impl;

import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.CostAccountConfigDto;
import com.wms.report.domain.vo.CostAccountVo;
import com.wms.report.mapper.ReportInboundDailyMapper;
import com.wms.report.mapper.ReportOutboundDailyMapper;
import com.wms.report.mapper.ReportScrapDailyMapper;
import com.wms.report.mapper.ReportTransferDailyMapper;
import com.wms.system.domain.entity.SysConfig;
import com.wms.system.mapper.SysConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CostAccountServiceImpl 单元测试
 * 验证费用核算配置接口返回契约与前端使用保持一致。
 */
@DisplayName("CostAccountServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class CostAccountServiceImplTest {

    @Mock
    private SysConfigMapper sysConfigMapper;

    @Mock
    private ReportInboundDailyMapper inboundMapper;

    @Mock
    private ReportOutboundDailyMapper outboundMapper;

    @Mock
    private ReportScrapDailyMapper scrapMapper;

    @Mock
    private ReportTransferDailyMapper transferMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private CostAccountServiceImpl costAccountService;

    @BeforeEach
    void setUp() {
        costAccountService = new CostAccountServiceImpl(
                sysConfigMapper, inboundMapper, outboundMapper, scrapMapper, transferMapper, redisTemplate);
    }

    @Test
    @DisplayName("读取费用核算配置时应返回 enabled 状态")
    void shouldReturnEnabledWhenGetConfig() {
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(createConfig(ReportConstants.COST_CONFIG_ENABLED_KEY, "true"))
                .thenReturn(createConfig(ReportConstants.COST_CONFIG_YEAR_KEY, "2026"))
                .thenReturn(createConfig(ReportConstants.COST_CONFIG_PERIOD_KEY, ReportConstants.COST_PERIOD_MONTHLY));

        CostAccountVo result = costAccountService.getConfig();

        assertTrue(result.getEnabled());
        assertEquals(2026, result.getYear());
        assertEquals(ReportConstants.COST_PERIOD_MONTHLY, result.getPeriod());
    }

    @Test
    @DisplayName("更新费用核算配置后应返回 enabled 状态")
    void shouldReturnEnabledWhenUpdateConfig() {
        when(sysConfigMapper.selectOne(any()))
                .thenReturn(createConfig(ReportConstants.COST_CONFIG_ENABLED_KEY, "false"))
                .thenReturn(createConfig(ReportConstants.COST_CONFIG_YEAR_KEY, "2025"))
                .thenReturn(createConfig(ReportConstants.COST_CONFIG_PERIOD_KEY, ReportConstants.COST_PERIOD_YEARLY));

        CostAccountConfigDto dto = new CostAccountConfigDto();
        dto.setEnabled(true);
        dto.setYear(2026);
        dto.setPeriod(ReportConstants.COST_PERIOD_QUARTERLY);

        CostAccountVo result = costAccountService.updateConfig(dto);

        assertTrue(result.getEnabled());
        assertEquals(2026, result.getYear());
        assertEquals(ReportConstants.COST_PERIOD_QUARTERLY, result.getPeriod());
        verify(sysConfigMapper, times(3)).updateById(any(SysConfig.class));
        verify(redisTemplate).delete(ReportConstants.COST_CACHE_PREFIX + "*");
    }

    private SysConfig createConfig(String key, String value) {
        SysConfig config = new SysConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        return config;
    }
}
