package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 * 对应表 sys_login_log，记录用户登录/登出日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_log")
public class SysLoginLog extends BaseEntity {

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

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

    /** 登录状态: SUCCESS/FAIL/LOGOUT */
    @Schema(description = "登录状态")
    @TableField("`status`")
    private String status;

    /** 失败原因 */
    @Schema(description = "失败原因")
    private String failReason;

    /** 登录时间 */
    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}
