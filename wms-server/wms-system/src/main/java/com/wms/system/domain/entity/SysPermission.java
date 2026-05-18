package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统权限实体，对应权限表sys_permission */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    /** 权限名称 */
    @Schema(description = "权限名称")
    private String permName;

    /** 权限编码 */
    @Schema(description = "权限编码")
    private String permCode;

    /** 权限类型(1-菜单 2-按钮 3-接口) */
    @Schema(description = "权限类型(1-菜单 2-按钮 3-接口)")
    private Integer permType;

    /** 上级权限ID */
    @Schema(description = "上级权限ID")
    private Long parentId;

    /** 关联菜单ID */
    @Schema(description = "关联菜单ID")
    private Long menuId;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;
}
