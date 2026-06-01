package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置新增/编辑请求DTO
 */
@Data
@Schema(description = "系统配置新增/编辑请求")
public class SysConfigDto {

    /** 配置键 */
    @NotBlank(message = "配置键不能为空")
    @Schema(description = "配置键")
    private String configKey;

    /** 配置值 */
    @NotBlank(message = "配置值不能为空")
    @Schema(description = "配置值")
    private String configValue;

    /** 配置名称 */
    @Schema(description = "配置名称")
    private String configName;

    /** 配置分组 */
    @Schema(description = "配置分组")
    private String configGroup;

    /** 配置描述 */
    @Schema(description = "配置描述")
    private String configDesc;
}
