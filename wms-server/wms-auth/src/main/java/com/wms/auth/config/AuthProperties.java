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
    private long rsaKeyExpire = 86400;
    private boolean captchaEnabled = true;
    /** 滑块验证码过期时间（秒，与 tianai 内置 TTL 对齐） */
    private long captchaExpire = 120;
    /** 滑块验证码接口 IP 限流阈值（60 秒窗口） */
    private int captchaRateLimitThreshold = 20;
    /** 滑块拼图坐标容差（px） */
    private int captchaTrackTolerance = 5;
    private long avatarMaxSizeBytes = 2 * 1024 * 1024L;
}
