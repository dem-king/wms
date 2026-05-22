package com.wms.report.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.report.domain.dto.ExportQueryDto;
import com.wms.report.service.ReportExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报表导出控制器
 * 提供Excel和PDF格式导出接口
 */
@Tag(name = "报表导出")
@RestController
@RequestMapping("/report/export")
@RequiredArgsConstructor
@Validated
public class ReportExportController {

    private final ReportExportService reportExportService;

    /**
     * 导出Excel
     */
    @Operation(summary = "导出Excel")
    @PostMapping("/excel")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    @OperLog(module = "report", type = "导出", desc = "导出Excel报表")
    public ResponseEntity<byte[]> exportExcel(@Valid @RequestBody ExportQueryDto queryDto) {
        byte[] data = reportExportService.exportExcel(queryDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    /**
     * 导出PDF
     */
    @Operation(summary = "导出PDF")
    @PostMapping("/pdf")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    @OperLog(module = "report", type = "导出", desc = "导出PDF报表")
    public ResponseEntity<byte[]> exportPdf(@Valid @RequestBody ExportQueryDto queryDto) {
        byte[] data = reportExportService.exportPdf(queryDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
