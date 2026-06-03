package com.wms.business.domain.dto;

import com.wms.business.domain.constant.OrderConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 调拨单新增DTO
 * 包含调拨单主表信息和明细列表
 */
@Data
@Schema(description = "调拨单新增请求")
public class TransferOrderDto {

    /** 调出库房ID */
    @NotNull(message = "调出库房ID不能为空")
    @Schema(description = "调出库房ID")
    private Long fromWarehouseId;

    /** 调入库房ID */
    @NotNull(message = "调入库房ID不能为空")
    @Schema(description = "调入库房ID")
    private Long toWarehouseId;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 调拨明细列表 */
    @Valid
    @NotNull(message = "调拨明细不能为空")
    @Schema(description = "调拨明细列表")
    @Size(min = OrderConstants.ORDER_DETAIL_MIN_SIZE, message = "调拨明细不能为空")
    private List<TransferDetailDto> details;

    /**
     * 调拨明细DTO
     */
    @Data
    @Schema(description = "调拨明细请求")
    public static class TransferDetailDto {

        /** 物品ID */
        @NotNull(message = "物品ID不能为空")
        @Schema(description = "物品ID")
        private Long itemId;

        /** 调出库位ID */
        @NotNull(message = "调出库位ID不能为空")
        @Schema(description = "调出库位ID")
        private Long fromBinId;

        /** 调入库位ID */
        @NotNull(message = "调入库位ID不能为空")
        @Schema(description = "调入库位ID")
        private Long toBinId;

        /** 数量 */
        @NotNull(message = "数量不能为空")
        @Schema(description = "数量")
        @Min(value = OrderConstants.ORDER_DETAIL_QUANTITY_MIN, message = "数量必须大于0")
        private Integer quantity;
    }
}
