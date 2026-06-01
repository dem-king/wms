package com.wms.business.controller;

import com.wms.business.domain.dto.InboundOrderDto;
import com.wms.business.domain.dto.InboundScanDto;
import com.wms.business.domain.vo.InboundOrderVo;
import com.wms.business.domain.vo.InboundScanResultVo;
import com.wms.business.service.InboundScanService;
import com.wms.business.service.InboundService;
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
 * 入库单控制器
 * 提供入库单CRUD、分页查询、提交等接口
 */
@Tag(name = "入库管理")
@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
@Validated
public class InboundController {

    private final InboundService inboundService;
    private final InboundScanService inboundScanService;

    /**
     * 入库单分页列表
     */
    @Operation(summary = "入库单分页列表")
    @GetMapping
    @PreAuthorize("hasAuthority('business:inbound:list')")
    @DataScope
    public R<PageResult<InboundOrderVo>> pageOrders(PageParam pageParam,
                                                     @RequestParam(required = false) Long warehouseId,
                                                     @RequestParam(required = false) Integer orderType,
                                                     @RequestParam(required = false) Integer status,
                                                     @RequestParam(required = false) String orderNo) {
        return R.ok(inboundService.pageOrders(pageParam, warehouseId, orderType, status, orderNo));
    }

    /**
     * 入库单详情(含明细)
     */
    @Operation(summary = "入库单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:inbound:list')")
    @DataScope
    public R<InboundOrderVo> getOrderById(@PathVariable Long id) {
        return R.ok(inboundService.getOrderById(id));
    }

    /**
     * 新增入库单
     */
    @Operation(summary = "新增入库单")
    @PostMapping
    @PreAuthorize("hasAuthority('business:inbound:add')")
    @OperLog(module = "business", type = "新增", desc = "新增入库单")
    public R<InboundOrderVo> createOrder(@Valid @RequestBody InboundOrderDto dto) {
        return R.ok(inboundService.createOrder(dto));
    }

    /**
     * 更新入库单(仅草稿状态)
     */
    @Operation(summary = "更新入库单")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:inbound:edit')")
    @OperLog(module = "business", type = "更新", desc = "更新入库单")
    public R<InboundOrderVo> updateOrder(@PathVariable Long id, @Valid @RequestBody InboundOrderDto dto) {
        return R.ok(inboundService.updateOrder(id, dto));
    }

    /**
     * 入库单扫码识别
     */
    @Operation(summary = "入库单扫码识别")
    @PostMapping("/scan")
    @PreAuthorize("hasAuthority('business:inbound:scan')")
    @DataScope
    public R<InboundScanResultVo> scan(@Valid @RequestBody InboundScanDto dto) {
        return R.ok(inboundScanService.scan(dto));
    }

    /**
     * 提交入库单
     */
    @Operation(summary = "提交入库单")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('business:inbound:submit')")
    @OperLog(module = "business", type = "提交", desc = "提交入库单")
    public R<Void> submitOrder(@PathVariable Long id) {
        inboundService.submitOrder(id);
        return R.ok();
    }

    /**
     * 删除入库单(仅草稿状态)
     */
    @Operation(summary = "删除入库单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:inbound:delete')")
    @OperLog(module = "business", type = "删除", desc = "删除入库单")
    public R<Void> deleteOrder(@PathVariable Long id) {
        inboundService.deleteOrder(id);
        return R.ok();
    }
}
