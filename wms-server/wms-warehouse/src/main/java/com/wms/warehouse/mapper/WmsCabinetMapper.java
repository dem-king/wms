package com.wms.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.warehouse.domain.entity.WmsCabinet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 存放柜Mapper接口
 */
@Mapper
public interface WmsCabinetMapper extends BaseMapper<WmsCabinet> {
}
