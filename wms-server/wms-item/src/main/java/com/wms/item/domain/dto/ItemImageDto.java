package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 物品图片关联DTO
 * 用于把统一文件上传结果关联到物品图片记录。
 */
@Data
@Schema(description = "物品图片关联请求")
public class ItemImageDto {

    /** 图片URL */
    @NotBlank(message = "图片URL不能为空")
    @Schema(description = "图片URL")
    private String imageUrl;

    /** 对象存储路径 */
    @NotBlank(message = "对象存储路径不能为空")
    @Schema(description = "对象存储路径")
    private String objectName;

    /** 图片名称 */
    @Schema(description = "图片名称")
    private String imageName;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;
}
