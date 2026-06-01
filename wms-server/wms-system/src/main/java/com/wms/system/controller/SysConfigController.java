package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysConfigDto;
import com.wms.system.domain.vo.SysConfigVo;
import com.wms.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器
 * 提供配置分页查询、按key查询、新增、更新、删除等接口
 */
@Tag(name = "系统配置")
@RestController
@RequestMapping("/system/configs")
@RequiredArgsConstructor
@Validated
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /** 配置分页列表 */
    @Operation(summary = "配置分页列表")
    @GetMapping
    @PreAuthorize("hasAuthority('system:config:list')")
    @DataScope
    public R<PageResult<SysConfigVo>> page(PageParam pageParam,
                                           @RequestParam(required = false) String configKey,
                                           @RequestParam(required = false) String configGroup) {
        return R.ok(sysConfigService.page(pageParam, configKey, configGroup));
    }

    /** 按key查询配置值 */
    @Operation(summary = "按key查询配置值")
    @GetMapping("/key/{key}")
    @PreAuthorize("hasAuthority('system:config:list')")
    @DataScope
    public R<SysConfigVo> getByKey(@PathVariable String key) {
        return R.ok(sysConfigService.getByKey(key));
    }

    /** 新增配置 */
    @Operation(summary = "新增配置")
    @PostMapping
    @PreAuthorize("hasAuthority('system:config:add')")
    @OperLog(module = "system", type = "新增", desc = "新增系统配置")
    public R<SysConfigVo> create(@Valid @RequestBody SysConfigDto dto) {
        return R.ok(sysConfigService.create(dto));
    }

    /** 更新配置 */
    @Operation(summary = "更新配置")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:edit')")
    @OperLog(module = "system", type = "更新", desc = "更新系统配置")
    public R<SysConfigVo> update(@PathVariable Long id, @Valid @RequestBody SysConfigDto dto) {
        return R.ok(sysConfigService.update(id, dto));
    }

    /** 删除配置 */
    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:delete')")
    @OperLog(module = "system", type = "删除", desc = "删除系统配置")
    public R<Void> delete(@PathVariable Long id) {
        sysConfigService.delete(id);
        return R.ok();
    }
}
