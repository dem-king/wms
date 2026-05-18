package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Token响应")
public class TokenResp {

    @Schema(description = "访问Token")
    private String accessToken;

    @Schema(description = "刷新Token")
    private String refreshToken;

    @Schema(description = "Token类型")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间(秒)")
    private Long expiresIn;
}
