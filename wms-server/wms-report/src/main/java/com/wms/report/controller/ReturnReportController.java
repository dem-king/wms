package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.ReturnReportVo;
import com.wms.report.service.ReturnReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 借还统计报表控制器
 * 提供借还汇总、趋势、分类分布统计接口
 */
@Tag(name = "借还统计报表")
@RestController
@RequestMapping("/report/return")
@RequiredArgsConstructor
@Validated
public class ReturnReportController {

    private final ReturnReportService returnReportService;

    /**
     * 借还汇总统计
     */
    @Operation(summary = "借还汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ReturnReportVo.SummaryVo> getSummary(@Valid ReportQueryDto queryDto) {
        return R.ok(returnReportService.getSummary(queryDto));
    }

    /**
     * 借还趋势统计
     */
    @Operation(summary = "借还趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ReturnReportVo.TrendVo> getTrend(@Valid ReportQueryDto queryDto) {
        return R.ok(returnReportService.getTrend(queryDto));
    }

    /**
     * 借还分类分布统计
     */
    @Operation(summary = "借还分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ReturnReportVo.DistributionVo> getDistribution(@Valid ReportQueryDto queryDto) {
        return R.ok(returnReportService.getDistribution(queryDto));
    }
}
