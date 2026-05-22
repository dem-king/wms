package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportOutboundDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库日聚合Mapper接口
 */
@Mapper
public interface ReportOutboundDailyMapper extends BaseMapper<ReportOutboundDaily> {
}
