package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物品默认库位关联实体
 * 对应表wms_item_bin，用于维护物品档案的默认/可存放库位。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_item_bin")
public class WmsItemBin extends BaseEntity {

    /** 物品ID */
    @Schema(description = "物品ID")
    private Long itemId;

    /** 库位ID */
    @Schema(description = "库位ID")
    private Long binId;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;
}
