package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 * 对应表 sys_oper_log，记录用户操作日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {

    /** 操作模块 */
    @Schema(description = "操作模块")
    @TableField("`module`")
    private String module;

    /** 操作类型 */
    @Schema(description = "操作类型")
    @TableField("`type`")
    private String type;

    /** 操作描述 */
    @Schema(description = "操作描述")
    @TableField("`desc`")
    private String desc;

    /** 操作人ID */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String operatorName;

    /** 请求URL */
    @Schema(description = "请求URL")
    private String requestUrl;

    /** 请求方法 */
    @Schema(description = "请求方法")
    private String requestMethod;

    /** 请求参数(脱敏后) */
    @Schema(description = "请求参数")
    private String requestParams;

    /** 响应结果 */
    @Schema(description = "响应结果")
    private String responseResult;

    /** 操作IP */
    @Schema(description = "操作IP")
    private String operIp;

    /** 操作状态: SUCCESS/FAIL */
    @Schema(description = "操作状态")
    @TableField("`status`")
    private String status;

    /** 异常信息 */
    @Schema(description = "异常信息")
    private String errorMsg;

    /** 耗时(毫秒) */
    @Schema(description = "耗时(毫秒)")
    private Long costTime;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operTime;
}
