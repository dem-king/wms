package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsStockCheckOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点单Mapper接口
 */
@Mapper
public interface WmsStockCheckOrderMapper extends BaseMapper<WmsStockCheckOrder> {
}