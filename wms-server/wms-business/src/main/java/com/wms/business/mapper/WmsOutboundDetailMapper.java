package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsOutboundDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库明细表Mapper接口
 */
@Mapper
public interface WmsOutboundDetailMapper extends BaseMapper<WmsOutboundDetail> {
}
