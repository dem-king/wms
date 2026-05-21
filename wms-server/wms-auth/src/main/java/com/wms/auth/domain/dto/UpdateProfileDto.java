package com.wms.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 当前登录用户资料更新请求
 */
@Data
@Schema(description = "当前登录用户资料更新请求")
public class UpdateProfileDto {

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    @Schema(description = "真实姓名")
    private String realName;

    /** 手机号 */
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 头像地址 */
    @Schema(description = "头像地址")
    private String avatar;
}
