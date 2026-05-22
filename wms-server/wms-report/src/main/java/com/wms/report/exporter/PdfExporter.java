package com.wms.report.exporter;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * PDF导出器
 * 使用iText 7创建PdfDocument+Table
 */
public class PdfExporter {

    /** 中文字体路径(系统默认宋体) */
    private static final String FONT_PATH = "C:/Windows/Fonts/simsun.ttc,0";

    /**
     * 导出数据为PDF字节数组
     *
     * @param data       数据列表
     * @param columns    列配置列表
     * @param title      报表标题
     * @param filterDesc 筛选条件描述
     * @param exportUser 导出人
     * @return PDF文件字节数组
     */
    public byte[] export(List<Map<String, Object>> data, List<ExportColumnConfig> columns,
                         String title, String filterDesc, String exportUser) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.IDENTITY_H);

            // 写入标题
            document.add(new Paragraph(title).setFont(font).setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // 写入筛选条件
            if (filterDesc != null && !filterDesc.isEmpty()) {
                document.add(new Paragraph(filterDesc).setFont(font).setFontSize(10));
            }

            // 写入导出信息
            String exportInfo = "导出人: " + exportUser + "  导出时间: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            document.add(new Paragraph(exportInfo).setFont(font).setFontSize(10));
            document.add(new Paragraph("\n"));

            // 创建表格
            float[] widths = new float[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                widths[i] = columns.get(i).getColumnWidth();
            }
            Table table = new Table(UnitValue.createPointArray(widths));
            table.useAllAvailableWidth();

            // 写入表头
            for (ExportColumnConfig column : columns) {
                table.addHeaderCell(new Cell().add(new Paragraph(column.getColumnTitle()).setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            // 写入数据行
            for (Map<String, Object> rowData : data) {
                for (ExportColumnConfig column : columns) {
                    Object value = rowData.get(column.getFieldName());
                    String text = value != null ? value.toString() : "";
                    table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(9)));
                }
            }

            document.add(table);
            document.close();
        } catch (IOException e) {
            throw new RuntimeException("PDF导出失败", e);
        }
        return out.toByteArray();
    }
}
