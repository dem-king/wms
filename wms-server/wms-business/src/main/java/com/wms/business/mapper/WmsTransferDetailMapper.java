package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsTransferDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调拨明细表Mapper接口
 */
@Mapper
public interface WmsTransferDetailMapper extends BaseMapper<WmsTransferDetail> {
}
