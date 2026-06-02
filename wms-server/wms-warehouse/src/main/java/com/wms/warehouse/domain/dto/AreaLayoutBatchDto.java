package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 区域坐标批量更新DTO
 * 包含多个区域的坐标更新项
 */
@Data
@Schema(description = "区域坐标批量更新请求")
public class AreaLayoutBatchDto {

    /** 区域坐标项列表 */
    @Valid
    @NotEmpty(message = "区域坐标项不能为空")
    @Schema(description = "区域坐标项列表")
    private List<AreaLayoutItemDto> items;
}