package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 归还单主表实体
 * 对应表 wms_return_order，存储归还单的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_return_order")
public class WmsReturnOrder extends BaseEntity {

    /** 归还单号 */
    @Schema(description = "归还单号")
    private String orderNo;

    /** 关联出库单ID */
    @Schema(description = "关联出库单ID")
    private Long outboundOrderId;

    /** 状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成) */
    @Schema(description = "状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)")
    private Integer status;

    /** 归还人 */
    @Schema(description = "归还人")
    private String receiver;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
