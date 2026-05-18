package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品-标签关联实体
 * 对应表 wms_item_tag，存储物品与标签的多对多关联关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_item_tag")
public class WmsItemTag extends BaseEntity {

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 标签ID */
    @Schema(description = "标签ID")
    private Long tagId;
}
