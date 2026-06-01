package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限视图对象
 */
@Data
@Schema(description = "权限信息")
public class SysPermissionVo {

    /** 权限ID */
    @Schema(description = "权限ID")
    private Long id;

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

    /** Related menu name */
    @Schema(description = "鍏宠仈鑿滃崟鍚嶇О")
    private String menuName;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
