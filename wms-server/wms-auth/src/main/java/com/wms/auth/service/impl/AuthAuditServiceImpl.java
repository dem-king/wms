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

/**
 * 认证审计服务实现类
 * 异步记录登录日志和操作日志，查询最近一次成功登录记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAuditServiceImpl implements AuthAuditService {

    private final SysLoginLogService sysLoginLogService;
    private final SysOperLogService sysOperLogService;

    @Async
    /**
     * 异步记录登录日志
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param loginIp 登录IP
     * @param browser 浏览器
     * @param os 操作系统
     * @param status 登录状态
     * @param failReason 失败原因
     */
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
    /**
     * 异步记录操作日志
     * 
     * @param operLog 操作日志实体
     */
    @Override
    public void recordOperLog(SysOperLog operLog) {
        sysOperLogService.asyncSave(operLog);
    }

    /**
     * 查询用户最近一次成功登录记录
     * 
     * @param userId 用户ID
     * @return 登录日志实体
     */
    @Override
    public SysLoginLog getLatestSuccessLoginLog(Long userId) {
        return sysLoginLogService.getLatestSuccessLoginLog(userId);
    }
}
