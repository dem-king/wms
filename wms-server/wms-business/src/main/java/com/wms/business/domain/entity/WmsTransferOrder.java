package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调拨单主表实体
 * 对应表 wms_transfer_order，存储调拨单的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_transfer_order")
public class WmsTransferOrder extends BaseEntity {

    /** 调拨单号 */
    @Schema(description = "调拨单号")
    private String orderNo;

    /** 调出库房ID */
    @Schema(description = "调出库房ID")
    private Long fromWarehouseId;

    /** 调入库房ID */
    @Schema(description = "调入库房ID")
    private Long toWarehouseId;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    @TableField("order_status")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
