package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 部门新增/编辑DTO
 */
@Data
@Schema(description = "部门新增/编辑请求")
public class SysDeptDto {

    /** 部门名称 */
    @NotBlank(message = "部门名称不能为空")
    @Schema(description = "部门名称")
    private String deptName;

    /** 部门编码 */
    @NotBlank(message = "部门编码不能为空")
    @Schema(description = "部门编码")
    private String deptCode;

    /** 上级部门ID(0为顶级) */
    @Schema(description = "上级部门ID(0为顶级)")
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
}
