package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "验证码响应")
public class CaptchaResp {

    @Schema(description = "验证码Key")
    private String captchaKey;

    @Schema(description = "Base64验证码图片")
    private String captchaImage;
}
