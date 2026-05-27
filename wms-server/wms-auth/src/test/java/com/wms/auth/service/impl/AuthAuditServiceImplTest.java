package com.wms.auth.service.impl;

import com.wms.system.domain.constant.SysLogConstants;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.service.SysLoginLogService;
import com.wms.system.service.SysOperLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AuthAuditServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class AuthAuditServiceImplTest {

    @Mock
    private SysLoginLogService sysLoginLogService;

    @Mock
    private SysOperLogService sysOperLogService;

    @InjectMocks
    private AuthAuditServiceImpl authAuditService;

    @Test
    @DisplayName("recordLoginLog 应委托 SysLoginLogService.recordLoginLog")
    void shouldDelegateRecordLoginLogToSysLoginLogService() {
        authAuditService.recordLoginLog(
                1001L,
                "admin",
                "127.0.0.1",
                "内网IP",
                "Chrome 124",
                "Windows 10",
                SysLogConstants.LOGIN_STATUS_SUCCESS,
                null
        );

        verify(sysLoginLogService).recordLoginLog(
                1001L,
                "admin",
                "127.0.0.1",
                "内网IP",
                "Chrome 124",
                "Windows 10",
                SysLogConstants.LOGIN_STATUS_SUCCESS,
                null
        );
    }

    @Test
    @DisplayName("recordOperLog 应委托 SysOperLogService.asyncSave")
    void shouldDelegateRecordOperLogToSysOperLogService() {
        SysOperLog operLog = new SysOperLog();
        operLog.setOperatorId(1001L);
        operLog.setOperatorName("admin");
        operLog.setType("登出");

        authAuditService.recordOperLog(operLog);

        verify(sysOperLogService).asyncSave(operLog);
    }

    @Test
    @DisplayName("getLatestSuccessLoginLog 应委托 SysLoginLogService.getLatestSuccessLoginLog")
    void shouldDelegateGetLatestSuccessLoginLogToSysLoginLogService() {
        SysLoginLog latestLoginLog = new SysLoginLog();
        latestLoginLog.setUserId(1002L);
        latestLoginLog.setUsername("zhangsan");
        when(sysLoginLogService.getLatestSuccessLoginLog(1002L)).thenReturn(latestLoginLog);

        SysLoginLog result = authAuditService.getLatestSuccessLoginLog(1002L);

        assertSame(latestLoginLog, result);
        verify(sysLoginLogService).getLatestSuccessLoginLog(1002L);
    }
}
