package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置视图对象
 * 用于返回系统配置信息给前端
 */
@Data
@Schema(description = "系统配置视图对象")
public class SysConfigVo {

    /** 配置ID */
    @Schema(description = "配置ID")
    private Long id;

    /** 配置键 */
    @Schema(description = "配置键")
    private String configKey;

    /** 配置值 */
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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
