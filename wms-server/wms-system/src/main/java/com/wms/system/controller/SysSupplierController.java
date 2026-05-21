package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysSupplierDto;
import com.wms.system.domain.vo.SysSupplierVo;
import com.wms.system.service.SysSupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商管理控制器
 * 提供供应商CRUD接口
 */
@Tag(name = "供应商管理")
@RestController
@RequestMapping("/system/suppliers")
@RequiredArgsConstructor
@Validated
public class SysSupplierController {

    private final SysSupplierService sysSupplierService;

    /**
     * 供应商列表(分页)
     */
    @Operation(summary = "供应商列表(分页)")
    @GetMapping
    @PreAuthorize("hasAuthority('system:supplier:list')")
    @DataScope
    public R<PageResult<SysSupplierVo>> page(PageParam pageParam,
                                              @RequestParam(required = false) String supplierName,
                                              @RequestParam(required = false) String supplierCode) {
        return R.ok(sysSupplierService.page(pageParam, supplierName, supplierCode));
    }

    /**
     * 供应商详情
     */
    @Operation(summary = "供应商详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:supplier:list')")
    @DataScope
    public R<SysSupplierVo> getById(@PathVariable Long id) {
        return R.ok(sysSupplierService.getById(id));
    }

    /**
     * 新增供应商
     */
    @Operation(summary = "新增供应商")
    @PostMapping
    @PreAuthorize("hasAuthority('system:supplier:add')")
    @OperLog(module = "system", type = "INSERT", desc = "新增供应商")
    public R<SysSupplierVo> create(@Valid @RequestBody SysSupplierDto dto) {
        return R.ok(sysSupplierService.create(dto));
    }

    /**
     * 更新供应商
     */
    @Operation(summary = "更新供应商")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:supplier:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新供应商")
    public R<SysSupplierVo> update(@PathVariable Long id, @Valid @RequestBody SysSupplierDto dto) {
        return R.ok(sysSupplierService.update(id, dto));
    }

    /**
     * 删除供应商
     */
    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:supplier:delete')")
    @OperLog(module = "system", type = "DELETE", desc = "删除供应商")
    public R<Void> delete(@PathVariable Long id) {
        sysSupplierService.delete(id);
        return R.ok();
    }
}
