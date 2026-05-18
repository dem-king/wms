package com.wms.auth.enums;

import lombok.Getter;

@Getter
public enum LoginResultEnum {

    SUCCESS(0, "登录成功"),
    FAIL(1, "登录失败"),
    LOCKED(2, "账号锁定");

    private final int code;
    private final String desc;

    LoginResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
