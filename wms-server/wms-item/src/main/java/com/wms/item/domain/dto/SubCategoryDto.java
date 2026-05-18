package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 细分类目新增/编辑DTO
 */
@Data
@Schema(description = "细分类目新增/编辑请求")
public class SubCategoryDto {

    /** 细分类目名称 */
    @NotBlank(message = "细分类目名称不能为空")
    @Schema(description = "细分类目名称")
    private String subCategoryName;

    /** 细分类目编码 */
    @NotBlank(message = "细分类目编码不能为空")
    @Schema(description = "细分类目编码")
    private String subCategoryCode;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;
}
