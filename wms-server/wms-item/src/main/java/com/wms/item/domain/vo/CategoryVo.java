package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 主类目视图对象
 * 包含子分类列表，用于树形展示
 */
@Data
@Schema(description = "主类目信息")
public class CategoryVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 细分类目列表 */
    @Schema(description = "细分类目列表")
    private List<SubCategoryVo> subCategories;
}
