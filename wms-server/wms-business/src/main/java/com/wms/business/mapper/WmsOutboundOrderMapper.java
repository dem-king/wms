package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsOutboundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单主表Mapper接口
 */
@Mapper
public interface WmsOutboundOrderMapper extends BaseMapper<WmsOutboundOrder> {
}
