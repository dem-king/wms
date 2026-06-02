package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 盘点差异明细实体
 * 对应表 wms_stock_check_detail，存储盘点差异明细(盘盈/盘亏)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_stock_check_detail")
public class WmsStockCheckDetail extends BaseEntity {

    /** 盘点单ID */
    @Schema(description = "盘点单ID")
    private Long checkOrderId;

    /** 标签编号 */
    @Schema(description = "标签编号")
    private String labelNo;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 差异类型(surplus-盘盈 deficit-盘亏) */
    @Schema(description = "差异类型(surplus-盘盈 deficit-盘亏)")
    private String diffType;

    /** 系统数量 */
    @Schema(description = "系统数量")
    private Integer systemQty;

    /** 实际数量 */
    @Schema(description = "实际数量")
    private Integer actualQty;

    /** 库位ID */
    @Schema(description = "库位ID")
    private Long binId;
}