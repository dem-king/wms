package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 细分类目实体
 * 对应表 wms_sub_category，存储主类目下的细分分类信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_sub_category")
public class WmsSubCategory extends BaseEntity {

    /** 所属主类目ID */
    @Schema(description = "所属主类目ID")
    private Long categoryId;

    /** 细分类目名称 */
    @Schema(description = "细分类目名称")
    private String subCategoryName;

    /** 细分类目编码 */
    @Schema(description = "细分类目编码")
    private String subCategoryCode;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;
}
