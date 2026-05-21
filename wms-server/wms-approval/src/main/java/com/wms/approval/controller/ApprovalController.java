package com.wms.approval.controller;

import com.wms.approval.domain.dto.ApprovalActionDto;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import com.wms.approval.service.ApprovalService;
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
 * 审批控制器
 * 提供审批流程的发起、审批通过、驳回、撤回等接口
 */
@Tag(name = "审批管理")
@RestController
@RequestMapping("/approval")
@RequiredArgsConstructor
@Validated
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * 审批单分页列表
     */
    @Operation(summary = "审批单分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<ApprovalOrderVo>> pageApprovals(PageParam pageParam,
                                                         @RequestParam(required = false) Integer bizType,
                                                         @RequestParam(required = false) Integer status) {
        return R.ok(approvalService.pageApprovals(pageParam, bizType, status));
    }

    /**
     * 审批单详情(含记录)
     */
    @Operation(summary = "审批单详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ApprovalOrderVo> getApprovalById(@PathVariable Long id) {
        return R.ok(approvalService.getApprovalById(id));
    }

    /**
     * 按业务单据查询审批单
     */
    @Operation(summary = "按业务单据查询审批单")
    @GetMapping("/biz")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ApprovalOrderVo> getByBiz(@RequestParam Long bizId, @RequestParam Integer bizType) {
        return R.ok(approvalService.getByBiz(bizId, bizType));
    }

    /**
     * 审批通过
     */
    @Operation(summary = "审批通过")
    @PostMapping("/{id}/approve")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "approval", type = "审批", desc = "审批通过")
    public R<Void> approve(@PathVariable Long id, @Valid @RequestBody ApprovalActionDto dto) {
        approvalService.approve(id, dto);
        return R.ok();
    }

    /**
     * 审批驳回
     */
    @Operation(summary = "审批驳回")
    @PostMapping("/{id}/reject")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "approval", type = "审批", desc = "审批驳回")
    public R<Void> reject(@PathVariable Long id, @Valid @RequestBody ApprovalActionDto dto) {
        approvalService.reject(id, dto);
        return R.ok();
    }

    /**
     * 撤回审批
     */
    @Operation(summary = "撤回审批")
    @PostMapping("/{id}/revoke")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "approval", type = "撤回", desc = "撤回审批")
    public R<Void> revoke(@PathVariable Long id) {
        approvalService.revoke(id);
        return R.ok();
    }
}
