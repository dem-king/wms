package com.wms.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.monitor.domain.entity.MonitorStockAlert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存预警Mapper接口
 * 供预警扫描任务和查询服务使用
 */
@Mapper
public interface MonitorStockAlertMapper extends BaseMapper<MonitorStockAlert> {
}
