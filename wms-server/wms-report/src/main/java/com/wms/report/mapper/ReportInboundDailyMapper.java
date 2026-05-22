package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportInboundDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库日聚合Mapper接口
 */
@Mapper
public interface ReportInboundDailyMapper extends BaseMapper<ReportInboundDaily> {
}
