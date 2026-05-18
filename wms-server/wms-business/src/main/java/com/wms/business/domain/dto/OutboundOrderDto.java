package com.wms.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

        /** 数量 */
        @NotNull(message = "数量不能为空")
        @Schema(description = "数量")
        private Integer quantity;

        /** 库位ID */
        @Schema(description = "库位ID")
        private Long binId;
    }
}
