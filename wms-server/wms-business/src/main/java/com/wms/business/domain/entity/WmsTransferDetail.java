package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调拨明细表实体
 * 对应表 wms_transfer_detail，存储调拨单的物品明细信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_transfer_detail")
public class WmsTransferDetail extends BaseEntity {

    /** 调拨单ID */
    @Schema(description = "调拨单ID")
    private Long orderId;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;
}
