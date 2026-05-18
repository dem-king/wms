package com.wms.item.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.item.domain.vo.StockVo;
import com.wms.item.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理控制器
 * 提供库存分页查询、物品库存详情、库存预警等接口
 */
@Tag(name = "库存管理")
@RestController
@RequestMapping("/item/stock")
@RequiredArgsConstructor
@Validated
public class StockController {

    private final StockService stockService;

    /**
     * 库存分页列表
     */
    @Operation(summary = "库存分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<StockVo>> page(PageParam pageParam,
                                       @RequestParam(required = false) Long warehouseId,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Boolean alertOnly) {
        return R.ok(stockService.page(pageParam, warehouseId, categoryId, alertOnly));
    }

    /**
     * 物品库存详情(各库位)
     */
    @Operation(summary = "物品库存详情")
    @GetMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<StockVo>> getByItemId(@PathVariable Long itemId) {
        return R.ok(stockService.getByItemId(itemId));
    }

    /**
     * 库存预警列表
     */
    @Operation(summary = "库存预警列表")
    @GetMapping("/alert")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<StockVo>> getAlertList() {
        return R.ok(stockService.getAlertList());
    }
}
