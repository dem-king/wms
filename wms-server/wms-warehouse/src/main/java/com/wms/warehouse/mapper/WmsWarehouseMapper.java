package com.wms.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库房Mapper接口
 */
@Mapper
public interface WmsWarehouseMapper extends BaseMapper<WmsWarehouse> {
}
