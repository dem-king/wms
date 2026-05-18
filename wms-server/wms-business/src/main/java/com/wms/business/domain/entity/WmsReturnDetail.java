package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 归还明细表实体
 * 对应表 wms_return_detail，存储归还单的物品明细信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_return_detail")
public class WmsReturnDetail extends BaseEntity {

    /** 归还单ID */
    @Schema(description = "归还单ID")
    private Long orderId;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;

    /** 物品状态(1-正常 2-损坏) */
    @Schema(description = "物品状态(1-正常 2-损坏)")
    private Integer conditionStatus;
}
