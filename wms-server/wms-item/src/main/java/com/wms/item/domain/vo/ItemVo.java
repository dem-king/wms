package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 物品视图对象
 * 包含类目名称、标签列表等关联信息
 */
@Data
@Schema(description = "物品信息")
public class ItemVo {

    /** 主键 */
    @Schema(description = "主键")
    private Long id;

    /** 物品编号 */
    @Schema(description = "物品编号")
    private String itemCode;

    /** 物品名称 */
    @Schema(description = "物品名称")
    private String itemName;

    /** 拼音/首字母 */
    @Schema(description = "拼音/首字母")
    private String pinyin;

    /** 型号 */
    @Schema(description = "型号")
    private String model;

    /** 规格 */
    @Schema(description = "规格")
    private String spec;

    /** 规格型号 */
    @Schema(description = "规格型号")
    private String specModel;

    /** 计量单位 */
    @Schema(description = "计量单位")
    private String unit;

    /** 品牌 */
    @Schema(description = "品牌")
    private String brand;

    /** 主类目ID */
    @Schema(description = "主类目ID")
    private Long categoryId;

    /** 主类目名称 */
    @Schema(description = "主类目名称")
    private String categoryName;

    /** 细分类目ID */
    @Schema(description = "细分类目ID")
    private Long subCategoryId;

    /** 细分类目名称 */
    @Schema(description = "细分类目名称")
    private String subCategoryName;

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

    /** 实时库存数量 */
    @Schema(description = "实时库存数量")
    private Integer currentStock;

    /** 库存下限(安全库存) */
    @Schema(description = "库存下限(安全库存)")
    private Integer stockLowerLimit;

    /** 库存上限(最大库存) */
    @Schema(description = "库存上限(最大库存)")
    private Integer stockUpperLimit;

    /** 补货阈值 */
    @Schema(description = "补货阈值")
    private Integer replenishThreshold;

    /** 闲置判定天数 */
    @Schema(description = "闲置判定天数")
    private Integer idleDays;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 标签列表 */
    @Schema(description = "标签列表")
    private List<TagVo> tags;

    /** 标签ID列表 */
    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    /** 标签名称列表 */
    @Schema(description = "标签名称列表")
    private List<String> tagNames;

    /** 图片列表 */
    @Schema(description = "图片列表")
    private List<ItemImageVo> images;

    /** 默认库位ID列表 */
    @Schema(description = "默认库位ID列表")
    private List<Long> binIds;

    /** 默认库位列表 */
    @Schema(description = "默认库位列表")
    private List<ItemLocationVo> locations;
}
