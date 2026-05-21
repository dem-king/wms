package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 最近登录信息视图对象
 */
@Data
@Schema(description = "最近登录信息")
public class LastLoginInfoVo {

    /** 最近登录时间 */
    @Schema(description = "最近登录时间")
    private LocalDateTime loginTime;

    /** 最近登录IP */
    @Schema(description = "最近登录IP")
    private String loginIp;
}
