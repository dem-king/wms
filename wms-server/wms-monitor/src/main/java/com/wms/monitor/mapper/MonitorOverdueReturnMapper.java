package com.wms.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.monitor.domain.entity.MonitorOverdueReturn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 逾期归还Mapper接口
 */
@Mapper
public interface MonitorOverdueReturnMapper extends BaseMapper<MonitorOverdueReturn> {
}
