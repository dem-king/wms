package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysOperLogQueryDto;
import com.wms.system.domain.vo.SysOperLogVo;
import com.wms.system.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 * 提供操作日志分页查询接口(只读，禁止修改/删除)
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/system/oper-log")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    /**
     * 操作日志分页查询
     */
    @Operation(summary = "操作日志分页查询")
    @GetMapping
    @DataScope
    @PreAuthorize("hasAuthority('system:oper-log:list')")
    public R<PageResult<SysOperLogVo>> page(PageParam pageParam,
                                            @ModelAttribute SysOperLogQueryDto queryDto) {
        return R.ok(sysOperLogService.page(pageParam, queryDto));
    }
}
