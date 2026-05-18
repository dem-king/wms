package com.wms.auth.enums;

import lombok.Getter;

@Getter
public enum AuthErrorCode {

    CREDENTIAL_INVALID(401, "用户名或密码错误"),
    ACCOUNT_DISABLED(403, "账号已被禁用"),
    ACCOUNT_LOCKED(423, "账号已被锁定，请稍后再试"),
    CAPTCHA_INVALID(400, "验证码无效或已过期"),
    CAPTCHA_MISMATCH(400, "验证码错误"),
    TOKEN_INVALID(401, "Token无效"),
    TOKEN_EXPIRED(401, "Token已过期"),
    TOKEN_REVOKED(401, "Token已被撤销"),
    DECRYPT_ERROR(400, "密码解密失败"),
    RSA_KEY_ERROR(400, "RSA密钥获取失败"),
    IP_RATE_LIMITED(429, "请求过于频繁，请稍后再试"),
    PASSWORD_STRENGTH_FAIL(400, "密码强度不足，需8-20位含大小写字母和数字"),
    PASSWORD_SAME(400, "新密码不能与原密码相同"),
    PERMISSION_DENIED(403, "无操作权限");

    private final int code;
    private final String msg;

    AuthErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
