package com.wms.report.service.impl;

import com.wms.common.exception.BizException;
import com.wms.common.util.SecurityUtil;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ExportQueryDto;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.dto.TransferReportQueryDto;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.InboundReportVo;
import com.wms.report.domain.vo.OutboundReportVo;
import com.wms.report.domain.vo.ScrapReportVo;
import com.wms.report.domain.vo.StockReportVo;
import com.wms.report.domain.vo.TransferReportVo;
import com.wms.report.domain.vo.ReturnReportVo;
import com.wms.report.exporter.ExcelExporter;
import com.wms.report.exporter.ExportColumnConfig;
import com.wms.report.exporter.PdfExporter;
import com.wms.report.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 报表导出服务实现类
 * 根据reportType获取报表数据，转换为Map列表，调用ExcelExporter或PdfExporter导出
 */
@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final InboundReportService inboundReportService;
    private final OutboundReportService outboundReportService;
    private final StockReportService stockReportService;
    private final ReturnReportService returnReportService;
    private final ScrapReportService scrapReportService;
    private final TransferReportService transferReportService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 导出Excel
     * 根据reportType获取报表数据，使用Apache POI生成Excel
     *
     * @param queryDto 导出参数
     * @return Excel文件字节数组
     */
    @Override
    public byte[] exportExcel(ExportQueryDto queryDto) {
        String reportType = queryDto.getReportType();
        List<Map<String, Object>> data = getExportData(reportType, queryDto);
        List<ExportColumnConfig> columns = getColumns(reportType);
        String sheetName = getSheetName(reportType);
        return new ExcelExporter().export(data, columns, sheetName);
    }

    /**
     * 导出PDF
     * 根据reportType获取报表数据，使用iText生成PDF
     *
     * @param queryDto 导出参数
     * @return PDF文件字节数组
     */
    @Override
    public byte[] exportPdf(ExportQueryDto queryDto) {
        String reportType = queryDto.getReportType();
        List<Map<String, Object>> data = getExportData(reportType, queryDto);
        List<ExportColumnConfig> columns = getColumns(reportType);
        String title = getSheetName(reportType);
        String filterDesc = buildFilterDesc(queryDto);
        String exportUser = SecurityUtil.getCurrentUsername();
        return new PdfExporter().export(data, columns, title, filterDesc, exportUser);
    }

    /**
     * 获取导出数据(分类汇总列表)
     */
    private List<Map<String, Object>> getExportData(String reportType, ExportQueryDto queryDto) {
        List<CommonReportVo.CategorySummaryItem> summaryList = getCategorySummaryList(reportType, queryDto);
        if (summaryList.size() > ReportConstants.EXPORT_MAX_ROWS) {
            throw new BizException("导出数据量超过上限" + ReportConstants.EXPORT_MAX_ROWS + "条，请缩小查询范围");
        }
        List<Map<String, Object>> data = new ArrayList<>();
        for (CommonReportVo.CategorySummaryItem item : summaryList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("categoryId", item.getCategoryId());
            row.put("categoryName", item.getCategoryName());
            row.put("quantity", item.getQuantity());
            row.put("amount", item.getAmount());
            data.add(row);
        }
        return data;
    }

    /**
     * 获取分类汇总列表
     */
    private List<CommonReportVo.CategorySummaryItem> getCategorySummaryList(String reportType, ExportQueryDto queryDto) {
        return switch (reportType) {
            case ReportConstants.REPORT_TYPE_INBOUND -> inboundReportService.getSummary(toReportQueryDto(queryDto)).getCategorySummaryList();
            case ReportConstants.REPORT_TYPE_OUTBOUND -> outboundReportService.getSummary(toReportQueryDto(queryDto)).getCategorySummaryList();
            case ReportConstants.REPORT_TYPE_STOCK -> stockReportService.getSummary(toReportQueryDto(queryDto)).getCategorySummaryList();
            case ReportConstants.REPORT_TYPE_RETURN -> returnReportService.getSummary(toReportQueryDto(queryDto)).getCategorySummaryList();
            case ReportConstants.REPORT_TYPE_SCRAP -> scrapReportService.getSummary(toReportQueryDto(queryDto)).getCategorySummaryList();
            case ReportConstants.REPORT_TYPE_TRANSFER -> {
                // 调拨报表的汇总结构不同，转为通用CategorySummaryItem
                List<TransferReportVo.TransferCategorySummaryItem> list =
                        transferReportService.getSummary(toTransferQueryDto(queryDto)).getCategorySummaryList();
                yield list.stream().map(t -> {
                    CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
                    item.setCategoryId(t.getCategoryId());
                    item.setCategoryName(t.getCategoryName());
                    item.setQuantity(t.getQuantity());
                    item.setAmount(t.getAmount());
                    return item;
                }).toList();
            }
            default -> throw new BizException("不支持的报表类型: " + reportType);
        };
    }

    /**
     * 获取导出列配置
     */
    private List<ExportColumnConfig> getColumns(String reportType) {
        return List.of(
                new ExportColumnConfig("categoryId", "分类ID", 15),
                new ExportColumnConfig("categoryName", "分类名称", 25),
                new ExportColumnConfig("quantity", "数量", 15),
                new ExportColumnConfig("amount", "金额", 20)
        );
    }

    /**
     * 获取Sheet名称
     */
    private String getSheetName(String reportType) {
        return switch (reportType) {
            case ReportConstants.REPORT_TYPE_INBOUND -> "入库统计报表";
            case ReportConstants.REPORT_TYPE_OUTBOUND -> "出库统计报表";
            case ReportConstants.REPORT_TYPE_STOCK -> "库存统计报表";
            case ReportConstants.REPORT_TYPE_RETURN -> "借还统计报表";
            case ReportConstants.REPORT_TYPE_SCRAP -> "报废统计报表";
            case ReportConstants.REPORT_TYPE_TRANSFER -> "调拨统计报表";
            default -> "报表数据";
        };
    }

    /**
     * 构建筛选条件描述
     */
    private String buildFilterDesc(ExportQueryDto queryDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("查询范围: ").append(queryDto.getStartDate().format(DATE_FMT))
                .append(" ~ ").append(queryDto.getEndDate().format(DATE_FMT));
        if (queryDto.getWarehouseId() != null) {
            sb.append("  库房ID: ").append(queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            sb.append("  分类ID: ").append(queryDto.getCategoryId());
        }
        return sb.toString();
    }

    /**
     * ExportQueryDto转ReportQueryDto
     */
    private ReportQueryDto toReportQueryDto(ExportQueryDto dto) {
        ReportQueryDto queryDto = new ReportQueryDto();
        queryDto.setStartDate(dto.getStartDate());
        queryDto.setEndDate(dto.getEndDate());
        queryDto.setWarehouseId(dto.getWarehouseId());
        queryDto.setCategoryId(dto.getCategoryId());
        return queryDto;
    }

    /**
     * ExportQueryDto转TransferReportQueryDto
     */
    private TransferReportQueryDto toTransferQueryDto(ExportQueryDto dto) {
        TransferReportQueryDto queryDto = new TransferReportQueryDto();
        queryDto.setStartDate(dto.getStartDate());
        queryDto.setEndDate(dto.getEndDate());
        queryDto.setCategoryId(dto.getCategoryId());
        return queryDto;
    }
}
