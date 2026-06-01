package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.StockReportVo;
import com.wms.report.service.StockReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 库存统计报表控制器
 * 提供库存汇总、趋势、分类分布统计接口
 */
@Tag(name = "库存统计报表")
@RestController
@RequestMapping("/report/stock")
@RequiredArgsConstructor
@Validated
public class StockReportController {

    private final StockReportService stockReportService;

    /**
     * 库存汇总统计
     */
    @Operation(summary = "库存汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('report:stock:list')")
    @DataScope
    public R<StockReportVo.SummaryVo> getSummary(@Valid ReportQueryDto queryDto) {
        return R.ok(stockReportService.getSummary(queryDto));
    }

    /**
     * 库存趋势统计
     */
    @Operation(summary = "库存趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('report:stock:list')")
    @DataScope
    public R<StockReportVo.TrendVo> getTrend(@Valid ReportQueryDto queryDto) {
        return R.ok(stockReportService.getTrend(queryDto));
    }

    /**
     * 库存分类分布统计
     */
    @Operation(summary = "库存分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("hasAuthority('report:stock:list')")
    @DataScope
    public R<StockReportVo.DistributionVo> getDistribution(@Valid ReportQueryDto queryDto) {
        return R.ok(stockReportService.getDistribution(queryDto));
    }
}
