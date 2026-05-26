package com.wms.auth.service.impl;

import com.wms.auth.service.AuthAuditService;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.service.SysLoginLogService;
import com.wms.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAuditServiceImpl implements AuthAuditService {

    private final SysLoginLogService sysLoginLogService;
    private final SysOperLogService sysOperLogService;

    @Async
    @Override
    public void recordLoginLog(Long userId, String username, String loginIp, String browser,
                               String os, String status, String failReason) {
        sysLoginLogService.recordLoginLog(
                userId,
                username,
                loginIp,
                null,
                browser,
                os,
                status,
                failReason
        );
    }

    @Async
    @Override
    public void recordOperLog(SysOperLog operLog) {
        sysOperLogService.asyncSave(operLog);
    }

    @Override
    public SysLoginLog getLatestSuccessLoginLog(Long userId) {
        return sysLoginLogService.getLatestSuccessLoginLog(userId);
    }
}
