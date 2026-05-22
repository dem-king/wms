package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存日快照实体
 * 对应表 report_stock_daily，存储按日+库房+分类的库存快照数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_stock_daily")
public class ReportStockDaily extends BaseEntity {

    /** 快照日期 */
    @Schema(description = "快照日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 库存总数量 */
    @Schema(description = "库存总数量")
    private Integer totalQuantity;

    /** 库存总金额 */
    @Schema(description = "库存总金额")
    private BigDecimal totalAmount;
}
