package com.wms.system.domain.constant;

import java.util.Set;

/**
 * 系统日志常量类
 * 定义操作日志和登录日志相关常量
 */
public final class SysLogConstants {

    private SysLogConstants() {
    }

    /** 操作状态：成功 */
    public static final String OPER_STATUS_SUCCESS = "SUCCESS";

    /** 操作状态：失败 */
    public static final String OPER_STATUS_FAIL = "FAIL";

    /** 登录状态：成功 */
    public static final String LOGIN_STATUS_SUCCESS = "SUCCESS";

    /** 登录状态：失败 */
    public static final String LOGIN_STATUS_FAIL = "FAIL";

    /** 登录状态：登出 */
    public static final String LOGIN_STATUS_LOGOUT = "LOGOUT";

    /** 请求参数最大长度(字节) */
    public static final int MAX_PARAM_LENGTH = 10240;

    /** 异常信息最大长度 */
    public static final int MAX_ERROR_MSG_LENGTH = 500;

    /** 脱敏替换值 */
    public static final String DESENSITIZE_MASK = "******";

    /** 敏感字段名列表 */
    public static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "token", "secret", "key", "authorization", "accessToken", "refreshToken"
    );
}
