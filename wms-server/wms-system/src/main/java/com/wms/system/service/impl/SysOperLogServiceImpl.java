package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.system.converter.SysOperLogConverter;
import com.wms.system.domain.dto.SysOperLogQueryDto;
import com.wms.system.domain.entity.SysOperLog;
import com.wms.system.domain.vo.SysOperLogVo;
import com.wms.system.mapper.SysOperLogMapper;
import com.wms.system.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 操作日志服务实现类
 * 处理操作日志的分页查询和异步保存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;
    private final SysOperLogConverter sysOperLogConverter;

    /**
     * 分页查询操作日志
     * 
     * @param pageParam 分页参数
     * @param module 模块(可选)
     * @param type 操作类型(可选)
     * @param operatorName 操作人(可选)
     * @param status 状态(可选)
     * @return 操作日志分页结果
     */
    @Override
    public PageResult<SysOperLogVo> page(PageParam pageParam, SysOperLogQueryDto queryDto) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        // 按操作时间范围查询
        if (queryDto.getStartDate() != null) {
            wrapper.ge(SysOperLog::getOperTime, queryDto.getStartDate().atStartOfDay());
        }
        if (queryDto.getEndDate() != null) {
            LocalDateTime endDateTime = queryDto.getEndDate().plusDays(1).atStartOfDay();
            wrapper.lt(SysOperLog::getOperTime, endDateTime);
        }
        // 按操作模块精确查询
        if (queryDto.getModule() != null && !queryDto.getModule().isBlank()) {
            wrapper.eq(SysOperLog::getModule, queryDto.getModule());
        }
        // 按操作类型精确查询
        if (queryDto.getType() != null && !queryDto.getType().isBlank()) {
            wrapper.eq(SysOperLog::getType, queryDto.getType());
        }
        // 按操作人模糊查询
        if (queryDto.getOperatorName() != null && !queryDto.getOperatorName().isBlank()) {
            wrapper.like(SysOperLog::getOperatorName, queryDto.getOperatorName());
        }
        wrapper.orderByDesc(SysOperLog::getOperTime);

        Page<SysOperLog> page = sysOperLogMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<SysOperLogVo> result = new PageResult<>();
        result.setRecords(sysOperLogConverter.toVoList(page.getRecords()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 异步保存操作日志
     * 
     * @param entity 操作日志实体
     */
    @Override
    @Async
    public void asyncSave(SysOperLog operLog) {
        try {
            sysOperLogMapper.insert(operLog);
        } catch (Exception e) {
            // 异步保存失败不影响业务，仅记录警告日志
            log.warn("操作日志异步保存失败: {}", e.getMessage());
        }
    }
}
