package com.wms.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 报废单新增DTO
 * 包含报废单主表信息和明细列表
 */
@Data
@Schema(description = "报废单新增请求")
public class ScrapOrderDto {

    /** 所属库房ID */
    @NotNull(message = "所属库房ID不能为空")
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 报废原因 */
    @NotBlank(message = "报废原因不能为空")
    @Schema(description = "报废原因")
    private String scrapReason;

    /** 报废明细列表 */
    @Valid
    @NotNull(message = "报废明细不能为空")
    @Schema(description = "报废明细列表")
    private List<ScrapDetailDto> details;

    /**
     * 报废明细DTO
     */
    @Data
    @Schema(description = "报废明细请求")
    public static class ScrapDetailDto {

        /** 物品ID */
        @NotNull(message = "物品ID不能为空")
        @Schema(description = "物品ID")
        private Long itemId;

        /** 数量 */
        @NotNull(message = "数量不能为空")
        @Schema(description = "数量")
        private Integer quantity;
    }
}
