package com.wms.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录请求
 * 携带用户名、RSA 加密密码、滑块拼图 Token 与轨迹
 *
 * @author wms-team
 * @since 1.0
 */
@Data
@Schema(description = "登录请求")
public class LoginReq {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度2-50位")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "RSA加密密码")
    private String encryptedPassword;

    @NotBlank(message = "请完成滑块验证")
    @Schema(description = "滑块拼图Token")
    private String captchaToken;

    @NotBlank(message = "请完成滑块验证")
    @Schema(description = "滑块拖动轨迹数据（前端采集后加密/编码的 JSON 字符串）")
    private String captchaTrack;
}
