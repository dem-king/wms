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

    /** 电子标签ID */
    @Schema(description = "电子标签ID")
    private Long labelId;

    /** 归还库位ID */
    @Schema(description = "归还库位ID")
    private Long binId;

    /** 数量 */
    @Schema(description = "数量")
    private Integer quantity;

    /** 物品状态(1-正常 2-损坏 3-丢失 4-数量不符) */
    @Schema(description = "物品状态(1-正常 2-损坏 3-丢失 4-数量不符)")
    private Integer conditionStatus;

    /** 异常说明(损坏/丢失/数量不符等异常归还时的备注) */
    @Schema(description = "异常说明")
    private String abnormalRemark;

    /** 实际归还数量(数量不符时记录实际归还数量) */
    @Schema(description = "实际归还数量")
    private Integer actualQuantity;
}
