package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 出库/领用单主表实体
 * 对应表 wms_outbound_order，存储出库单的基础信息和状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_outbound_order")
public class WmsOutboundOrder extends BaseEntity {

    /** 出库单号 */
    @Schema(description = "出库单号")
    private String orderNo;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 出库类型(1-领用出库 2-调拨出库 3-报废出库) */
    @Schema(description = "出库类型(1-领用出库 2-调拨出库 3-报废出库)")
    private Integer orderType;

    /** 状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回) */
    @Schema(description = "状态(0-草稿 1-待审核 2-已审核 3-已完成 4-已驳回)")
    private Integer status;

    /** 领用人 */
    @Schema(description = "领用人")
    private String receiver;

    /** 用途 */
    @Schema(description = "用途")
    private String purpose;

    /** 预计归还日期 */
    @Schema(description = "预计归还日期")
    private LocalDateTime expectedReturnDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
