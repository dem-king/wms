package com.wms.auth.service;

import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;

public interface AuthAuditService {

    void recordLoginLog(Long userId, String username, String loginIp, String browser,
                        String os, String status, String failReason);

    /**
     * 记录验证码校验失败事件
     * 写入 username=__captcha__ 的登录失败日志, 用于安全审计与高频失败告警
     *
     * @param reason       失败原因码（如 CAPTCHA_MISMATCH / CAPTCHA_INVALID / CAPTCHA_REQUIRED / CAPTCHA_FREQUENT）
     * @param clientIp     客户端 IP
     * @param userAgent    User-Agent 头
     * @param captchaToken 验证码 Token（仅取前 8 位写入日志，避免敏感信息过长）
     */
    void recordCaptchaFailure(String reason, String clientIp, String userAgent, String captchaToken);

    void recordOperLog(SysOperLog log);

    SysLoginLog getLatestSuccessLoginLog(Long userId);
}
