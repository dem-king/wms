package com.wms.approval.converter;

import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.entity.WmsApprovalRecord;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 审批单转换器
 * 负责WmsApprovalOrder实体与ApprovalOrderVo之间的转换
 */
@Component
public class ApprovalOrderConverter {

    /**
     * WmsApprovalOrder实体转ApprovalOrderVo
     *
     * @param entity 审批单实体
     * @return 审批单VO
     */
    public ApprovalOrderVo toVo(WmsApprovalOrder entity) {
        ApprovalOrderVo vo = new ApprovalOrderVo();
        vo.setId(entity.getId());
        vo.setBizId(entity.getBizId());
        vo.setBizType(entity.getBizType());
        vo.setStatus(entity.getStatus());
        vo.setApplicantId(entity.getApplicantId());
        vo.setCurrentStep(entity.getCurrentStep());
        vo.setTotalSteps(entity.getTotalSteps());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setCreateBy(entity.getCreateBy());
        return vo;
    }

    /**
     * WmsApprovalRecord实体转ApprovalRecordVo
     *
     * @param entity 审批记录实体
     * @return 审批记录VO
     */
    public ApprovalOrderVo.ApprovalRecordVo toRecordVo(WmsApprovalRecord entity) {
        ApprovalOrderVo.ApprovalRecordVo vo = new ApprovalOrderVo.ApprovalRecordVo();
        vo.setId(entity.getId());
        vo.setApprovalId(entity.getApprovalId());
        vo.setStepOrder(entity.getStepOrder());
        vo.setApproverId(entity.getApproverId());
        vo.setApproverName(entity.getApproverName());
        vo.setResult(entity.getResult());
        vo.setOpinion(entity.getOpinion());
        vo.setApproveTime(entity.getApproveTime());
        return vo;
    }

    /**
     * 批量转换审批记录列表
     *
     * @param entities 审批记录实体列表
     * @return 审批记录VO列表
     */
    public List<ApprovalOrderVo.ApprovalRecordVo> toRecordVoList(List<WmsApprovalRecord> entities) {
        return entities.stream().map(this::toRecordVo).collect(Collectors.toList());
    }
}
