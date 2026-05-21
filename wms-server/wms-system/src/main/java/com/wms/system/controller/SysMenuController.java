package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysMenuDto;
import com.wms.system.domain.vo.MenuTreeVo;
import com.wms.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * 提供菜单CRUD、菜单树查询、用户菜单树等接口
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menus")
@RequiredArgsConstructor
@Validated
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 获取菜单列表(树形)
     */
    @Operation(summary = "菜单列表(树形)")
    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:list')")
    @DataScope
    public R<List<MenuTreeVo>> list() {
        return R.ok(sysMenuService.buildMenuTree());
    }

    /**
     * 获取菜单详情
     */
    @Operation(summary = "菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @DataScope
    public R<MenuTreeVo> getById(@PathVariable Long id) {
        return R.ok(sysMenuService.getById(id));
    }

    /**
     * 新增菜单
     */
    @Operation(summary = "新增菜单")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    @OperLog(module = "system", type = "INSERT", desc = "新增菜单")
    public R<MenuTreeVo> create(@Valid @RequestBody SysMenuDto dto) {
        return R.ok(sysMenuService.create(dto));
    }

    /**
     * 更新菜单
     */
    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新菜单")
    public R<MenuTreeVo> update(@PathVariable Long id, @Valid @RequestBody SysMenuDto dto) {
        return R.ok(sysMenuService.update(id, dto));
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @OperLog(module = "system", type = "DELETE", desc = "删除菜单")
    public R<Void> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return R.ok();
    }

    /**
     * 获取菜单树(角色分配菜单用)
     * 返回全部菜单树，不含权限控制
     */
    @Operation(summary = "菜单树(角色分配用)")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @DataScope
    public R<List<MenuTreeVo>> tree() {
        return R.ok(sysMenuService.buildMenuTree());
    }

    /**
     * 获取当前登录用户的菜单树
     * 用于前端动态路由和侧边栏渲染
     */
    @Operation(summary = "当前用户菜单树")
    @GetMapping("/userMenuTree")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<MenuTreeVo>> userMenuTree() {
        return R.ok(sysMenuService.getUserMenuTree());
    }
}
