package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsInboundOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库单主表Mapper接口
 */
@Mapper
public interface WmsInboundOrderMapper extends BaseMapper<WmsInboundOrder> {
}
