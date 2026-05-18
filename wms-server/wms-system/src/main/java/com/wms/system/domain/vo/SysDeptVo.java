package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门视图对象(树形)
 */
@Data
@Schema(description = "部门信息(树形)")
public class SysDeptVo {

    /** 部门ID */
    @Schema(description = "部门ID")
    private Long id;

    /** 部门名称 */
    @Schema(description = "部门名称")
    private String deptName;

    /** 部门编码 */
    @Schema(description = "部门编码")
    private String deptCode;

    /** 上级部门ID */
    @Schema(description = "上级部门ID")
    private Long parentId;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 负责人 */
    @Schema(description = "负责人")
    private String leader;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String phone;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 子部门列表 */
    @Schema(description = "子部门列表")
    private List<SysDeptVo> children;
}
