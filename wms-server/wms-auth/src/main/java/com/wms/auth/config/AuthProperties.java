package com.wms.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wms.auth")
public class AuthProperties {

    /** JWT 密钥 */
    private String jwtSecret;
    /** 访问令牌过期时间（秒） */
    private long accessTokenExpire = 7200;
    /** 刷新令牌过期时间（秒） */
    private long refreshTokenExpire = 604800;
    /** 令牌续签阈值（秒） */
    private long tokenRenewThreshold = 1800;
    /** 登录失败阈值 */
    private int loginFailThreshold = 5;
    /** 登录锁定时长（秒） */
    private long lockDuration = 1800;
    /** IP 限流阈值 */
    private int ipRateLimitThreshold = 10;
    /** RSA 密钥有效期（秒） */
    private long rsaKeyExpire = 86400;
    /** 是否启用验证码 */
    private boolean captchaEnabled = true;
    /** 验证码过期时间（秒） */
    private long captchaExpire = 120;
    /** 验证码接口限流阈值 */
    private int captchaRateLimitThreshold = 20;
    /** 验证码轨迹容差 */
    private int captchaTrackTolerance = 5;

    /** 头像最大大小（字节） */
    private long avatarMaxSizeBytes = 2 * 1024 * 1024L;
}
