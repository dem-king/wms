package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物品档案Mapper接口
 */
@Mapper
public interface WmsItemMapper extends BaseMapper<WmsItem> {
}
