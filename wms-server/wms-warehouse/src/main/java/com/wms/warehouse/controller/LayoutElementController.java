package com.wms.warehouse.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.warehouse.domain.dto.LayoutElementBatchSaveDto;
import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.vo.LayoutElementBatchSaveVo;
import com.wms.warehouse.domain.vo.LayoutElementVo;
import com.wms.warehouse.service.LayoutElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库房布局元素管理控制器
 * 提供布局元素CRUD、按库房查询、批量保存等接口
 */
@Tag(name = "库房布局元素管理")
@RestController
@RequestMapping("/warehouse/layout-elements")
@RequiredArgsConstructor
@Validated
public class LayoutElementController {

    private final LayoutElementService layoutElementService;

    /**
     * 按库房ID查询布局元素列表
     * 可选按区域ID筛选
     */
    @Operation(summary = "按库房查询布局元素列表")
    @GetMapping
    @PreAuthorize("hasAuthority('warehouse:layout-element:list')")
    @DataScope
    public R<List<LayoutElementVo>> listByWarehouseId(@RequestParam Long warehouseId,
                                                      @RequestParam(required = false) Long areaId) {
        return R.ok(layoutElementService.listByWarehouseId(warehouseId, areaId));
    }

    /**
     * 新增布局元素
     * 校验参数有效性后调用Service创建
     */
    @Operation(summary = "新增布局元素")
    @PostMapping
    @PreAuthorize("hasAuthority('warehouse:layout-element:add')")
    @OperLog(module = "warehouse", type = "新增", desc = "新增布局元素")
    public R<LayoutElementVo> create(@Valid @RequestBody LayoutElementDto dto) {
        return R.ok(layoutElementService.create(dto));
    }

    /**
     * 更新布局元素
     * 校验参数有效性后调用Service更新
     */
    @Operation(summary = "更新布局元素")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:layout-element:edit')")
    @OperLog(module = "warehouse", type = "更新", desc = "更新布局元素")
    public R<LayoutElementVo> update(@PathVariable Long id, @Valid @RequestBody LayoutElementDto dto) {
        return R.ok(layoutElementService.update(id, dto));
    }

    /**
     * 删除布局元素（逻辑删除）
     */
    @Operation(summary = "删除布局元素")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:layout-element:delete')")
    @OperLog(module = "warehouse", type = "删除", desc = "删除布局元素")
    public R<Void> delete(@PathVariable Long id) {
        layoutElementService.delete(id);
        return R.ok();
    }

    /**
     * 批量保存布局元素
     * 处理新增、更新、删除操作，统计成功和失败数量
     */
    @Operation(summary = "批量保存布局元素")
    @PostMapping("/batch-save")
    @PreAuthorize("hasAuthority('warehouse:layout-element:edit')")
    @OperLog(module = "warehouse", type = "更新", desc = "批量保存布局元素")
    public R<LayoutElementBatchSaveVo> batchSave(@Valid @RequestBody LayoutElementBatchSaveDto dto) {
        return R.ok(layoutElementService.batchSave(dto));
    }
}