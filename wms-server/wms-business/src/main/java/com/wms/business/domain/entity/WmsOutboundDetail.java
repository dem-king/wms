package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出库明细表实体
 * 对应表 wms_outbound_detail，存储出库单的物品明细信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_outbound_detail")
public class WmsOutboundDetail extends BaseEntity {

    /** 出库单ID */
    @Schema(description = "出库单ID")
    private Long orderId;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;

    /** 库位ID */
    @Schema(description = "库位ID")
    private Long binId;
}
