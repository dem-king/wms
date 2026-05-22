package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.ScrapReportVo;
import com.wms.report.service.ScrapReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报废统计报表控制器
 * 提供报废汇总、趋势、分类分布统计接口
 */
@Tag(name = "报废统计报表")
@RestController
@RequestMapping("/report/scrap")
@RequiredArgsConstructor
@Validated
public class ScrapReportController {

    private final ScrapReportService scrapReportService;

    /**
     * 报废汇总统计
     */
    @Operation(summary = "报废汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ScrapReportVo.SummaryVo> getSummary(@Valid ReportQueryDto queryDto) {
        return R.ok(scrapReportService.getSummary(queryDto));
    }

    /**
     * 报废趋势统计
     */
    @Operation(summary = "报废趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ScrapReportVo.TrendVo> getTrend(@Valid ReportQueryDto queryDto) {
        return R.ok(scrapReportService.getTrend(queryDto));
    }

    /**
     * 报废分类分布统计
     */
    @Operation(summary = "报废分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ScrapReportVo.DistributionVo> getDistribution(@Valid ReportQueryDto queryDto) {
        return R.ok(scrapReportService.getDistribution(queryDto));
    }
}
