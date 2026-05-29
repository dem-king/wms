package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象。
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

    /** 数据范围(1-全部 2-自定义 3-本部门 4-本部门及以下 5-仅本人) */
    @Schema(description = "数据范围(1-全部 2-自定义 3-本部门 4-本部门及以下 5-仅本人)")
    private Integer dataScope;

    /** 自定义数据范围部门ID列表 */
    @Schema(description = "自定义数据范围部门ID列表")
    private List<Long> deptIds;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
