package com.wms.auth.domain.constant;

/**
 * 认证模块常量类
 * 定义验证码参数、登录锁定、限流、操作日志等常量
 */
public final class AuthConstants {

    private AuthConstants() {
    }

    /** 验证码宽度 */
    public static final int CAPTCHA_WIDTH = 120;

    /** 验证码高度 */
    public static final int CAPTCHA_HEIGHT = 40;

    /** 验证码字符数 */
    public static final int CAPTCHA_CHAR_COUNT = 4;

    /** 验证码干扰线数 */
    public static final int CAPTCHA_LINE_COUNT = 6;

    /** IP限流时间窗口（秒） */
    public static final int RATE_LIMIT_WINDOW_SECONDS = 60;

    /** 权限缓存过期时间（分钟） */
    public static final int PERM_CACHE_EXPIRE_MINUTES = 5;

    /** 操作结果：成功 */
    public static final int OPER_RESULT_SUCCESS = 1;

    /** 认证操作日志模块 */
    public static final String OPER_LOG_MODULE_AUTH = "auth";

    /** 用户登出日志描述 */
    public static final String OPER_LOG_DESC_LOGOUT = "用户登出";

    /** UserAgent截取最大长度 */
    public static final int USER_AGENT_MAX_LENGTH = 200;

    /** 锁定标记值 */
    public static final String LOCK_FLAG = "1";
}
