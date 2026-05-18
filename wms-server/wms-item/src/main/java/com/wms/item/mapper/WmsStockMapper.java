package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsStock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存Mapper接口
 */
@Mapper
public interface WmsStockMapper extends BaseMapper<WmsStock> {
}
