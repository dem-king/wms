package com.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.business.domain.entity.WmsStockCheckDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点差异明细Mapper接口
 */
@Mapper
public interface WmsStockCheckDetailMapper extends BaseMapper<WmsStockCheckDetail> {
}