package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 存放柜布局项 DTO
 * 描述单个存放柜在布局中的坐标和排序信息
 */
@Data
@Schema(description = "存放柜布局项")
public class CabinetLayoutItemDto {

    /** 存放柜ID */
    @NotNull(message = "存放柜ID不能为空")
    @Schema(description = "存放柜ID")
    private Long id;

    /** X坐标 */
    @NotNull(message = "X坐标不能为空")
    @Min(value = 0, message = "X坐标不能小于0")
    @Schema(description = "X坐标")
    private Integer positionX;

    /** Y坐标 */
    @NotNull(message = "Y坐标不能为空")
    @Min(value = 0, message = "Y坐标不能小于0")
    @Schema(description = "Y坐标")
    private Integer positionY;

    /** 排序号 */
    @NotNull(message = "排序号不能为空")
    @Min(value = 1, message = "排序号必须大于0")
    @Schema(description = "排序号")
    private Integer sortOrder;
}
