package com.wms.report.exporter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导出列配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportColumnConfig {

    /** 字段名 */
    private String fieldName;

    /** 列标题 */
    private String columnTitle;

    /** 列宽 */
    private int columnWidth;
}
