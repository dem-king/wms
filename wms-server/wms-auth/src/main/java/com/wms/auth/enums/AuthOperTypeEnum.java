package com.wms.auth.enums;

import lombok.Getter;

@Getter
public enum AuthOperTypeEnum {

    LOGOUT("LOGOUT", "登出"),
    CHANGE_PASSWORD("CHANGE_PASSWORD", "修改密码"),
    TOKEN_REVOKE("TOKEN_REVOKE", "Token撤销");

    private final String code;
    private final String desc;

    AuthOperTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
