package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysPermissionDto;
import com.wms.system.domain.vo.SysPermissionVo;
import com.wms.system.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 权限管理控制器
 * 提供权限CRUD接口
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/system/permissions")
@RequiredArgsConstructor
@Validated
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    /**
     * 权限列表
     */
    @Operation(summary = "权限列表")
    @GetMapping
    @PreAuthorize("hasAuthority('system:perm:list')")
    @DataScope
    public R<PageResult<SysPermissionVo>> list(@Valid PageParam pageParam,
                                               @RequestParam(required = false) String permName,
                                               @RequestParam(required = false) String permCode) {
        return R.ok(sysPermissionService.page(pageParam, permName, permCode));
    }

    /**
     * 权限详情
     */
    @Operation(summary = "权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:perm:list')")
    @DataScope
    public R<SysPermissionVo> getById(@PathVariable Long id) {
        return R.ok(sysPermissionService.getById(id));
    }

    /**
     * 新增权限
     */
    @Operation(summary = "新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:perm:add')")
    @OperLog(module = "system", type = "INSERT", desc = "新增权限")
    public R<SysPermissionVo> create(@Valid @RequestBody SysPermissionDto dto) {
        return R.ok(sysPermissionService.create(dto));
    }

    /**
     * 更新权限
     */
    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:perm:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新权限")
    public R<SysPermissionVo> update(@PathVariable Long id, @Valid @RequestBody SysPermissionDto dto) {
        return R.ok(sysPermissionService.update(id, dto));
    }

    /**
     * 删除权限
     */
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:perm:delete')")
    @OperLog(module = "system", type = "DELETE", desc = "删除权限")
    public R<Void> delete(@PathVariable Long id) {
        sysPermissionService.delete(id);
        return R.ok();
    }
}
