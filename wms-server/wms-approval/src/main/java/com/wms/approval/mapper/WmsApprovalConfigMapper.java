package com.wms.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批配置Mapper
 */
@Mapper
public interface WmsApprovalConfigMapper extends BaseMapper<WmsApprovalConfig> {
}
