package com.wms.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.monitor.converter.MonitorConverter;
import com.wms.monitor.domain.entity.MonitorOverdueReturn;
import com.wms.monitor.domain.vo.OverdueReturnVo;
import com.wms.monitor.mapper.MonitorOverdueReturnMapper;
import com.wms.monitor.service.OverdueReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 逾期归还服务实现类
 * 处理逾期归还记录的分页查询操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverdueReturnServiceImpl implements OverdueReturnService {

    private final MonitorOverdueReturnMapper monitorOverdueReturnMapper;
    private final MonitorConverter monitorConverter;

    /**
     * 分页查询逾期归还记录
     * 
     * @param pageParam 分页参数
     * @param alertLevel 预警级别(可选)
     * @param status 处理状态(可选)
     * @return 逾期归还分页结果
     */
    @Override
    public PageResult<OverdueReturnVo> page(PageParam pageParam, String alertLevel, String status) {
        Page<MonitorOverdueReturn> page = new Page<>(pageParam.getPage(), pageParam.getSize());
        LambdaQueryWrapper<MonitorOverdueReturn> wrapper = new LambdaQueryWrapper<>();
        if (alertLevel != null) {
            wrapper.eq(MonitorOverdueReturn::getAlertLevel, alertLevel);
        }
        if (status != null) {
            wrapper.eq(MonitorOverdueReturn::getStatus, status);
        }
        wrapper.orderByDesc(MonitorOverdueReturn::getOverdueDays);
        Page<MonitorOverdueReturn> result = monitorOverdueReturnMapper.selectPage(page, wrapper);

        List<OverdueReturnVo> voList = result.getRecords().stream()
                .map(monitorConverter::toOverdueReturnVo)
                .collect(Collectors.toList());

        PageResult<OverdueReturnVo> pageResult = new PageResult<>();
        pageResult.setRecords(voList);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(pageParam.getPage());
        pageResult.setSize(pageParam.getSize());
        return pageResult;
    }
}
