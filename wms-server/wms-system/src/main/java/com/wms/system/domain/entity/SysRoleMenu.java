package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 角色菜单关联实体，对应角色菜单关联表sys_role_menu */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long roleId;

    /** 菜单ID */
    @Schema(description = "菜单ID")
    private Long menuId;
}
