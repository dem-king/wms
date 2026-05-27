package com.wms.auth.service;

import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;

public interface AuthAuditService {

    void recordLoginLog(Long userId, String username, String loginIp, String loginLocation, String browser,
                        String os, String status, String failReason);

    void recordOperLog(SysOperLog log);

    SysLoginLog getLatestSuccessLoginLog(Long userId);
}
