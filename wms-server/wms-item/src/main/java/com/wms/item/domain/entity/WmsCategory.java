package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 主类目实体
 * 对应表 wms_category，存储物品主分类信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_category")
public class WmsCategory extends BaseEntity {

    /** 类目名称 */
    @Schema(description = "类目名称")
    private String categoryName;

    /** 类目编码 */
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
