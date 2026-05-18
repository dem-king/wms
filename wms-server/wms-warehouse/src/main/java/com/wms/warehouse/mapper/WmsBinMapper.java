package com.wms.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.warehouse.domain.entity.WmsBin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库位Mapper接口
 */
@Mapper
public interface WmsBinMapper extends BaseMapper<WmsBin> {
}
