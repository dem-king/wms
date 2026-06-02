package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报废明细表实体
 * 对应表 wms_scrap_detail，存储报废单的物品明细信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_scrap_detail")
public class WmsScrapDetail extends BaseEntity {

    /** 报废单ID */
    @Schema(description = "报废单ID")
    private Long orderId;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 报废库位ID */
    @Schema(description = "报废库位ID")
    private Long binId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;
}
