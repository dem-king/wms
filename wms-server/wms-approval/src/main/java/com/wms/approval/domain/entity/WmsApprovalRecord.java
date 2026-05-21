package com.wms.approval.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审批记录实体
 * 记录每次审批操作的详细信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_approval_record")
@Schema(description = "审批记录")
public class WmsApprovalRecord extends BaseEntity {

    /** 审批单ID */
    @Schema(description = "审批单ID")
    private Long approvalId;

    /** 节点顺序 */
    @Schema(description = "节点顺序")
    private Integer stepOrder;

    /** 审批人ID */
    @Schema(description = "审批人ID")
    private Long approverId;

    /** 审批人姓名 */
    @Schema(description = "审批人姓名")
    private String approverName;

    /** 审批结果(1-通过 2-驳回) */
    @Schema(description = "审批结果(1-通过 2-驳回)")
    private Integer result;

    /** 审批意见 */
    @Schema(description = "审批意见")
    private String opinion;

    /** 审批时间 */
    @Schema(description = "审批时间")
    private LocalDateTime approveTime;
}
