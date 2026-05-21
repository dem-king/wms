package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 标签绑定物品请求DTO
 */
@Data
@Schema(description = "标签绑定物品请求")
public class LabelBindDto {

    /** 绑定物品ID */
    @NotNull(message = "物品ID不能为空")
    @Schema(description = "绑定物品ID")
    private Long itemId;

    /** 绑定类型(1-单品对应 2-批次对应) */
    @NotNull(message = "绑定类型不能为空")
    @Min(value = 1, message = "绑定类型: 1-单品对应 2-批次对应")
    @Max(value = 2, message = "绑定类型: 1-单品对应 2-批次对应")
    @Schema(description = "绑定类型(1-单品对应 2-批次对应)")
    private Integer bindType;
}
