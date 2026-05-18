package com.wms.system.controller;

import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysDeptDto;
import com.wms.system.domain.vo.SysDeptVo;
import com.wms.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 * 提供部门CRUD、部门树查询等接口
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/depts")
@RequiredArgsConstructor
@Validated
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 部门列表(树形)
     */
    @Operation(summary = "部门列表(树形)")
    @GetMapping
    @PreAuthorize("hasAuthority('system:dept:list')")
    public R<List<SysDeptVo>> list() {
        return R.ok(sysDeptService.listTree());
    }

    /**
     * 部门树(下拉选择用)
     */
    @Operation(summary = "部门树(下拉选择用)")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public R<List<SysDeptVo>> tree() {
        return R.ok(sysDeptService.tree());
    }

    /**
     * 新增部门
     */
    @Operation(summary = "新增部门")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    @OperLog(module = "system", type = "INSERT", desc = "新增部门")
    public R<SysDeptVo> create(@Valid @RequestBody SysDeptDto dto) {
        return R.ok(sysDeptService.create(dto));
    }

    /**
     * 更新部门
     */
    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新部门")
    public R<SysDeptVo> update(@PathVariable Long id, @Valid @RequestBody SysDeptDto dto) {
        return R.ok(sysDeptService.update(id, dto));
    }

    /**
     * 删除部门
     */
    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    @OperLog(module = "system", type = "DELETE", desc = "删除部门")
    public R<Void> delete(@PathVariable Long id) {
        sysDeptService.delete(id);
        return R.ok();
    }
}
