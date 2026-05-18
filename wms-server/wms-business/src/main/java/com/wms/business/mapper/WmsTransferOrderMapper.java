package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsTransferOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调拨单主表Mapper接口
 */
@Mapper
public interface WmsTransferOrderMapper extends BaseMapper<WmsTransferOrder> {
}
