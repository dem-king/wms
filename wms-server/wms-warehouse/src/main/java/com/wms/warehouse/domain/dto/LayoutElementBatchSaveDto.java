package com.wms.warehouse.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 布局元素批量保存DTO
 * 包含新增、更新、删除的元素列表
 */
@Data
@Schema(description = "布局元素批量保存请求")
public class LayoutElementBatchSaveDto {

    /** 所属库房ID */
    @NotNull(message = "所属库房ID不能为空")
    @Schema(description = "所属库房ID")
    private Long warehouseId;

    /** 新增元素列表 */
    @Valid
    @Schema(description = "新增元素列表")
    private List<LayoutElementDto> created;

    /** 更新元素列表 */
    @Valid
    @Schema(description = "更新元素列表")
    private List<LayoutElementUpdateItemDto> updated;

    /** 删除元素ID列表 */
    @Schema(description = "删除元素ID列表")
    private List<Long> deletedIds;
}