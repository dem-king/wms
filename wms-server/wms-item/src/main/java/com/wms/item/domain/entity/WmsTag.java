package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义标签实体
 * 对应表 wms_tag，存储物品的自定义标签信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_tag")
public class WmsTag extends BaseEntity {

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
}
