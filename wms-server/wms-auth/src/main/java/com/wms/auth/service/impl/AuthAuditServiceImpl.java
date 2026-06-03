package com.wms.auth.service.impl;

import com.wms.auth.service.AuthAuditService;
import com.wms.common.util.UserAgentParser;
import com.wms.system.domain.constant.SysLogConstants;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.service.SysLoginLogService;
import com.wms.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 认证审计服务实现
 * 所有写操作均以 @Async 异步落库, 避免阻塞登录主流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAuditServiceImpl implements AuthAuditService {

    /** 审计占位用户名, 用于在登录日志中标记"未通过验证码校验的事件" */
    private static final String CAPTCHA_AUDIT_USERNAME = "__captcha__";

    /** 写入审计日志时 Token 摘要长度, 避免完整 Token 落入日志 */
    private static final int CAPTCHA_TOKEN_LOG_PREFIX = 8;

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
    public void recordCaptchaFailure(String reason, String clientIp, String userAgent,
                                     String captchaToken) {
        try {
            // 解析 UA 摘要, 与普通登录失败日志保持字段一致便于检索
            UserAgentParser.ParsedUserAgent parsed = UserAgentParser.parse(userAgent);
            sysLoginLogService.recordLoginLog(
                    null,
                    CAPTCHA_AUDIT_USERNAME,
                    clientIp,
                    null,
                    parsed.browser(),
                    parsed.os(),
                    SysLogConstants.LOGIN_STATUS_FAIL,
                    "captcha:" + reason + ":token=" + truncate(captchaToken, CAPTCHA_TOKEN_LOG_PREFIX)
            );
        } catch (Exception e) {
            // 审计失败不影响主流程, 仅记录 warn 便于排查
            log.warn("记录验证码失败审计失败: reason={}, ip={}, msg={}", reason, clientIp, e.getMessage());
        }
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

    /**
     * 截断字符串, 避免审计日志字段过长撑爆数据库
     *
     * @param value 原值
     * @param maxLen 最大长度（<= 0 时原样返回）
     * @return 截断后的字符串
     */
    private static String truncate(String value, int maxLen) {
        if (value == null || maxLen <= 0 || value.length() <= maxLen) {
            return value;
        }
        return value.substring(0, maxLen);
    }
}
