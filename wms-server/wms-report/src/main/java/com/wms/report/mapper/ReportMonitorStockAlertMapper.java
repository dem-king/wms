package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.MonitorStockAlert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存预警Mapper接口(P4-38基础版本)
 * 供预警日聚合任务使用，完整版本将在wms-monitor模块中实现
 * 重命名为ReportMonitorStockAlertMapper，避免与wms-monitor模块同名Bean冲突
 */
@Mapper
public interface ReportMonitorStockAlertMapper extends BaseMapper<MonitorStockAlert> {
}
