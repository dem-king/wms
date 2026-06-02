package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 站内信查询参数。
 */
@Data
@Schema(description = "站内信查询参数")
public class SysMessageQueryDto {

    /** 读取状态(0-未读 1-已读) */
    @Schema(description = "读取状态(0-未读 1-已读)")
    private Integer readStatus;
}
