package com.wms.business.domain.dto;

import com.wms.business.domain.constant.OrderConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单新增/编辑DTO
 * 包含出库单主表信息和明细列表
 */
@Data
@Schema(description = "出库单新增/编辑请求")
public class OutboundOrderDto {

    /** 库房ID */
    @NotNull(message = "库房ID不能为空")
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 出库类型(1-领用出库 2-调拨出库 3-报废出库) */
    @NotNull(message = "出库类型不能为空")
    @Schema(description = "出库类型(1-领用出库 2-调拨出库 3-报废出库)")
    @Min(value = OrderConstants.ORDER_TYPE_MIN, message = "出库类型不正确")
    @Max(value = OrderConstants.ORDER_TYPE_MAX, message = "出库类型不正确")
    private Integer orderType;

    /** 领用人 */
    @Schema(description = "领用人")
    private String receiver;

    /** 用途 */
    @Schema(description = "用途")
    private String purpose;

    /** 预计归还日期 */
    @Schema(description = "预计归还日期")
    private LocalDateTime expectedReturnDate;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 出库明细列表 */
    @Valid
    @NotNull(message = "出库明细不能为空")
    @Schema(description = "出库明细列表")
    @Size(min = OrderConstants.ORDER_DETAIL_MIN_SIZE, message = "出库明细不能为空")
    private List<OutboundDetailDto> details;

    /**
     * 出库明细DTO
     */
    @Data
    @Schema(description = "出库明细请求")
    public static class OutboundDetailDto {

        /** 物品ID */
        @NotNull(message = "物品ID不能为空")
        @Schema(description = "物品ID")
        private Long itemId;

        /** 电子标签ID */
        @Schema(description = "电子标签ID")
        private Long labelId;

        /** 数量 */
        @NotNull(message = "数量不能为空")
        @Schema(description = "数量")
        @Min(value = OrderConstants.ORDER_DETAIL_QUANTITY_MIN, message = "数量必须大于0")
        private Integer quantity;

        /** 库位ID */
        @NotNull(message = "出库库位ID不能为空")
        @Schema(description = "库位ID")
        private Long binId;
    }
}
