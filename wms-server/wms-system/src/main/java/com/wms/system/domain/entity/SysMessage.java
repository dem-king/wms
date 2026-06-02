package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 站内信实体。
 * 存储系统发送给用户的站内消息和读取状态。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
@Schema(description = "站内信")
public class SysMessage extends BaseEntity {

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
}
