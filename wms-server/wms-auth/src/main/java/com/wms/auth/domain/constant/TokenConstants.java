package com.wms.auth.domain.constant;

/**
 * Token常量类
 * 定义JWT Token相关的常量
 */
public final class TokenConstants {

    private TokenConstants() {
    }

    /** Token类型：Bearer */
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    /** HMAC-SHA256密钥长度（字节） */
    public static final int HMAC_KEY_LENGTH = 32;

    /** Token撤销标记值 */
    public static final String REVOKE_FLAG = "1";

    /** Token前缀截取长度（用于会话标识） */
    public static final int TOKEN_PREFIX_LENGTH = 8;

    /** UserAgent截取长度（会话记录） */
    public static final int SESSION_UA_MAX_LENGTH = 50;

    /** Refresh Token类型标识 */
    public static final String TOKEN_TYPE_REFRESH = "refresh";
}
