package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象
 */
@Data
@Schema(description = "操作日志信息")
public class SysOperLogVo {

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 操作模块 */
    @Schema(description = "操作模块")
    private String module;

    /** 操作类型 */
    @Schema(description = "操作类型")
    private String type;

    /** 操作描述 */
    @Schema(description = "操作描述")
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

    /** 请求参数 */
    @Schema(description = "请求参数")
    private String requestParams;

    /** 响应结果 */
    @Schema(description = "响应结果")
    private String responseResult;

    /** 操作IP */
    @Schema(description = "操作IP")
    private String operIp;

    /** 操作状态 */
    @Schema(description = "操作状态")
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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
