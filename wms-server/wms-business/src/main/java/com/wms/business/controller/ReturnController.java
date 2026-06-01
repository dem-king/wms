package com.wms.business.controller;

import com.wms.business.domain.dto.ReturnOrderDto;
import com.wms.business.domain.vo.ReturnOrderVo;
import com.wms.business.service.ReturnService;
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
 * 归还单控制器
 * 提供归还单创建、更新、删除、提交、分页查询等接口
 */
@Tag(name = "归还管理")
@RestController
@RequestMapping("/return")
@RequiredArgsConstructor
@Validated
public class ReturnController {

    private final ReturnService returnService;

    /**
     * 归还单分页列表
     */
    @Operation(summary = "归还单分页列表")
    @GetMapping
    @PreAuthorize("hasAuthority('business:return:list')")
    @DataScope
    public R<PageResult<ReturnOrderVo>> pageOrders(PageParam pageParam,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) String orderNo) {
        return R.ok(returnService.pageOrders(pageParam, status, orderNo));
    }

    /**
     * 归还单详情(含明细)
     */
    @Operation(summary = "归还单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:return:list')")
    @DataScope
    public R<ReturnOrderVo> getOrderById(@PathVariable Long id) {
        return R.ok(returnService.getOrderById(id));
    }

    /**
     * 新增归还单
     */
    @Operation(summary = "新增归还单")
    @PostMapping
    @PreAuthorize("hasAuthority('business:return:add')")
    @OperLog(module = "business", type = "新增", desc = "新增归还单")
    public R<ReturnOrderVo> createOrder(@Valid @RequestBody ReturnOrderDto dto) {
        return R.ok(returnService.createOrder(dto));
    }

    /**
     * 更新归还单(仅草稿状态)
     */
    @Operation(summary = "更新归还单")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:return:edit')")
    @OperLog(module = "business", type = "更新", desc = "更新归还单")
    public R<ReturnOrderVo> updateOrder(@PathVariable Long id, @Valid @RequestBody ReturnOrderDto dto) {
        return R.ok(returnService.updateOrder(id, dto));
    }

    /**
     * 提交归还单
     */
    @Operation(summary = "提交归还单")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('business:return:submit')")
    @OperLog(module = "business", type = "提交", desc = "提交归还单")
    public R<Void> submitOrder(@PathVariable Long id) {
        returnService.submitOrder(id);
        return R.ok();
    }

    /**
     * 删除归还单(仅草稿状态)
     */
    @Operation(summary = "删除归还单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:return:delete')")
    @OperLog(module = "business", type = "删除", desc = "删除归还单")
    public R<Void> deleteOrder(@PathVariable Long id) {
        returnService.deleteOrder(id);
        return R.ok();
    }
}
