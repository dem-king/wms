package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色新增/编辑DTO。
 */
@Data
@Schema(description = "角色新增/编辑请求")
public class SysRoleDto {

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String roleName;

    /** 角色编码 */
    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码")
    private String roleCode;

    /** 角色描述 */
    @Schema(description = "角色描述")
    private String roleDesc;

    /** 数据范围(1-全部 2-自定义 3-本部门 4-本部门及以下 5-仅本人) */
    @Schema(description = "数据范围(1-全部 2-自定义 3-本部门 4-本部门及以下 5-仅本人)")
    private Integer dataScope;

    /** 自定义数据范围部门ID列表 */
    @Schema(description = "自定义数据范围部门ID列表")
    private List<Long> deptIds;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;
}
