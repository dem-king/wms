package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品图片实体
 * 对应表 wms_item_image，存储物品的图片信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_item_image")
public class WmsItemImage extends BaseEntity {

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 图片URL */
    @Schema(description = "图片URL")
    private String imageUrl;

    /** 存储桶 */
    @Schema(description = "存储桶")
    private String bucket;

    /** 对象存储路径 */
    @Schema(description = "对象存储路径")
    private String objectName;

    /** 图片名称 */
    @Schema(description = "图片名称")
    private String imageName;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;
}
