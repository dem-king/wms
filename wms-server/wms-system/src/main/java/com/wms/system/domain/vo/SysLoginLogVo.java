package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志视图对象
 */
@Data
@Schema(description = "登录日志信息")
public class SysLoginLogVo {

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 登录IP */
    @Schema(description = "登录IP")
    private String loginIp;

    /** 登录地点 */
    @Schema(description = "登录地点")
    private String loginLocation;

    /** 浏览器 */
    @Schema(description = "浏览器")
    private String browser;

    /** 操作系统 */
    @Schema(description = "操作系统")
    private String os;

    /** 登录状态 */
    @Schema(description = "登录状态")
    private String status;

    /** 失败原因 */
    @Schema(description = "失败原因")
    private String failReason;

    /** 登录时间 */
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
