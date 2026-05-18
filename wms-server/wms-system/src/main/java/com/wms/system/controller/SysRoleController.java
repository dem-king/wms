package com.wms.system.controller;

import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysRoleDto;
import com.wms.system.domain.vo.SysRoleVo;
import com.wms.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * 提供角色CRUD、角色菜单/权限分配等接口
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/roles")
@RequiredArgsConstructor
@Validated
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 角色列表
     */
    @Operation(summary = "角色列表")
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public R<List<SysRoleVo>> list() {
        return R.ok(sysRoleService.listAll().stream().map(role -> {
            SysRoleVo vo = new SysRoleVo();
            vo.setId(role.getId());
            vo.setRoleName(role.getRoleName());
            vo.setRoleCode(role.getRoleCode());
            vo.setRoleDesc(role.getRoleDesc());
            vo.setDataScope(role.getDataScope());
            vo.setStatus(role.getStatus());
            vo.setCreateTime(role.getCreateTime());
            return vo;
        }).toList());
    }

    /**
     * 角色详情
     */
    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public R<SysRoleVo> getById(@PathVariable Long id) {
        SysRoleVo vo = new SysRoleVo();
        var role = sysRoleService.getById(id);
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleDesc(role.getRoleDesc());
        vo.setDataScope(role.getDataScope());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        return R.ok(vo);
    }

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    @OperLog(module = "system", type = "INSERT", desc = "新增角色")
    public R<SysRoleVo> create(@Valid @RequestBody SysRoleDto dto) {
        return R.ok(sysRoleService.create(dto));
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新角色")
    public R<SysRoleVo> update(@PathVariable Long id, @Valid @RequestBody SysRoleDto dto) {
        return R.ok(sysRoleService.update(id, dto));
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @OperLog(module = "system", type = "DELETE", desc = "删除角色")
    public R<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return R.ok();
    }

    /**
     * 查询角色菜单
     */
    @Operation(summary = "查询角色菜单")
    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:list')")
    public R<List<Long>> getRoleMenus(@PathVariable Long id) {
        return R.ok(sysRoleService.getRoleMenus(id));
    }

    /**
     * 分配角色菜单
     */
    @Operation(summary = "分配角色菜单")
    @PutMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "分配角色菜单")
    public R<Void> assignRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignRoleMenus(id, menuIds);
        return R.ok();
    }

    /**
     * 查询角色权限
     */
    @Operation(summary = "查询角色权限")
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:list')")
    public R<List<Long>> getRolePermissions(@PathVariable Long id) {
        return R.ok(sysRoleService.getRolePermissions(id));
    }

    /**
     * 分配角色权限
     */
    @Operation(summary = "分配角色权限")
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "分配角色权限")
    public R<Void> assignRolePermissions(@PathVariable Long id, @RequestBody List<Long> permIds) {
        sysRoleService.assignRolePermissions(id, permIds);
        return R.ok();
    }
}
