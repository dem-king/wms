package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内信视图对象。
 */
@Data
@Schema(description = "站内信视图对象")
public class SysMessageVo {

    /** 站内信ID */
    @Schema(description = "站内信ID")
    private Long id;

    /** 接收人ID */
    @Schema(description = "接收人ID")
    private Long receiverId;

    /** 消息标题 */
    @Schema(description = "消息标题")
    private String title;

    /** 消息内容 */
    @Schema(description = "消息内容")
    private String content;

    /** 消息类型 */
    @Schema(description = "消息类型")
    private String messageType;

    /** 消息级别 */
    @Schema(description = "消息级别")
    private String messageLevel;

    /** 业务去重键 */
    @Schema(description = "业务去重键")
    private String businessKey;

    /** 目标跳转地址 */
    @Schema(description = "目标跳转地址")
    private String targetUrl;

    /** 读取状态(0-未读 1-已读) */
    @Schema(description = "读取状态(0-未读 1-已读)")
    private Integer readStatus;

    /** 读取时间 */
    @Schema(description = "读取时间")
    private LocalDateTime readTime;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
