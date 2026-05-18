package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 细分类目视图对象
 */
@Data
@Schema(description = "细分类目信息")
public class SubCategoryVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
