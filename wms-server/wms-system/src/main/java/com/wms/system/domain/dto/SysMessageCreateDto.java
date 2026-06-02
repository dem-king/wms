package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 站内信创建参数。
 * 供系统内部模块发送站内信时使用。
 */
@Data
@Schema(description = "站内信创建参数")
public class SysMessageCreateDto {

    /** 接收人ID */
    @NotNull(message = "接收人ID不能为空")
    @Schema(description = "接收人ID")
    private Long receiverId;

    /** 消息标题 */
    @NotBlank(message = "消息标题不能为空")
    @Schema(description = "消息标题")
    private String title;

    /** 消息内容 */
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容")
    private String content;

    /** 消息类型 */
    @NotBlank(message = "消息类型不能为空")
    @Schema(description = "消息类型")
    private String messageType;

    /** 消息级别 */
    @NotBlank(message = "消息级别不能为空")
    @Schema(description = "消息级别")
    private String messageLevel;

    /** 业务去重键 */
    @NotBlank(message = "业务去重键不能为空")
    @Schema(description = "业务去重键")
    private String businessKey;

    /** 目标跳转地址 */
    @Schema(description = "目标跳转地址")
    private String targetUrl;
}
