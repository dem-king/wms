package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsScrapDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报废明细表Mapper接口
 */
@Mapper
public interface WmsScrapDetailMapper extends BaseMapper<WmsScrapDetail> {
}
