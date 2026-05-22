package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportAlertDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警日聚合Mapper接口
 */
@Mapper
public interface ReportAlertDailyMapper extends BaseMapper<ReportAlertDaily> {
}
