package com.wms.business.controller;

import com.wms.business.domain.dto.ScrapOrderDto;
import com.wms.business.domain.vo.ScrapOrderVo;
import com.wms.business.service.ScrapService;
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
 * 报废单控制器
 * 提供报废单创建、提交、分页查询、详情等接口
 */
@Tag(name = "报废管理")
@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
@Validated
public class ScrapController {

    private final ScrapService scrapService;

    /**
     * 报废单分页列表
     */
    @Operation(summary = "报废单分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<ScrapOrderVo>> pageOrders(PageParam pageParam,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(required = false) String orderNo) {
        return R.ok(scrapService.pageOrders(pageParam, status, orderNo));
    }

    /**
     * 报废单详情(含明细)
     */
    @Operation(summary = "报废单详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ScrapOrderVo> getOrderById(@PathVariable Long id) {
        return R.ok(scrapService.getOrderById(id));
    }

    /**
     * 新增报废单
     */
    @Operation(summary = "新增报废单")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "新增", desc = "新增报废单")
    public R<ScrapOrderVo> createOrder(@Valid @RequestBody ScrapOrderDto dto) {
        return R.ok(scrapService.createOrder(dto));
    }

    /**
     * 更新报废单(仅草稿状态)
     */
    @Operation(summary = "更新报废单")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "更新", desc = "更新报废单")
    public R<ScrapOrderVo> updateOrder(@PathVariable Long id, @Valid @RequestBody ScrapOrderDto dto) {
        return R.ok(scrapService.updateOrder(id, dto));
    }

    /**
     * 提交报废单
     */
    @Operation(summary = "提交报废单")
    @PostMapping("/{id}/submit")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "提交", desc = "提交报废单")
    public R<Void> submitOrder(@PathVariable Long id) {
        scrapService.submitOrder(id);
        return R.ok();
    }

    /**
     * 删除报废单(仅草稿状态)
     */
    @Operation(summary = "删除报废单")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "business", type = "删除", desc = "删除报废单")
    public R<Void> deleteOrder(@PathVariable Long id) {
        scrapService.deleteOrder(id);
        return R.ok();
    }
}
