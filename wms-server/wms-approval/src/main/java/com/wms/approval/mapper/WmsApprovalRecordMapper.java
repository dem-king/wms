package com.wms.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.approval.domain.entity.WmsApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批记录Mapper
 */
@Mapper
public interface WmsApprovalRecordMapper extends BaseMapper<WmsApprovalRecord> {
}
