package com.wms.report.service;

import com.wms.report.domain.dto.ExportQueryDto;

/**
 * 报表导出服务接口
 * 提供Excel和PDF格式导出功能
 */
public interface ReportExportService {

    /**
     * 导出Excel
     * 根据reportType获取报表数据，使用Apache POI生成Excel
     *
     * @param queryDto 导出参数
     * @return Excel文件字节数组
     */
    byte[] exportExcel(ExportQueryDto queryDto);

    /**
     * 导出PDF
     * 根据reportType获取报表数据，使用iText生成PDF
     *
     * @param queryDto 导出参数
     * @return PDF文件字节数组
     */
    byte[] exportPdf(ExportQueryDto queryDto);
}
