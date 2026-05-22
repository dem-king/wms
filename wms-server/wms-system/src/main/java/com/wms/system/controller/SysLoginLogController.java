package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysLoginLogQueryDto;
import com.wms.system.domain.vo.SysLoginLogVo;
import com.wms.system.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志控制器
 * 提供登录日志分页查询接口(只读，禁止修改/删除)
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/system/login-log")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final SysLoginLogService sysLoginLogService;

    /**
     * 登录日志分页查询
     */
    @Operation(summary = "登录日志分页查询")
    @GetMapping
    @DataScope
    @PreAuthorize("hasAuthority('system:login-log:list')")
    public R<PageResult<SysLoginLogVo>> page(PageParam pageParam,
                                             @ModelAttribute SysLoginLogQueryDto queryDto) {
        return R.ok(sysLoginLogService.page(pageParam, queryDto));
    }
}
