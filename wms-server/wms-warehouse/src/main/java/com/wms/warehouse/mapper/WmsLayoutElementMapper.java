package com.wms.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.warehouse.domain.entity.WmsLayoutElement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库房布局元素Mapper接口
 */
@Mapper
public interface WmsLayoutElementMapper extends BaseMapper<WmsLayoutElement> {
}