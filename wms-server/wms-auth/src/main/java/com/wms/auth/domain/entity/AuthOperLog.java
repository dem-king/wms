package com.wms.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 * 记录用户关键业务操作的审计信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_oper_log")
public class AuthOperLog extends BaseEntity {

    /** 操作用户ID */
    @Schema(description = "操作用户ID")
    private Long userId;

    /** 操作用户名 */
    @Schema(description = "操作用户名")
    private String username;

    /** 操作类型(login/logout/insert/update/delete等) */
    @Schema(description = "操作类型")
    private String operType;

    /** 操作结果(0-失败 1-成功) */
    @Schema(description = "操作结果(0-失败 1-成功)")
    private Integer operResult;

    /** 客户端IP地址 */
    @Schema(description = "客户端IP地址")
    private String clientIp;

    /** 请求ID(用于链路追踪) */
    @Schema(description = "请求ID")
    private String requestId;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operTime;
}
