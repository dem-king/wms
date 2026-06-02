package com.wms.business.domain.dto.pda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 盘点明细项DTO
 * 单个物品的盘点结果：系统数量 vs 实际数量
 */
@Data
@Schema(description = "盘点明细项")
public class StockCheckDetailDto {

    /** 物品ID */
    @NotNull(message = "物品ID不能为空")
    @Schema(description = "物品ID")
    private Long itemId;

    /** 系统数量 */
    @NotNull(message = "系统数量不能为空")
    @Schema(description = "系统数量")
    private Integer systemQty;

    /** 实际数量 */
    @NotNull(message = "实际数量不能为空")
    @Schema(description = "实际数量")
    private Integer actualQty;

    /** 库位ID */
    @Schema(description = "库位ID")
    private Long binId;
}