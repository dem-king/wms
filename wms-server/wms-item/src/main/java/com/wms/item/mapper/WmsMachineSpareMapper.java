package com.wms.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.item.domain.entity.WmsMachineSpare;
import org.apache.ibatis.annotations.Mapper;

/**
 * 机器-备件关联Mapper
 */
@Mapper
public interface WmsMachineSpareMapper extends BaseMapper<WmsMachineSpare> {
}
