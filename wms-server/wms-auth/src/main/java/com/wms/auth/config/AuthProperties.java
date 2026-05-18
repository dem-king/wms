package com.wms.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wms.auth")
public class AuthProperties {

    private String jwtSecret;
    private long accessTokenExpire = 7200;
    private long refreshTokenExpire = 604800;
    private long tokenRenewThreshold = 1800;
    private int loginFailThreshold = 5;
    private long lockDuration = 1800;
    private int ipRateLimitThreshold = 10;
    private long captchaExpire = 300;
    private long rsaKeyExpire = 86400;
    private boolean captchaEnabled = true;
}
