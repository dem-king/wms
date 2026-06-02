package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysUserDto;
import com.wms.system.domain.vo.SysUserVo;
import com.wms.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理控制器。
 * 提供用户CRUD、密码重置、状态切换、角色分配等接口。
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/users")
@RequiredArgsConstructor
@Validated
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 用户列表(分页)。
     */
    @Operation(summary = "用户列表(分页)")
    @GetMapping
    @PreAuthorize("hasAuthority('system:user:list')")
    @DataScope
    public R<PageResult<SysUserVo>> page(PageParam pageParam,
                                         @RequestParam(required = false) String username,
                                         @RequestParam(required = false) String realName,
                                         @RequestParam(required = false) Integer status) {
        return R.ok(sysUserService.page(pageParam, username, realName, status));
    }

    /**
     * 按用户名精确查询用户。
     */
    @Operation(summary = "按用户名精确查询用户")
    @GetMapping("/by-username")
    @PreAuthorize("hasAnyAuthority('system:user:list','approval:config:add','approval:config:edit')")
    @DataScope
    public R<SysUserVo> getByUsername(@RequestParam String username) {
        return R.ok(sysUserService.getVoByUsernameExact(username));
    }

    /**
     * 用户详情。
     */
    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:list')")
    @DataScope
    public R<SysUserVo> getById(@PathVariable Long id) {
        return R.ok(sysUserService.getById(id));
    }

    /**
     * 新增用户。
     */
    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    @OperLog(module = "system", type = "INSERT", desc = "新增用户")
    public R<SysUserVo> create(@Valid @RequestBody SysUserDto dto) {
        return R.ok(sysUserService.create(dto));
    }

    /**
     * 更新用户。
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新用户")
    public R<SysUserVo> update(@PathVariable Long id, @Valid @RequestBody SysUserDto dto) {
        return R.ok(sysUserService.update(id, dto));
    }

    /**
     * 删除用户(逻辑删除)。
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @OperLog(module = "system", type = "DELETE", desc = "删除用户")
    public R<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return R.ok();
    }

    /**
     * 重置用户密码。
     */
    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/resetPwd")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @OperLog(module = "system", type = "UPDATE", desc = "重置用户密码")
    public R<Void> resetPwd(@PathVariable Long id) {
        sysUserService.resetPwd(id);
        return R.ok();
    }

    /**
     * 切换用户状态。
     */
    @Operation(summary = "切换用户状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "切换用户状态")
    public R<Void> changeStatus(@PathVariable Long id) {
        sysUserService.changeStatus(id);
        return R.ok();
    }

    /**
     * 查询用户角色。
     */
    @Operation(summary = "查询用户角色")
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:list')")
    @DataScope
    public R<List<Long>> getUserRoles(@PathVariable Long id) {
        return R.ok(sysUserService.getUserRoles(id));
    }

    /**
     * 分配用户角色。
     */
    @Operation(summary = "分配用户角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "分配用户角色")
    public R<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(id, roleIds);
        return R.ok();
    }
}
