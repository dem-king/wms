package com.wms.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenReq {

    @NotBlank(message = "refreshToken不能为空")
    @Schema(description = "刷新Token")
    private String refreshToken;
}
