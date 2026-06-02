package com.wms.approval.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批配置VO
 * 返回审批配置详情，包含审批节点列表
 */
@Data
@Schema(description = "审批配置VO")
public class ApprovalConfigVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 业务类型 */
    @Schema(description = "业务类型")
    private Integer bizType;

    /** 是否启用 */
    @Schema(description = "是否启用")
    private Integer enabled;

    /** 是否免审 */
    @Schema(description = "是否免审")
    private Integer autoApprove;

    /** 配置名称 */
    @Schema(description = "配置名称")
    private String configName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 审批超时阈值(小时) */
    @Schema(description = "审批超时阈值(小时)")
    private Integer timeoutHours;

    /** 超时处理方式 */
    @Schema(description = "超时处理方式")
    private Integer timeoutAction;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 审批节点列表 */
    @Schema(description = "审批节点列表")
    private List<ApprovalNodeVo> nodes;

    /**
     * 审批节点VO
     */
    @Data
    @Schema(description = "审批节点VO")
    public static class ApprovalNodeVo {

        /** 主键 */
        @Schema(description = "主键")
        private Long id;

        /** 审批配置ID */
        @Schema(description = "审批配置ID")
        private Long configId;

        /** 节点顺序 */
        @Schema(description = "节点顺序")
        private Integer stepOrder;

        /** 节点名称 */
        @Schema(description = "节点名称")
        private String nodeName;

        /** 审批人类型 */
        @Schema(description = "审批人类型")
        private Integer approverType;

        /** 审批人/角色ID */
        @Schema(description = "审批人/角色ID")
        private Long approverId;

        /** 审批用户名 */
        @Schema(description = "审批用户名")
        private String approverUsername;

        /** 审批人名称 */
        @Schema(description = "审批人名称")
        private String approverName;

        /** 审批角色名称 */
        @Schema(description = "审批角色名称")
        private String approverRoleName;
    }
}
