package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签新增/编辑DTO
 */
@Data
@Schema(description = "标签新增/编辑请求")
public class TagDto {

    /** 标签名称 */
    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称")
    private String tagName;

    /** 标签颜色 */
    @Schema(description = "标签颜色")
    private String tagColor;

    /** 标签描述 */
    @Schema(description = "标签描述")
    private String tagDesc;

    /** 关联范围(0-全局 1-主类目 2-细分类目 3-具体物品) */
    @Schema(description = "关联范围(0-全局 1-主类目 2-细分类目 3-具体物品)")
    private Integer scopeType;

    /** 关联范围对象ID */
    @Schema(description = "关联范围对象ID")
    private Long scopeId;
}
