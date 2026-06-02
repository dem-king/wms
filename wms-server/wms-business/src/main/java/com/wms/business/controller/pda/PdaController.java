package com.wms.business.controller.pda;

import com.wms.business.domain.dto.pda.RfidBatchReadDto;
import com.wms.business.domain.dto.pda.StockCheckDto;
import com.wms.business.domain.vo.pda.PdaTaskVo;
import com.wms.business.domain.vo.pda.RfidBatchReadResultVo;
import com.wms.business.domain.vo.pda.StockCheckResultVo;
import com.wms.business.service.pda.PdaService;
import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * PDA专用接口控制器
 * 提供RFID批量读取上报、盘点结果提交、待办任务统计等PDA专用接口
 */
@Tag(name = "PDA专用接口")
@RestController
@RequestMapping("/pda")
@RequiredArgsConstructor
@Validated
public class PdaController {

    private final PdaService pdaService;

    /**
     * RFID批量读取上报
     * PDA端完成RFID批量读取后，将EPC码列表上报至后端进行差异对比
     */
    @Operation(summary = "RFID批量读取上报")
    @PostMapping("/rfid/batch-read")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "pda", type = "上报", desc = "RFID批量读取上报")
    public R<RfidBatchReadResultVo> rfidBatchRead(@Valid @RequestBody RfidBatchReadDto dto) {
        return R.ok(pdaService.rfidBatchRead(dto));
    }

    /**
     * 盘点结果提交
     * PDA端确认盘点差异后，提交盘点结果至后端保存
     */
    @Operation(summary = "盘点结果提交")
    @PostMapping("/stock/check")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "pda", type = "提交", desc = "盘点结果提交")
    public R<StockCheckResultVo> stockCheck(@Valid @RequestBody StockCheckDto dto) {
        return R.ok(pdaService.stockCheck(dto));
    }

    /**
     * PDA待办任务统计
     * 查询当前用户的待处理任务数量统计
     */
    @Operation(summary = "PDA待办任务统计")
    @GetMapping("/tasks")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PdaTaskVo> getTasks() {
        return R.ok(pdaService.getTasks());
    }
}