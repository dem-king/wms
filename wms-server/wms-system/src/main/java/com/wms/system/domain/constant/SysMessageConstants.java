package com.wms.system.domain.constant;

/**
 * 站内信常量类。
 */
public final class SysMessageConstants {

    private SysMessageConstants() {
    }

    /** 读取状态：未读 */
    public static final int READ_STATUS_UNREAD = 0;

    /** 读取状态：已读 */
    public static final int READ_STATUS_READ = 1;

    /** 消息类型：审批超时 */
    public static final String TYPE_APPROVAL_TIMEOUT = "APPROVAL_TIMEOUT";

    /** 消息级别：普通 */
    public static final String LEVEL_INFO = "INFO";

    /** 消息级别：警告 */
    public static final String LEVEL_WARNING = "WARNING";
}
