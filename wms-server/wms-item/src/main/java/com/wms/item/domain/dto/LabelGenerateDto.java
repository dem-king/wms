package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 批量生成标签请求DTO
 */
@Data
@Schema(description = "批量生成标签请求")
public class LabelGenerateDto {

    /** 绑定物品ID */
    @Schema(description = "绑定物品ID")
    private Long itemId;

    /** 绑定库位ID */
    @NotNull(message = "库位ID不能为空")
    @Schema(description = "绑定库位ID")
    private Long binId;

    /** 标签编号前缀 */
    @Schema(description = "标签编号前缀")
    private String labelPrefix;

    /** 生成数量 */
    @NotNull(message = "生成数量不能为空")
    @Min(value = 1, message = "生成数量至少为1")
    @Max(value = 100, message = "单次生成数量不能超过100")
    @Schema(description = "生成数量")
    private Integer count;

    /** 标签类型(1-二维码 2-条形码 3-RFID) */
    @NotNull(message = "标签类型不能为空")
    @Min(value = 1, message = "标签类型: 1-二维码 2-条形码 3-RFID")
    @Max(value = 3, message = "标签类型: 1-二维码 2-条形码 3-RFID")
    @Schema(description = "标签类型(1-二维码 2-条形码 3-RFID)")
    private Integer labelType;

    /** 绑定类型(1-单品对应 2-批次对应) */
    @NotNull(message = "绑定类型不能为空")
    @Min(value = 1, message = "绑定类型: 1-单品对应 2-批次对应")
    @Max(value = 2, message = "绑定类型: 1-单品对应 2-批次对应")
    @Schema(description = "绑定类型(1-单品对应 2-批次对应)")
    private Integer bindType;
}
