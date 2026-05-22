package com.wms.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.report.domain.entity.ReportStockDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存日快照Mapper接口
 */
@Mapper
public interface ReportStockDailyMapper extends BaseMapper<ReportStockDaily> {
}
