package com.wms.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 * 记录用户登录成功/失败的审计信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_login_log")
public class AuthLoginLog extends BaseEntity {

    /** 登录用户名 */
    @Schema(description = "登录用户名")
    private String username;

    /** 登录用户ID */
    @Schema(description = "登录用户ID")
    private Long userId;

    /** 登录结果(0-失败 1-成功) */
    @Schema(description = "登录结果(0-失败 1-成功)")
    private Integer loginResult;

    /** 登录IP地址 */
    @Schema(description = "登录IP地址")
    private String loginIp;

    /** 用户代理(浏览器信息) */
    @Schema(description = "用户代理")
    private String userAgent;

    /** 失败原因 */
    @Schema(description = "失败原因")
    private String failReason;

    /** 登录时间 */
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}
