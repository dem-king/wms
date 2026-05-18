package com.wms.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 入库单新增/编辑DTO
 * 包含入库单主表信息和明细列表
 */
@Data
@Schema(description = "入库单新增/编辑请求")
public class InboundOrderDto {

    /** 库房ID */
    @NotNull(message = "库房ID不能为空")
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 供应商ID */
    @Schema(description = "供应商ID")
    private Long supplierId;

    /** 入库类型(1-采购入库 2-归还入库 3-调拨入库) */
    @NotNull(message = "入库类型不能为空")
    @Schema(description = "入库类型(1-采购入库 2-归还入库 3-调拨入库)")
    private Integer orderType;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 入库明细列表 */
    @Valid
    @NotNull(message = "入库明细不能为空")
    @Schema(description = "入库明细列表")
    private List<InboundDetailDto> details;

    /**
     * 入库明细DTO
     */
    @Data
    @Schema(description = "入库明细请求")
    public static class InboundDetailDto {

        /** 物品ID */
        @NotNull(message = "物品ID不能为空")
        @Schema(description = "物品ID")
        private Long itemId;

        /** 数量 */
        @NotNull(message = "数量不能为空")
        @Schema(description = "数量")
        private Integer quantity;

        /** 单价 */
        @Schema(description = "单价")
        private BigDecimal unitPrice;

        /** 入库库位ID */
        @Schema(description = "入库库位ID")
        private Long binId;
    }
}
