package com.wms.business.controller;

import com.wms.business.domain.dto.OutboundOrderDto;
import com.wms.business.domain.vo.OutboundOrderVo;
import com.wms.business.service.OutboundService;
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
 * 出库单控制器
 * 提供出库单CRUD、分页查询、提交等接口
 */
@Tag(name = "出库管理")
@RestController
@RequestMapping("/outbound")
@RequiredArgsConstructor
@Validated
public class OutboundController {

    private final OutboundService outboundService;

    /**
     * 出库单分页列表
     */
    @Operation(summary = "出库单分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<OutboundOrderVo>> pageOrders(PageParam pageParam,
                                                      @RequestParam(required = false) Long warehouseId,
                                                      @RequestParam(required = false) Integer orderType,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) String orderNo) {
        return R.ok(outboundService.pageOrders(pageParam, warehouseId, orderType, status, orderNo));
    }

    /**
     * 出库单详情(含明细)
     */
    @Operation(summary = "出库单详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<OutboundOrderVo> getOrderById(@PathVariable Long id) {
        return R.ok(outboundService.getOrderById(id));
    }

    /**
     * 新增出库单
     */
    @Operation(summary = "新增出库单")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "新增", desc = "新增出库单")
    public R<OutboundOrderVo> createOrder(@Valid @RequestBody OutboundOrderDto dto) {
        return R.ok(outboundService.createOrder(dto));
    }

    /**
     * 更新出库单(仅草稿状态)
     */
    @Operation(summary = "更新出库单")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "更新", desc = "更新出库单")
    public R<OutboundOrderVo> updateOrder(@PathVariable Long id, @Valid @RequestBody OutboundOrderDto dto) {
        return R.ok(outboundService.updateOrder(id, dto));
    }

    /**
     * 提交出库单
     */
    @Operation(summary = "提交出库单")
    @PostMapping("/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "提交", desc = "提交出库单")
    public R<Void> submitOrder(@PathVariable Long id) {
        outboundService.submitOrder(id);
        return R.ok();
    }

    /**
     * 删除出库单(仅草稿状态)
     */
    @Operation(summary = "删除出库单")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "删除", desc = "删除出库单")
    public R<Void> deleteOrder(@PathVariable Long id) {
        outboundService.deleteOrder(id);
        return R.ok();
    }
}
