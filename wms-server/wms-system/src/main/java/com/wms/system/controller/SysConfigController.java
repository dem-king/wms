package com.wms.system.controller;

import com.wms.common.annotation.OperLog;
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

import java.util.List;

/**
 * 系统配置控制器
 * 提供配置列表查询、按key查询、更新配置等接口
 */
@Tag(name = "系统配置")
@RestController
@RequestMapping("/system/configs")
@RequiredArgsConstructor
@Validated
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /**
     * 配置列表
     */
    @Operation(summary = "配置列表")
    @GetMapping
    @PreAuthorize("hasAuthority('system:config:list')")
    public R<List<SysConfigVo>> list() {
        return R.ok(sysConfigService.listAll());
    }

    /**
     * 按key查询配置值
     */
    @Operation(summary = "按key查询配置值")
    @GetMapping("/{key}")
    @PreAuthorize("hasAuthority('system:config:list')")
    public R<SysConfigVo> getByKey(@PathVariable String key) {
        return R.ok(sysConfigService.getByKey(key));
    }

    /**
     * 更新配置
     */
    @Operation(summary = "更新配置")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:edit')")
    @OperLog(module = "system", type = "UPDATE", desc = "更新系统配置")
    public R<SysConfigVo> update(@PathVariable Long id, @Valid @RequestBody SysConfigDto dto) {
        return R.ok(sysConfigService.update(id, dto));
    }
}
