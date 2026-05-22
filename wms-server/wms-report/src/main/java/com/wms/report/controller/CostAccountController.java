package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.CostAccountConfigDto;
import com.wms.report.domain.vo.CostAccountVo;
import com.wms.report.service.CostAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 年度费用核算控制器
 * 提供费用核算汇总、配置查询和更新接口
 */
@Tag(name = "年度费用核算")
@RestController
@RequestMapping("/report/cost")
@RequiredArgsConstructor
@Validated
public class CostAccountController {

    private final CostAccountService costAccountService;

    /**
     * 获取年度费用核算汇总
     */
    @Operation(summary = "费用核算汇总")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<CostAccountVo> getSummary(@RequestParam Integer year) {
        return R.ok(costAccountService.getSummary(year));
    }

    /**
     * 获取费用核算配置
     */
    @Operation(summary = "获取费用核算配置")
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public R<CostAccountVo> getConfig() {
        return R.ok(costAccountService.getConfig());
    }

    /**
     * 更新费用核算配置
     */
    @Operation(summary = "更新费用核算配置")
    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    @OperLog(module = "report", type = "修改", desc = "更新费用核算配置")
    public R<CostAccountVo> updateConfig(@Valid @RequestBody CostAccountConfigDto dto) {
        return R.ok(costAccountService.updateConfig(dto));
    }
}
