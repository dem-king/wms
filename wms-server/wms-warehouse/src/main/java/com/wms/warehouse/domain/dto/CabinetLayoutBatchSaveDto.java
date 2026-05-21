package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 存放柜布局批量保存 DTO
 * 描述区域下多个存放柜的位置和排序保存请求
 */
@Data
@Schema(description = "存放柜布局批量保存请求")
public class CabinetLayoutBatchSaveDto {

    /** 区域ID */
    @NotNull(message = "区域ID不能为空")
    @Schema(description = "区域ID")
    private Long areaId;

    /** 布局项列表 */
    @Valid
    @NotEmpty(message = "布局项不能为空")
    @Schema(description = "布局项列表")
    private List<CabinetLayoutItemDto> cabinets;
}
