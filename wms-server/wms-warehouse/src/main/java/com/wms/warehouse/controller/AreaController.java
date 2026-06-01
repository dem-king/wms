package com.wms.warehouse.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.warehouse.domain.dto.AreaDto;
import com.wms.warehouse.domain.vo.AreaVo;
import com.wms.warehouse.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 存放区域管理控制器
 * 提供区域CRUD、按库房查询等接口
 */
@Tag(name = "存放区域管理")
@RestController
@RequestMapping("/warehouse/areas")
@RequiredArgsConstructor
@Validated
public class AreaController {

    private final AreaService areaService;

    /**
     * 按库房ID分页查询区域列表
     */
    @Operation(summary = "按库房分页查询区域列表")
    @GetMapping
    @PreAuthorize("hasAuthority('warehouse:area:list')")
    @DataScope
    public R<PageResult<AreaVo>> page(PageParam pageParam, @RequestParam Long warehouseId) {
        return R.ok(areaService.page(pageParam, warehouseId));
    }

    /**
     * 按库房ID查询区域列表
     */
    @Operation(summary = "按库房查询区域列表")
    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasAuthority('warehouse:area:list')")
    @DataScope
    public R<List<AreaVo>> listByWarehouseId(@PathVariable Long warehouseId) {
        return R.ok(areaService.listByWarehouseId(warehouseId));
    }

    /**
     * 新增区域
     */
    @Operation(summary = "新增区域")
    @PostMapping
    @PreAuthorize("hasAuthority('warehouse:area:add')")
    @OperLog(module = "warehouse", type = "新增", desc = "新增区域")
    public R<AreaVo> create(@Valid @RequestBody AreaDto dto) {
        return R.ok(areaService.create(dto));
    }

    /**
     * 更新区域
     */
    @Operation(summary = "更新区域")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:area:edit')")
    @OperLog(module = "warehouse", type = "更新", desc = "更新区域")
    public R<AreaVo> update(@PathVariable Long id, @Valid @RequestBody AreaDto dto) {
        return R.ok(areaService.update(id, dto));
    }

    /**
     * 删除区域
     */
    @Operation(summary = "删除区域")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:area:delete')")
    @OperLog(module = "warehouse", type = "删除", desc = "删除区域")
    public R<Void> delete(@PathVariable Long id) {
        areaService.delete(id);
        return R.ok();
    }
}
