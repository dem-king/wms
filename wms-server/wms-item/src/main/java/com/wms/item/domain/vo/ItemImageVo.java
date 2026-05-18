package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 物品图片视图对象
 */
@Data
@Schema(description = "物品图片信息")
public class ItemImageVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 图片URL */
    @Schema(description = "图片URL")
    private String imageUrl;

    /** 图片名称 */
    @Schema(description = "图片名称")
    private String imageName;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;
}
