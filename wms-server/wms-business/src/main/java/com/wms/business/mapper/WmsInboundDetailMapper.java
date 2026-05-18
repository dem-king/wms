package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsInboundDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库明细表Mapper接口
 */
@Mapper
public interface WmsInboundDetailMapper extends BaseMapper<WmsInboundDetail> {
}
