package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 主类目新增/编辑DTO
 */
@Data
@Schema(description = "主类目新增/编辑请求")
public class CategoryDto {

    /** 类目名称 */
    @NotBlank(message = "类目名称不能为空")
    @Schema(description = "类目名称")
    private String categoryName;

    /** 类目编码 */
    @NotBlank(message = "类目编码不能为空")
    @Schema(description = "类目编码")
    private String categoryCode;

    /** 标识颜色 */
    @Schema(description = "标识颜色")
    private String categoryColor;

    /** 图标 */
    @Schema(description = "图标")
    private String icon;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 是否消耗品(0-否 1-是) */
    @Schema(description = "是否消耗品(0-否 1-是)")
    private Integer isConsumable;
}
