package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.OutboundReportVo;
import com.wms.report.service.OutboundReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 出库统计报表控制器
 * 提供出库汇总、趋势、分类分布统计接口
 */
@Tag(name = "出库统计报表")
@RestController
@RequestMapping("/report/outbound")
@RequiredArgsConstructor
@Validated
public class OutboundReportController {

    private final OutboundReportService outboundReportService;

    /**
     * 出库汇总统计
     */
    @Operation(summary = "出库汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<OutboundReportVo.SummaryVo> getSummary(@Valid ReportQueryDto queryDto) {
        return R.ok(outboundReportService.getSummary(queryDto));
    }

    /**
     * 出库趋势统计
     */
    @Operation(summary = "出库趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<OutboundReportVo.TrendVo> getTrend(@Valid ReportQueryDto queryDto) {
        return R.ok(outboundReportService.getTrend(queryDto));
    }

    /**
     * 出库分类分布统计
     */
    @Operation(summary = "出库分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<OutboundReportVo.DistributionVo> getDistribution(@Valid ReportQueryDto queryDto) {
        return R.ok(outboundReportService.getDistribution(queryDto));
    }
}
