package com.wms.monitor.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.monitor.domain.vo.OverdueReturnVo;
import com.wms.monitor.service.OverdueReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 逾期归还控制器
 * 提供逾期归还记录分页查询接口
 */
@Tag(name = "逾期归还")
@RestController
@RequestMapping("/monitor/overdue-return")
@RequiredArgsConstructor
@Validated
public class OverdueReturnController {

    private final OverdueReturnService overdueReturnService;

    /**
     * 逾期归还记录分页查询
     */
    @Operation(summary = "逾期归还记录分页查询")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<OverdueReturnVo>> page(PageParam pageParam,
                                               @RequestParam(required = false) String alertLevel,
                                               @RequestParam(required = false) String status) {
        return R.ok(overdueReturnService.page(pageParam, alertLevel, status));
    }
}
