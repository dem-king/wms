package com.wms.approval.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.approval.domain.entity.WmsApprovalNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批节点配置Mapper
 */
@Mapper
public interface WmsApprovalNodeMapper extends BaseMapper<WmsApprovalNode> {
}
