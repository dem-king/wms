package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统用户实体，对应用户表sys_user */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 密码(BCrypt加密) */
    @Schema(description = "密码(BCrypt加密)")
    private String password;

    /** 真实姓名 */
    @Schema(description = "真实姓名")
    private String realName;

    /** 部门ID */
    @Schema(description = "部门ID")
    private Long deptId;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatar;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;
}
