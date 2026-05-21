package com.wms.approval.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批节点配置实体
 * 定义审批流程中每个节点的审批人配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_approval_node")
@Schema(description = "审批节点配置")
public class WmsApprovalNode extends BaseEntity {

    /** 审批配置ID */
    @Schema(description = "审批配置ID")
    private Long configId;

    /** 节点顺序(从1开始) */
    @Schema(description = "节点顺序(从1开始)")
    private Integer stepOrder;

    /** 节点名称 */
    @Schema(description = "节点名称")
    private String nodeName;

    /** 审批人类型(1-指定角色 2-指定用户 3-库房管理员) */
    @Schema(description = "审批人类型(1-指定角色 2-指定用户 3-库房管理员)")
    private Integer approverType;

    /** 审批人/角色ID */
    @Schema(description = "审批人/角色ID")
    private Long approverId;
}
