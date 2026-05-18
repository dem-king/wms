package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统角色实体，对应角色表sys_role */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称 */
    @Schema(description = "角色名称")
    private String roleName;

    /** 角色编码 */
    @Schema(description = "角色编码")
    private String roleCode;

    /** 角色描述 */
    @Schema(description = "角色描述")
    private String roleDesc;

    /** 数据范围(1-全部 2-本部门 3-本部门及子部门 4-自定义) */
    @Schema(description = "数据范围(1-全部 2-本部门 3-本部门及子部门 4-自定义)")
    private Integer dataScope;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;
}
