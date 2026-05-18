package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "RSA公钥响应")
public class RsaKeyPairResp {

    @Schema(description = "RSA公钥")
    private String publicKey;

    @Schema(description = "密钥ID")
    private String keyId;
}
