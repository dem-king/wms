package com.wms.report.controller.dashboard;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.dashboard.DashboardConfigDto;
import com.wms.report.domain.vo.dashboard.DashboardConfigVo;
import com.wms.report.domain.vo.dashboard.DashboardDataVo;
import com.wms.report.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "首页工作台接口")
@RestController
@RequestMapping("/report/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @DataScope
    @Operation(summary = "获取首页数据")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/data")
    public R<DashboardDataVo> getDashboardData(@RequestParam(required = false, defaultValue = "admin") String role) {
        return R.ok(dashboardService.getDashboardData(role));
    }

    @Operation(summary = "获取首页配置")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/config")
    public R<DashboardConfigVo> getConfig() {
        return R.ok(dashboardService.getConfig());
    }

    @Operation(summary = "保存首页配置")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/config")
    public R<Void> saveConfig(@RequestBody DashboardConfigDto dto) {
        dashboardService.saveConfig(dto);
        return R.ok();
    }
}