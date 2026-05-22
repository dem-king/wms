package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportTransferDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调拨日聚合Mapper接口
 */
@Mapper
public interface ReportTransferDailyMapper extends BaseMapper<ReportTransferDaily> {
}
