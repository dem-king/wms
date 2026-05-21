package com.wms.approval.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 审批操作DTO
 * 用于审批通过/驳回时提交审批意见
 */
@Data
@Schema(description = "审批操作DTO")
public class ApprovalActionDto {

    /** 审批意见 */
    @Schema(description = "审批意见")
    private String opinion;
}
