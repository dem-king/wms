package com.wms.auth.service.impl;

import com.wms.system.domain.constant.SysLogConstants;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.service.SysLoginLogService;
import com.wms.system.service.SysOperLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                "Chrome 124",
                "Windows 10",
                SysLogConstants.LOGIN_STATUS_SUCCESS,
                null
        );

        verify(sysLoginLogService).recordLoginLog(
                1001L,
                "admin",
                "127.0.0.1",
                null,
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

    @Test
    @DisplayName("recordCaptchaFailure 应写入 username=__captcha__ 的 FAIL 日志, 原因含 reason 与 token 摘要")
    void shouldRecordCaptchaFailureAsFailedLoginLog() {
        authAuditService.recordCaptchaFailure("CAPTCHA_MISMATCH", "1.2.3.4",
                "Mozilla/5.0 (Windows NT 10.0)", "tok-1234567890");

        ArgumentCaptor<String> failReasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(sysLoginLogService).recordLoginLog(
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq("__captcha__"),
                org.mockito.ArgumentMatchers.eq("1.2.3.4"),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.eq(SysLogConstants.LOGIN_STATUS_FAIL),
                failReasonCaptor.capture()
        );
        String reason = failReasonCaptor.getValue();
        assertTrue(reason.contains("CAPTCHA_MISMATCH"),
                "fail reason should contain captcha reason code, was: " + reason);
        assertTrue(reason.contains("tok-1234"),
                "fail reason should contain truncated captcha token (first 8 chars), was: " + reason);
    }

    @Test
    @DisplayName("recordCaptchaFailure 当下游 mapper 抛异常时不应向上抛, 仅记录 warn")
    void shouldSwallowExceptionWhenAuditFailed() {
        org.mockito.Mockito.doThrow(new RuntimeException("db down"))
                .when(sysLoginLogService).recordLoginLog(
                        org.mockito.ArgumentMatchers.isNull(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.isNull(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString());

        // 不应抛异常
        authAuditService.recordCaptchaFailure("CAPTCHA_REQUIRED", "127.0.0.1", "ua", "tok");
    }
}
