package com.wms.business.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 归还单新增DTO
 * 包含归还单主表信息和明细列表
 */
@Data
@Schema(description = "归还单新增请求")
public class ReturnOrderDto {

    /** 关联出库单ID */
    @NotNull(message = "关联出库单ID不能为空")
    @Schema(description = "关联出库单ID")
    private Long outboundOrderId;

    /** 归还人 */
    @Schema(description = "归还人")
    private String receiver;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 归还明细列表 */
    @Valid
    @NotNull(message = "归还明细不能为空")
    @Schema(description = "归还明细列表")
    private List<ReturnDetailDto> details;

    /**
     * 归还明细DTO
     */
    @Data
    @Schema(description = "归还明细请求")
    public static class ReturnDetailDto {

        /** 物品ID */
        @NotNull(message = "物品ID不能为空")
        @Schema(description = "物品ID")
        private Long itemId;

        /** 数量 */
        @NotNull(message = "数量不能为空")
        @Schema(description = "数量")
        private Integer quantity;

        /** 物品状态(1-正常 2-损坏 3-丢失 4-数量不符) */
        @Schema(description = "物品状态(1-正常 2-损坏 3-丢失 4-数量不符)")
        private Integer conditionStatus;

        /** 异常说明 */
        @Schema(description = "异常说明")
        private String abnormalRemark;

        /** 实际归还数量(数量不符时填写) */
        @Schema(description = "实际归还数量")
        private Integer actualQuantity;
    }
}
