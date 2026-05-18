package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统配置实体，对应配置表sys_config */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

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
}
