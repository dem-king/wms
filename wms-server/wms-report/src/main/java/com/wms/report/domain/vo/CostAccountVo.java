package com.wms.report.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 年度费用核算视图对象
 */
@Data
@Schema(description = "年度费用核算结果")
public class CostAccountVo {

    /** 核算年度 */
    @Schema(description = "核算年度")
    private Integer year;

    /** 核算周期 */
    @Schema(description = "核算周期")
    private String period;

    /** 各周期费用明细 */
    @Schema(description = "各周期费用明细")
    private List<CostPeriodItem> periodList;

    /** 年度总费用 */
    @Schema(description = "年度总费用")
    private BigDecimal totalCost;

    /**
     * 周期费用明细项
     */
    @Data
    @Schema(description = "周期费用明细项")
    public static class CostPeriodItem {

        /** 周期标签 */
        @Schema(description = "周期标签")
        private String periodLabel;

        /** 采购入库费用 */
        @Schema(description = "采购入库费用")
        private BigDecimal purchaseCost;

        /** 消耗出库费用 */
        @Schema(description = "消耗出库费用")
        private BigDecimal consumeCost;

        /** 报废损失费用 */
        @Schema(description = "报废损失费用")
        private BigDecimal scrapCost;

        /** 调拨费用 */
        @Schema(description = "调拨费用")
        private BigDecimal transferCost;

        /** 周期合计 */
        @Schema(description = "周期合计")
        private BigDecimal periodTotal;
    }
}
