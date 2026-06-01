package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.TransferReportQueryDto;
import com.wms.report.domain.vo.TransferReportVo;
import com.wms.report.service.TransferReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 调拨统计报表控制器
 * 提供调拨汇总、趋势、分类分布统计接口
 */
@Tag(name = "调拨统计报表")
@RestController
@RequestMapping("/report/transfer")
@RequiredArgsConstructor
@Validated
public class TransferReportController {

    private final TransferReportService transferReportService;

    /**
     * 调拨汇总统计
     */
    @Operation(summary = "调拨汇总统计")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('report:transfer:list')")
    @DataScope
    public R<TransferReportVo.SummaryVo> getSummary(@Valid TransferReportQueryDto queryDto) {
        return R.ok(transferReportService.getSummary(queryDto));
    }

    /**
     * 调拨趋势统计
     */
    @Operation(summary = "调拨趋势统计")
    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('report:transfer:list')")
    @DataScope
    public R<TransferReportVo.TrendVo> getTrend(@Valid TransferReportQueryDto queryDto) {
        return R.ok(transferReportService.getTrend(queryDto));
    }

    /**
     * 调拨分类分布统计
     */
    @Operation(summary = "调拨分类分布统计")
    @GetMapping("/category-distribution")
    @PreAuthorize("hasAuthority('report:transfer:list')")
    @DataScope
    public R<TransferReportVo.DistributionVo> getDistribution(@Valid TransferReportQueryDto queryDto) {
        return R.ok(transferReportService.getDistribution(queryDto));
    }
}
