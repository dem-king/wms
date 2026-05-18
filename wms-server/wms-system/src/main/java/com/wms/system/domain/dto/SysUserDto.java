package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 用户新增/编辑DTO
 */
@Data
@Schema(description = "用户新增/编辑请求")
public class SysUserDto {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    /** 密码(新增时必填，编辑时选填) */
    @Schema(description = "密码(新增时必填，编辑时选填)")
    private String password;

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
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

    /** 角色ID列表 */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
