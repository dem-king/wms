package com.wms.warehouse.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.warehouse.domain.dto.BinDto;
import com.wms.warehouse.domain.vo.BinVo;
import com.wms.warehouse.service.BinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库位管理控制器
 * 提供库位CRUD、按存放柜查询、批量生成库位等接口
 */
@Tag(name = "库位管理")
@RestController
@RequestMapping("/warehouse/bins")
@RequiredArgsConstructor
@Validated
public class BinController {

    private final BinService binService;

    /**
     * 按存放柜ID分页查询库位列表
     */
    @Operation(summary = "按存放柜分页查询库位列表")
    @GetMapping
    @PreAuthorize("hasAuthority('warehouse:bin:list')")
    @DataScope
    public R<PageResult<BinVo>> page(PageParam pageParam, @RequestParam Long cabinetId) {
        return R.ok(binService.page(pageParam, cabinetId));
    }

    /**
     * 按存放柜ID查询库位列表
     */
    @Operation(summary = "按存放柜查询库位列表")
    @GetMapping("/cabinet/{cabinetId}")
    @PreAuthorize("hasAuthority('warehouse:bin:list')")
    @DataScope
    public R<List<BinVo>> listByCabinetId(@PathVariable Long cabinetId) {
        return R.ok(binService.listByCabinetId(cabinetId));
    }

    /**
     * 新增库位
     */
    @Operation(summary = "新增库位")
    @PostMapping
    @PreAuthorize("hasAuthority('warehouse:bin:add')")
    @OperLog(module = "warehouse", type = "新增", desc = "新增库位")
    public R<BinVo> create(@Valid @RequestBody BinDto dto) {
        return R.ok(binService.create(dto));
    }

    /**
     * 更新库位
     */
    @Operation(summary = "更新库位")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:bin:edit')")
    @OperLog(module = "warehouse", type = "更新", desc = "更新库位")
    public R<BinVo> update(@PathVariable Long id, @Valid @RequestBody BinDto dto) {
        return R.ok(binService.update(id, dto));
    }

    /**
     * 删除库位
     */
    @Operation(summary = "删除库位")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('warehouse:bin:delete')")
    @OperLog(module = "warehouse", type = "删除", desc = "删除库位")
    public R<Void> delete(@PathVariable Long id) {
        binService.delete(id);
        return R.ok();
    }

    /**
     * 批量生成库位
     */
    @Operation(summary = "批量生成库位")
    @PostMapping("/cabinet/{cabinetId}/batch")
    @PreAuthorize("hasAuthority('warehouse:bin:batch')")
    @OperLog(module = "warehouse", type = "新增", desc = "批量生成库位")
    public R<List<BinVo>> batchCreate(@PathVariable Long cabinetId,
                                      @RequestParam @Min(1) Integer rows,
                                      @RequestParam @Min(1) Integer cols) {
        return R.ok(binService.batchCreate(cabinetId, rows, cols));
    }
}
