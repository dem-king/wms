package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色视图对象
 */
@Data
@Schema(description = "角色信息")
public class SysRoleVo {

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long id;

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

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
