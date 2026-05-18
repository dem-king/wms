package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 权限新增/编辑DTO
 */
@Data
@Schema(description = "权限新增/编辑请求")
public class SysPermissionDto {

    /** 权限名称 */
    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称")
    private String permName;

    /** 权限编码 */
    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码")
    private String permCode;

    /** 权限类型(1-菜单 2-按钮 3-接口) */
    @NotNull(message = "权限类型不能为空")
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
