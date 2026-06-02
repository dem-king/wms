package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 入库单主表实体
 * 对应表 wms_inbound_order，存储入库单的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inbound_order")
public class WmsInboundOrder extends BaseEntity {

    /** 入库单号 */
    @Schema(description = "入库单号")
    private String orderNo;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 供应商ID */
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 操作人ID */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /** 入库类型(1-采购入库 2-归还入库 3-调拨入库) */
    @Schema(description = "入库类型(1-采购入库 2-归还入库 3-调拨入库)")
    private Integer orderType;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    @TableField("order_status")
    private Integer status;

    /** 总金额 */
    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
