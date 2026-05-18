package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签视图对象
 */
@Data
@Schema(description = "标签信息")
public class TagVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 标签名称 */
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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
