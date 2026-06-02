package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 区域坐标更新项DTO
 * 描述单个区域在画布上的坐标位置
 */
@Data
@Schema(description = "区域坐标更新项")
public class AreaLayoutItemDto {

    /** 区域ID */
    @NotNull(message = "区域ID不能为空")
    @Schema(description = "区域ID")
    private Long id;

    /** 画布X坐标 */
    @Schema(description = "画布X坐标")
    private Integer coordX;

    /** 画布Y坐标 */
    @Schema(description = "画布Y坐标")
    private Integer coordY;
}