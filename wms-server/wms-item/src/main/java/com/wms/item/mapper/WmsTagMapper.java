package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 自定义标签Mapper接口
 */
@Mapper
public interface WmsTagMapper extends BaseMapper<WmsTag> {
}
