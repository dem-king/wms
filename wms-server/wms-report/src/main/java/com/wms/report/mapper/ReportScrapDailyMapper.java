package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportScrapDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报废日聚合Mapper接口
 */
@Mapper
public interface ReportScrapDailyMapper extends BaseMapper<ReportScrapDaily> {
}
