package com.wms.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.auth.domain.entity.AuthLoginLog;
import com.wms.auth.domain.entity.AuthOperLog;
import com.wms.auth.enums.LoginResultEnum;
import com.wms.auth.mapper.AuthLoginLogMapper;
import com.wms.auth.mapper.AuthOperLogMapper;
import com.wms.auth.service.AuthAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAuditServiceImpl implements AuthAuditService {

    private final AuthLoginLogMapper authLoginLogMapper;
    private final AuthOperLogMapper authOperLogMapper;

    @Async
    @Override
    public void recordLoginLog(AuthLoginLog loginLog) {
        try {
            authLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败: {}", e.getMessage());
        }
    }

    @Async
    @Override
    public void recordOperLog(AuthOperLog operLog) {
        try {
            authOperLogMapper.insert(operLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public AuthLoginLog getLatestSuccessLoginLog(Long userId) {
        return authLoginLogMapper.selectOne(
                new LambdaQueryWrapper<AuthLoginLog>()
                        .eq(AuthLoginLog::getUserId, userId)
                        .eq(AuthLoginLog::getLoginResult, LoginResultEnum.SUCCESS.getCode())
                        .orderByDesc(AuthLoginLog::getLoginTime)
                        .last("LIMIT 1")
        );
    }
}
