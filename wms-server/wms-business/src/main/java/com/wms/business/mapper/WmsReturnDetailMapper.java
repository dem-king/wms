package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsReturnDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 归还明细表Mapper接口
 */
@Mapper
public interface WmsReturnDetailMapper extends BaseMapper<WmsReturnDetail> {
}
