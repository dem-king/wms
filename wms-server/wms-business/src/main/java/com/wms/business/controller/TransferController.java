package com.wms.business.controller;

import com.wms.business.domain.dto.TransferOrderDto;
import com.wms.business.domain.vo.TransferOrderVo;
import com.wms.business.service.TransferService;
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
 * 调拨单控制器
 * 提供调拨单创建、提交、分页查询、详情等接口
 */
@Tag(name = "调拨管理")
@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
@Validated
public class TransferController {

    private final TransferService transferService;

    /**
     * 调拨单分页列表
     */
    @Operation(summary = "调拨单分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<TransferOrderVo>> pageOrders(PageParam pageParam,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) String orderNo) {
        return R.ok(transferService.pageOrders(pageParam, status, orderNo));
    }

    /**
     * 调拨单详情(含明细)
     */
    @Operation(summary = "调拨单详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<TransferOrderVo> getOrderById(@PathVariable Long id) {
        return R.ok(transferService.getOrderById(id));
    }

    /**
     * 新增调拨单
     */
    @Operation(summary = "新增调拨单")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "新增", desc = "新增调拨单")
    public R<TransferOrderVo> createOrder(@Valid @RequestBody TransferOrderDto dto) {
        return R.ok(transferService.createOrder(dto));
    }

    /**
     * 提交调拨单
     */
    @Operation(summary = "提交调拨单")
    @PostMapping("/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "提交", desc = "提交调拨单")
    public R<Void> submitOrder(@PathVariable Long id) {
        transferService.submitOrder(id);
        return R.ok();
    }
}
