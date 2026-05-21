package com.wms.warehouse.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.warehouse.domain.dto.WarehouseDto;
import com.wms.warehouse.domain.vo.WarehouseVo;
import com.wms.warehouse.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库房管理控制器
 * 提供库房CRUD、分页查询、列表查询等接口
 */
@Tag(name = "库房管理")
@RestController
@RequestMapping("/warehouse/warehouses")
@RequiredArgsConstructor
@Validated
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * 查询所有库房列表
     */
    @Operation(summary = "查询所有库房列表")
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<WarehouseVo>> listAll() {
        return R.ok(warehouseService.listAll());
    }

    /**
     * 库房分页列表
     */
    @Operation(summary = "库房分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<WarehouseVo>> page(PageParam pageParam,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) String keyword) {
        return R.ok(warehouseService.page(pageParam, status, keyword));
    }

    /**
     * 新增库房
     */
    @Operation(summary = "新增库房")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "新增", desc = "新增库房")
    public R<WarehouseVo> create(@Valid @RequestBody WarehouseDto dto) {
        return R.ok(warehouseService.create(dto));
    }

    /**
     * 更新库房
     */
    @Operation(summary = "更新库房")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "更新", desc = "更新库房")
    public R<WarehouseVo> update(@PathVariable Long id, @Valid @RequestBody WarehouseDto dto) {
        return R.ok(warehouseService.update(id, dto));
    }

    /**
     * 删除库房
     */
    @Operation(summary = "删除库房")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "删除", desc = "删除库房")
    public R<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return R.ok();
    }
}
