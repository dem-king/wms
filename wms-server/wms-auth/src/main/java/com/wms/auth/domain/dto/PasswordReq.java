package com.wms.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "修改密码请求")
public class PasswordReq {

    @NotBlank(message = "原密码不能为空")
    @Schema(description = "RSA加密原密码")
    private String encryptedOldPassword;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "RSA加密新密码")
    private String encryptedNewPassword;
}
