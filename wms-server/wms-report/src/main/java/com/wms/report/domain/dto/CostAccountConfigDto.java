package com.wms.report.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 费用核算配置参数
 */
@Data
@Schema(description = "费用核算配置参数")
public class CostAccountConfigDto {

    /** 启用开关 */
    @NotNull(message = "启用开关不能为空")
    @Schema(description = "启用开关")
    private Boolean enabled;

    /** 核算年度(2020-2099) */
    @NotNull(message = "核算年度不能为空")
    @Min(2020)
    @Max(2099)
    @Schema(description = "核算年度")
    private Integer year;

    /** 核算周期: MONTHLY/QUARTERLY/YEARLY */
    @NotNull(message = "核算周期不能为空")
    @Schema(description = "核算周期")
    private String period;
}
