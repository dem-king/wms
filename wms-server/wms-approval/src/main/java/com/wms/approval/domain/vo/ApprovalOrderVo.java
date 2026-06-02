package com.wms.approval.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批单VO
 * 返回审批单详情，包含审批记录列表
 */
@Data
@Schema(description = "审批单VO")
public class ApprovalOrderVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 审批单号 */
    @Schema(description = "审批单号")
    private String approvalNo;

    /** 业务单据ID */
    @Schema(description = "业务单据ID")
    private Long bizId;

    /** 业务单号 */
    @Schema(description = "业务单号")
    private String bizNo;

    /** 业务类型 */
    @Schema(description = "业务类型")
    private Integer bizType;

    /** 审批配置ID */
    @Schema(description = "审批配置ID")
    private Long configId;

    /** 审批状态 */
    @Schema(description = "审批状态")
    private Integer status;

    /** 申请人ID */
    @Schema(description = "申请人ID")
    private Long applicantId;

    /** 当前审批节点 */
    @Schema(description = "当前审批节点")
    private Integer currentStep;

    /** 当前审批节点名称 */
    @Schema(description = "当前审批节点名称")
    private String currentNodeName;

    /** 总审批节点数 */
    @Schema(description = "总审批节点数")
    private Integer totalSteps;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 申请人姓名 */
    @Schema(description = "申请人姓名")
    private String applicantName;

    /** 审批记录列表 */
    @Schema(description = "审批记录列表")
    private List<ApprovalRecordVo> records;

    /**
     * 审批记录VO
     */
    @Data
    @Schema(description = "审批记录VO")
    public static class ApprovalRecordVo {

        /** 主键 */
        @Schema(description = "主键")
        private Long id;

        /** 审批单ID */
        @Schema(description = "审批单ID")
        private Long approvalId;

        /** 节点顺序 */
        @Schema(description = "节点顺序")
        private Integer stepOrder;

        /** 节点名称 */
        @Schema(description = "节点名称")
        private String nodeName;

        /** 审批人ID */
        @Schema(description = "审批人ID")
        private Long approverId;

        /** 审批人姓名 */
        @Schema(description = "审批人姓名")
        private String approverName;

        /** 审批结果 */
        @Schema(description = "审批结果")
        private Integer result;

        /** 审批意见 */
        @Schema(description = "审批意见")
        private String opinion;

        /** 审批时间 */
        @Schema(description = "审批时间")
        private LocalDateTime approveTime;
    }
}
