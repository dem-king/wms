package com.wms.business.domain.vo.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 盘点差异明细VO
 * 单条盘盈或盘亏记录的详情
 */
@Data
@Schema(description = "盘点差异明细")
public class StockCheckDiffDetailVo {

    /** 标签编号 */
    @Schema(description = "标签编号")
    private String labelNo;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 物品编码 */
    @Schema(description = "物品编码")
    private String itemCode;

    /** 差异类型(surplus-盘盈 deficit-盘亏) */
    @Schema(description = "差异类型(surplus-盘盈 deficit-盘亏)")
    private String diffType;

    /** 系统数量 */
    @Schema(description = "系统数量")
    private Integer systemQty;

    /** 实际数量 */
    @Schema(description = "实际数量")
    private Integer actualQty;
}