package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 操作日志查询参数
 */
@Data
@Schema(description = "操作日志查询参数")
public class SysOperLogQueryDto {

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型")
    private String type;

    @Schema(description = "操作人(模糊匹配)")
    private String operatorName;
}
