package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.AlertReportQueryDto;
import com.wms.report.domain.vo.AlertReportVo;
import com.wms.report.service.AlertReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 预警统计报表控制器
 * 提供预警汇总、趋势、类型分布统计接口
 */
@Tag(name = "预警统计报表")
@RestController
@RequestMapping("/report/alert")
@RequiredArgsConstructor
@Validated
public class AlertReportController {

    private final AlertReportService alertReportService;

    /**
     * 预警汇总统计
     */
    @Operation(summary = "预警汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<AlertReportVo.SummaryVo> getSummary(@Valid AlertReportQueryDto queryDto) {
        return R.ok(alertReportService.getSummary(queryDto));
    }

    /**
     * 预警趋势统计
     */
    @Operation(summary = "预警趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<AlertReportVo.TrendVo> getTrend(@Valid AlertReportQueryDto queryDto) {
        return R.ok(alertReportService.getTrend(queryDto));
    }

    /**
     * 预警类型分布统计
     */
    @Operation(summary = "预警类型分布统计")
    @GetMapping("/type-distribution")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<AlertReportVo.DistributionVo> getDistribution(@Valid AlertReportQueryDto queryDto) {
        return R.ok(alertReportService.getDistribution(queryDto));
    }
}
