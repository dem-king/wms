package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 角色权限关联实体，对应角色权限关联表sys_role_permission */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class SysRolePermission extends BaseEntity {

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long roleId;

    /** 权限ID */
    @Schema(description = "权限ID")
    private Long permId;
}
