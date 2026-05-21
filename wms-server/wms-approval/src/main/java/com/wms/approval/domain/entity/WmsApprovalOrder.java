package com.wms.approval.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批单实体
 * 记录业务单据发起的审批流程实例
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_approval_order")
@Schema(description = "审批单")
public class WmsApprovalOrder extends BaseEntity {

    /** 业务单据ID */
    @Schema(description = "业务单据ID")
    private Long bizId;

    /** 业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还) */
    @Schema(description = "业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还)")
    private Integer bizType;

    /** 审批状态(0-待审批 1-审批中 2-已通过 3-已驳回 4-已撤回) */
    @Schema(description = "审批状态(0-待审批 1-审批中 2-已通过 3-已驳回 4-已撤回)")
    private Integer status;

    /** 申请人ID */
    @Schema(description = "申请人ID")
    private Long applicantId;

    /** 当前审批节点(从1开始) */
    @Schema(description = "当前审批节点(从1开始)")
    private Integer currentStep;

    /** 总审批节点数 */
    @Schema(description = "总审批节点数")
    private Integer totalSteps;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
