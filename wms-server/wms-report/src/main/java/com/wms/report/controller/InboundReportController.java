package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.vo.InboundReportVo;
import com.wms.report.service.InboundReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 入库统计报表控制器
 * 提供入库汇总、趋势、分类分布统计接口
 */
@Tag(name = "入库统计报表")
@RestController
@RequestMapping("/report/inbound")
@RequiredArgsConstructor
@Validated
public class InboundReportController {

    private final InboundReportService inboundReportService;

    /**
     * 入库汇总统计
     */
    @Operation(summary = "入库汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('report:inbound:list')")
    @DataScope
    public R<InboundReportVo.SummaryVo> getSummary(@Valid ReportQueryDto queryDto) {
        return R.ok(inboundReportService.getSummary(queryDto));
    }

    /**
     * 入库趋势统计
     */
    @Operation(summary = "入库趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('report:inbound:list')")
    @DataScope
    public R<InboundReportVo.TrendVo> getTrend(@Valid ReportQueryDto queryDto) {
        return R.ok(inboundReportService.getTrend(queryDto));
    }

    /**
     * 入库分类分布统计
     */
    @Operation(summary = "入库分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("hasAuthority('report:inbound:list')")
    @DataScope
    public R<InboundReportVo.DistributionVo> getDistribution(@Valid ReportQueryDto queryDto) {
        return R.ok(inboundReportService.getDistribution(queryDto));
    }
}
