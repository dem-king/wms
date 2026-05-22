package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.converter.SysLoginLogConverter;
import com.wms.system.domain.dto.SysLoginLogQueryDto;
import com.wms.system.domain.entity.SysLoginLog;
import com.wms.system.domain.vo.SysLoginLogVo;
import com.wms.system.mapper.SysLoginLogMapper;
import com.wms.system.service.SysLoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录日志服务实现类
 * 处理登录日志的分页查询和异步记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl implements SysLoginLogService {

    private final SysLoginLogMapper sysLoginLogMapper;
    private final SysLoginLogConverter sysLoginLogConverter;

    @Override
    public PageResult<SysLoginLogVo> page(PageParam pageParam, SysLoginLogQueryDto queryDto) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        // 按登录时间范围查询
        if (queryDto.getStartDate() != null) {
            wrapper.ge(SysLoginLog::getLoginTime, queryDto.getStartDate().atStartOfDay());
        }
        if (queryDto.getEndDate() != null) {
            LocalDateTime endDateTime = queryDto.getEndDate().plusDays(1).atStartOfDay();
            wrapper.lt(SysLoginLog::getLoginTime, endDateTime);
        }
        // 按用户名模糊查询
        if (queryDto.getUsername() != null && !queryDto.getUsername().isBlank()) {
            wrapper.like(SysLoginLog::getUsername, queryDto.getUsername());
        }
        // 按登录状态精确查询
        if (queryDto.getStatus() != null && !queryDto.getStatus().isBlank()) {
            wrapper.eq(SysLoginLog::getStatus, queryDto.getStatus());
        }
        wrapper.orderByDesc(SysLoginLog::getLoginTime);

        Page<SysLoginLog> page = sysLoginLogMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<SysLoginLogVo> result = new PageResult<>();
        result.setRecords(sysLoginLogConverter.toVoList(page.getRecords()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    @Override
    @Async
    public void recordLogin(String username, String loginIp, String loginLocation,
                            String browser, String os, String status, String failReason) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setLoginIp(loginIp);
            loginLog.setLoginLocation(loginLocation);
            loginLog.setBrowser(browser);
            loginLog.setOs(os);
            loginLog.setStatus(status);
            loginLog.setFailReason(failReason);
            loginLog.setLoginTime(LocalDateTime.now());
            sysLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            // 异步保存失败不影响登录流程，仅记录警告日志
            log.warn("登录日志异步保存失败: {}", e.getMessage());
        }
    }
}
