package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 调拨日聚合实体
 * 对应表 report_transfer_daily，存储按日+调出库房+调入库房+分类聚合的调拨统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_transfer_daily")
public class ReportTransferDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 调出库房ID */
    @Schema(description = "调出库房ID")
    private Long fromWarehouseId;

    /** 调入库房ID */
    @Schema(description = "调入库房ID")
    private Long toWarehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 调拨总数量 */
    @Schema(description = "调拨总数量")
    private Integer totalQuantity;

    /** 调拨总金额 */
    @Schema(description = "调拨总金额")
    private BigDecimal totalAmount;

    /** 调拨单据数 */
    @Schema(description = "调拨单据数")
    private Integer orderCount;
}
