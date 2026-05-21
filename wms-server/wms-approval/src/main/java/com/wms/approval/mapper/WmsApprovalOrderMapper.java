package com.wms.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批单Mapper
 */
@Mapper
public interface WmsApprovalOrderMapper extends BaseMapper<WmsApprovalOrder> {
}
