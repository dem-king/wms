package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsSubCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 细分类目Mapper接口
 */
@Mapper
public interface WmsSubCategoryMapper extends BaseMapper<WmsSubCategory> {
}
