package com.wms.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.monitor.converter.MonitorConverter;
import com.wms.monitor.domain.constant.MonitorConstants;
import com.wms.monitor.domain.entity.MonitorStockAlert;
import com.wms.monitor.domain.vo.StockAlertVo;
import com.wms.monitor.mapper.MonitorStockAlertMapper;
import com.wms.monitor.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存预警服务实现类
 * 处理预警记录的分页查询和状态更新操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockAlertServiceImpl implements StockAlertService {

    private final MonitorStockAlertMapper monitorStockAlertMapper;
    private final MonitorConverter monitorConverter;

    /**
     * 构建预警查询条件
     *
     * @param alertType   预警类型
     * @param status      处理状态
     * @param warehouseId 库房ID
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<MonitorStockAlert> buildWrapper(String alertType, String status, Long warehouseId) {
        LambdaQueryWrapper<MonitorStockAlert> wrapper = new LambdaQueryWrapper<>();
        if (alertType != null) {
            wrapper.eq(MonitorStockAlert::getAlertType, alertType);
        }
        if (status != null) {
            wrapper.eq(MonitorStockAlert::getStatus, status);
        }
        if (warehouseId != null) {
            wrapper.eq(MonitorStockAlert::getWarehouseId, warehouseId);
        }
        wrapper.orderByDesc(MonitorStockAlert::getTriggerTime);
        return wrapper;
    }

    /**
     * 分页查询库存预警记录
     * 
     * @param pageParam 分页参数
     * @param alertType 预警类型(可选)
     * @param status 处理状态(可选)
     * @param warehouseId 库房ID(可选)
     * @return 预警分页结果
     */
    @Override
    public PageResult<StockAlertVo> page(PageParam pageParam, String alertType, String status, Long warehouseId) {
        Page<MonitorStockAlert> page = new Page<>(pageParam.getPage(), pageParam.getSize());
        LambdaQueryWrapper<MonitorStockAlert> wrapper = buildWrapper(alertType, status, warehouseId);
        Page<MonitorStockAlert> result = monitorStockAlertMapper.selectPage(page, wrapper);

        List<StockAlertVo> voList = result.getRecords().stream()
                .map(monitorConverter::toStockAlertVo)
                .collect(Collectors.toList());

        PageResult<StockAlertVo> pageResult = new PageResult<>();
        pageResult.setRecords(voList);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(pageParam.getPage());
        pageResult.setSize(pageParam.getSize());
        return pageResult;
    }

    /**
     * 处理预警记录
     * 更新状态为已处理
     * 
     * @param id 预警记录ID
     * @return 更新后的预警VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockAlertVo resolve(Long id) {
        MonitorStockAlert alert = monitorStockAlertMapper.selectById(id);
        if (alert == null) {
            throw new BizException("预警记录不存在");
        }
        if (MonitorConstants.ALERT_STATUS_RESOLVED.equals(alert.getStatus())) {
            throw new BizException("预警记录已处理");
        }
        // 更新状态为已处理
        alert.setStatus(MonitorConstants.ALERT_STATUS_RESOLVED);
        monitorStockAlertMapper.updateById(alert);
        return monitorConverter.toStockAlertVo(alert);
    }
}
