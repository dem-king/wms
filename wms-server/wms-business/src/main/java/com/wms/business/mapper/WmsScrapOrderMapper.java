package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsScrapOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报废单主表Mapper接口
 */
@Mapper
public interface WmsScrapOrderMapper extends BaseMapper<WmsScrapOrder> {
}
