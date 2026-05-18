package com.wms.auth.constant;

public class AuthRedisKey {

    public static final String TOKEN_PREFIX = "auth:token:";
    public static final String SESSION_PREFIX = "auth:session:";
    public static final String REFRESH_PREFIX = "auth:refresh:";
    public static final String PERM_PREFIX = "auth:perm:";
    public static final String CAPTCHA_PREFIX = "auth:captcha:";
    public static final String RSA_PRIVATE_KEY = "auth:rsa:private";
    public static final String RSA_PUBLIC_KEY = "auth:rsa:public";
    public static final String RSA_KEY_ID = "auth:rsa:keyId";
    public static final String LOCK_FAIL_PREFIX = "auth:lock:fail:";
    public static final String LOCK_STATE_PREFIX = "auth:lock:state:";
    public static final String RATE_LIMIT_PREFIX = "auth:ratelimit:";

    private AuthRedisKey() {
    }
}
