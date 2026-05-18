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
 * 提供归还单创建、分页查询、详情等接口
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
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ReturnOrderVo> getOrderById(@PathVariable Long id) {
        return R.ok(returnService.getOrderById(id));
    }

    /**
     * 新增归还单
     */
    @Operation(summary = "新增归还单")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "新增", desc = "新增归还单")
    public R<ReturnOrderVo> createOrder(@Valid @RequestBody ReturnOrderDto dto) {
        return R.ok(returnService.createOrder(dto));
    }
}
