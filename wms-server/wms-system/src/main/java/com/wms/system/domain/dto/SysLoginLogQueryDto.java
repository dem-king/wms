package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 登录日志查询参数
 */
@Data
@Schema(description = "登录日志查询参数")
public class SysLoginLogQueryDto {

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "用户名(模糊匹配)")
    private String username;

    @Schema(description = "登录状态")
    private String status;
}
