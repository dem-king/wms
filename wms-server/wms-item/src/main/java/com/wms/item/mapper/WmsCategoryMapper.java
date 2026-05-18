package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 主类目Mapper接口
 */
@Mapper
public interface WmsCategoryMapper extends BaseMapper<WmsCategory> {
}
