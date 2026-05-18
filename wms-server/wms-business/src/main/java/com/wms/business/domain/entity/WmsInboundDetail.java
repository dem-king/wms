package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 入库明细表实体
 * 对应表 wms_inbound_detail，存储入库单的物品明细信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inbound_detail")
public class WmsInboundDetail extends BaseEntity {

    /** 入库单ID */
    @Schema(description = "入库单ID")
    private Long orderId;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;

    /** 单价 */
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    /** 金额 */
    @Schema(description = "金额")
    private BigDecimal amount;

    /** 入库库位ID */
    @Schema(description = "入库库位ID")
    private Long binId;
}
