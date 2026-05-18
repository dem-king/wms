package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsItemTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物品-标签关联Mapper接口
 */
@Mapper
public interface WmsItemTagMapper extends BaseMapper<WmsItemTag> {
}
