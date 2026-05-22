package com.wms.report.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 出库日聚合实体
 * 对应表 report_outbound_daily，存储按日+库房+分类聚合的出库统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_outbound_daily")
public class ReportOutboundDaily extends BaseEntity {

    /** 统计日期 */
    @Schema(description = "统计日期")
    private LocalDate statDate;

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 物品分类ID */
    @Schema(description = "物品分类ID")
    private Long categoryId;

    /** 出库总数量 */
    @Schema(description = "出库总数量")
    private Integer totalQuantity;

    /** 出库总金额 */
    @Schema(description = "出库总金额")
    private BigDecimal totalAmount;

    /** 出库单据数 */
    @Schema(description = "出库单据数")
    private Integer orderCount;
}
