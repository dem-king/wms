package com.wms.monitor.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.monitor.domain.vo.StockAlertVo;
import com.wms.monitor.service.StockAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 库存预警控制器
 * 提供预警记录分页查询和处理接口
 */
@Tag(name = "库存预警")
@RestController
@RequestMapping("/monitor/stock-alert")
@RequiredArgsConstructor
@Validated
public class StockAlertController {

    private final StockAlertService stockAlertService;

    /**
     * 预警记录分页查询
     */
    @Operation(summary = "预警记录分页查询")
    @GetMapping
    @PreAuthorize("hasAuthority('monitor:stock-alert:list')")
    @DataScope
    public R<PageResult<StockAlertVo>> page(PageParam pageParam,
                                            @RequestParam(required = false) String alertType,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) Long warehouseId) {
        return R.ok(stockAlertService.page(pageParam, alertType, status, warehouseId));
    }

    /**
     * 处理预警记录
     */
    @Operation(summary = "处理预警记录")
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('monitor:stock-alert:resolve')")
    @OperLog(module = "monitor", type = "处理", desc = "处理库存预警")
    public R<StockAlertVo> resolve(@PathVariable Long id) {
        return R.ok(stockAlertService.resolve(id));
    }
}
