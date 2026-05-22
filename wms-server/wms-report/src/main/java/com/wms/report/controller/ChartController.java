package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ChartQueryDto;
import com.wms.report.domain.vo.ChartOptionVo;
import com.wms.report.service.ChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 图表控制器
 * 提供ECharts图表数据接口
 */
@Tag(name = "图表展示")
@RestController
@RequestMapping("/report/chart")
@RequiredArgsConstructor
@Validated
public class ChartController {

    private final ChartService chartService;

    /**
     * 获取图表选项数据
     */
    @Operation(summary = "获取图表选项数据")
    @GetMapping("/option")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ChartOptionVo> getChartOption(@Valid ChartQueryDto queryDto) {
        return R.ok(chartService.getChartOption(queryDto));
    }
}
