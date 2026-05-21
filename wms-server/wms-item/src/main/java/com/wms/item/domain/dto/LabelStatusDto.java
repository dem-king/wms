package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 标签状态更新请求DTO
 */
@Data
@Schema(description = "标签状态更新请求")
public class LabelStatusDto {

    /** 标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置) */
    @NotNull(message = "标签状态不能为空")
    @Min(value = 1, message = "标签状态范围: 1-5")
    @Max(value = 5, message = "标签状态范围: 1-5")
    @Schema(description = "标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置)")
    private Integer labelStatus;
}
