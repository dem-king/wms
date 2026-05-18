package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 用户角色关联实体，对应用户角色关联表sys_user_role */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long roleId;
}
