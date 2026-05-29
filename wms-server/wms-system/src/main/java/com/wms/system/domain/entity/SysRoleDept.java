package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色部门数据范围实体，对应角色自定义部门范围表sys_role_dept。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_dept")
public class SysRoleDept extends BaseEntity {

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long roleId;

    /** 部门ID */
    @Schema(description = "部门ID")
    private Long deptId;
}
