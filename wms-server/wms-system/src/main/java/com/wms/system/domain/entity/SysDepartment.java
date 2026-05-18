package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统部门实体，对应部门表sys_department */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
public class SysDepartment extends BaseEntity {

    /** 部门名称 */
    @Schema(description = "部门名称")
    private String deptName;

    /** 部门编码 */
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
