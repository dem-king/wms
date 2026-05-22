package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存预警阈值设置DTO
 * 用于设置物品的安全库存、最大库存和补货阈值
 */
@Data
@Schema(description = "库存预警阈值设置请求")
public class StockThresholdDto {

    /** 库存下限(安全库存) */
    @Schema(description = "库存下限(安全库存)")
    private Integer stockLowerLimit;

    /** 库存上限(最大库存) */
    @Schema(description = "库存上限(最大库存)")
    private Integer stockUpperLimit;

    /** 补货阈值(消耗品专用) */
    @Schema(description = "补货阈值(消耗品专用)")
    private Integer replenishThreshold;
}
