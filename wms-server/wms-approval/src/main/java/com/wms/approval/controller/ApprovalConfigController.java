package com.wms.approval.controller;

import com.wms.approval.domain.dto.ApprovalConfigDto;
import com.wms.approval.domain.vo.ApprovalConfigVo;
import com.wms.approval.service.ApprovalConfigService;
import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 审批配置控制器
 * 提供审批配置的CRUD和分页查询接口
 */
@Tag(name = "审批配置管理")
@RestController
@RequestMapping("/approval/config")
@RequiredArgsConstructor
@Validated
public class ApprovalConfigController {

    private final ApprovalConfigService approvalConfigService;

    /**
     * 审批配置分页列表
     */
    @Operation(summary = "审批配置分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<ApprovalConfigVo>> pageConfigs(PageParam pageParam,
                                                       @RequestParam(required = false) Integer bizType) {
        return R.ok(approvalConfigService.pageConfigs(pageParam, bizType));
    }

    /**
     * 审批配置详情(含节点)
     */
    @Operation(summary = "审批配置详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ApprovalConfigVo> getConfigById(@PathVariable Long id) {
        return R.ok(approvalConfigService.getConfigById(id));
    }

    /**
     * 新增审批配置
     */
    @Operation(summary = "新增审批配置")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "approval", type = "新增", desc = "新增审批配置")
    public R<ApprovalConfigVo> createConfig(@Valid @RequestBody ApprovalConfigDto dto) {
        return R.ok(approvalConfigService.createConfig(dto));
    }

    /**
     * 更新审批配置
     */
    @Operation(summary = "更新审批配置")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "approval", type = "更新", desc = "更新审批配置")
    public R<ApprovalConfigVo> updateConfig(@PathVariable Long id, @Valid @RequestBody ApprovalConfigDto dto) {
        return R.ok(approvalConfigService.updateConfig(id, dto));
    }

    /**
     * 删除审批配置(逻辑删除)
     */
    @Operation(summary = "删除审批配置")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "approval", type = "删除", desc = "删除审批配置")
    public R<Void> deleteConfig(@PathVariable Long id) {
        approvalConfigService.deleteConfig(id);
        return R.ok();
    }
}
