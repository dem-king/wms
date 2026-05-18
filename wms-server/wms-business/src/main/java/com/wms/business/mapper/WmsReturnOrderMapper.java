package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsReturnOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 归还单主表Mapper接口
 */
@Mapper
public interface WmsReturnOrderMapper extends BaseMapper<WmsReturnOrder> {
}
