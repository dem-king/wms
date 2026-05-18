package com.wms.item.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 物品档案实体
 * 对应表 wms_item，存储物品的基础信息和库存参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_item")
public class WmsItem extends BaseEntity {

    /** 物品编号 */
    @Schema(description = "物品编号")
    private String itemCode;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 拼音/首字母(用于检索) */
    @Schema(description = "拼音/首字母(用于检索)")
    private String pinyin;

    /** 型号 */
    @Schema(description = "型号")
    private String model;

    /** 规格 */
    @Schema(description = "规格")
    private String spec;

    /** 计量单位 */
    @Schema(description = "计量单位")
    private String unit;

    /** 品牌 */
    @Schema(description = "品牌")
    private String brand;

    /** 主类目ID */
    @Schema(description = "主类目ID")
    private Long categoryId;

    /** 细分类目ID */
    @Schema(description = "细分类目ID")
    private Long subCategoryId;

    /** 默认供应商ID */
    @Schema(description = "默认供应商ID")
    private Long supplierId;

    /** 状态(1-在库 2-使用中 3-已归还 4-损坏 5-丢失 6-报废 7-闲置) */
    @Schema(description = "状态(1-在库 2-使用中 3-已归还 4-损坏 5-丢失 6-报废 7-闲置)")
    private Integer status;

    /** 是否消耗品(0-否 1-是) */
    @Schema(description = "是否消耗品(0-否 1-是)")
    private Integer isConsumable;

    /** 是否可归还(0-否 1-是) */
    @Schema(description = "是否可归还(0-否 1-是)")
    private Integer isReturnable;

    /** 采购单价 */
    @Schema(description = "采购单价")
    private BigDecimal purchasePrice;

    /** 库存数量 */
    @Schema(description = "库存数量")
    private Integer stockQty;

    /** 库存下限(安全库存) */
    @Schema(description = "库存下限(安全库存)")
    private Integer stockLowerLimit;

    /** 库存上限(最大库存) */
    @Schema(description = "库存上限(最大库存)")
    private Integer stockUpperLimit;

    /** 补货阈值(消耗品专用) */
    @Schema(description = "补货阈值(消耗品专用)")
    private Integer replenishThreshold;

    /** 闲置判定天数 */
    @Schema(description = "闲置判定天数")
    private Integer idleDays;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
