package com.wms.item.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物品新增/编辑DTO
 */
@Data
@Schema(description = "物品新增/编辑请求")
public class ItemDto {

    /** 物品名称 */
    @NotBlank(message = "物品名称不能为空")
    @Schema(description = "物品名称")
    private String itemName;

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
    @NotNull(message = "主类目ID不能为空")
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

    /** 标签ID列表 */
    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
