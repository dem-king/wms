package com.wms.report.exporter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Excel导出器
 * 使用Apache POI SXSSFWorkbook流式写入，支持大数据量导出
 */
public class ExcelExporter {

    /**
     * 导出数据为Excel字节数组
     *
     * @param data     数据列表，每行一个Map
     * @param columns  列配置列表
     * @param sheetName Sheet名称
     * @return Excel文件字节数组
     */
    public byte[] export(List<Map<String, Object>> data, List<ExportColumnConfig> columns, String sheetName) {
        // 使用SXSSFWorkbook流式写入，窗口大小100行，降低内存占用
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            SXSSFSheet sheet = workbook.createSheet(sheetName);

            // 创建标题行样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 写入标题行
            SXSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                SXSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i).getColumnTitle());
                cell.setCellStyle(headerStyle);
                // 设置列宽(单位:1/256字符宽)
                sheet.setColumnWidth(i, columns.get(i).getColumnWidth() * 256);
            }

            // 写入数据行
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                SXSSFRow row = sheet.createRow(rowIndex + 1);
                Map<String, Object> rowData = data.get(rowIndex);
                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                    SXSSFCell cell = row.createCell(colIndex);
                    Object value = rowData.get(columns.get(colIndex).getFieldName());
                    setCellValue(cell, value);
                }
            }

            // 写出到字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 创建标题行样式
     */
    private CellStyle createHeaderStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 设置单元格值
     */
    private void setCellValue(SXSSFCell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
