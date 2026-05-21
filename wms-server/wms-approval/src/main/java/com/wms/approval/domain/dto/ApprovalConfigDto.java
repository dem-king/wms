package com.wms.approval.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 审批配置DTO
 * 用于创建和更新审批配置
 */
@Data
@Schema(description = "审批配置DTO")
public class ApprovalConfigDto {

    /** 业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还) */
    @Schema(description = "业务类型")
    @NotNull(message = "业务类型不能为空")
    private Integer bizType;

    /** 是否启用(0-否 1-是) */
    @Schema(description = "是否启用(0-否 1-是)")
    private Integer enabled;

    /** 是否免审(0-否 1-是) */
    @Schema(description = "是否免审(0-否 1-是)")
    private Integer autoApprove;

    /** 配置名称 */
    @Schema(description = "配置名称")
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 审批节点列表 */
    @Schema(description = "审批节点列表")
    @Valid
    private List<ApprovalNodeDto> nodes;

    /**
     * 审批节点DTO
     */
    @Data
    @Schema(description = "审批节点DTO")
    public static class ApprovalNodeDto {

        /** 节点顺序(从1开始) */
        @Schema(description = "节点顺序(从1开始)")
        @NotNull(message = "节点顺序不能为空")
        private Integer stepOrder;

        /** 节点名称 */
        @Schema(description = "节点名称")
        @NotBlank(message = "节点名称不能为空")
        private String nodeName;

        /** 审批人类型(1-指定角色 2-指定用户 3-库房管理员) */
        @Schema(description = "审批人类型(1-指定角色 2-指定用户 3-库房管理员)")
        @NotNull(message = "审批人类型不能为空")
        private Integer approverType;

        /** 审批人/角色ID */
        @Schema(description = "审批人/角色ID")
        private Long approverId;
    }
}
