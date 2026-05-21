package com.wms.approval.converter;

import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.vo.ApprovalConfigVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 审批配置转换器
 * 负责WmsApprovalConfig实体与ApprovalConfigVo之间的转换
 */
@Component
public class ApprovalConfigConverter {

    /**
     * WmsApprovalConfig实体转ApprovalConfigVo
     *
     * @param entity 审批配置实体
     * @return 审批配置VO
     */
    public ApprovalConfigVo toVo(WmsApprovalConfig entity) {
        ApprovalConfigVo vo = new ApprovalConfigVo();
        vo.setId(entity.getId());
        vo.setBizType(entity.getBizType());
        vo.setEnabled(entity.getEnabled());
        vo.setAutoApprove(entity.getAutoApprove());
        vo.setConfigName(entity.getConfigName());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setCreateBy(entity.getCreateBy());
        return vo;
    }

    /**
     * WmsApprovalNode实体转ApprovalNodeVo
     *
     * @param entity 审批节点实体
     * @return 审批节点VO
     */
    public ApprovalConfigVo.ApprovalNodeVo toNodeVo(WmsApprovalNode entity) {
        ApprovalConfigVo.ApprovalNodeVo vo = new ApprovalConfigVo.ApprovalNodeVo();
        vo.setId(entity.getId());
        vo.setConfigId(entity.getConfigId());
        vo.setStepOrder(entity.getStepOrder());
        vo.setNodeName(entity.getNodeName());
        vo.setApproverType(entity.getApproverType());
        vo.setApproverId(entity.getApproverId());
        return vo;
    }

    /**
     * 批量转换审批节点列表
     *
     * @param entities 审批节点实体列表
     * @return 审批节点VO列表
     */
    public List<ApprovalConfigVo.ApprovalNodeVo> toNodeVoList(List<WmsApprovalNode> entities) {
        return entities.stream().map(this::toNodeVo).collect(Collectors.toList());
    }
}
