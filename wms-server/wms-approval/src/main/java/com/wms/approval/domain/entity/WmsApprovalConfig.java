package com.wms.approval.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批配置实体
 * 定义每种业务类型的审批流程配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_approval_config")
@Schema(description = "审批配置")
public class WmsApprovalConfig extends BaseEntity {

    /** 业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还) */
    @Schema(description = "业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还)")
    private Integer bizType;

    /** 是否启用(0-否 1-是) */
    @Schema(description = "是否启用(0-否 1-是)")
    private Integer enabled;

    /** 是否免审(0-否 1-是) */
    @Schema(description = "是否免审(0-否 1-是)")
    private Integer autoApprove;

    /** 配置名称 */
    @Schema(description = "配置名称")
    private String configName;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
