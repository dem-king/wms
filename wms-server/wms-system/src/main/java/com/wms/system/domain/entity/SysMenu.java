package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统菜单实体，对应菜单表sys_menu */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String menuName;

    /** 菜单编码 */
    @Schema(description = "菜单编码")
    private String menuCode;

    /** 上级菜单ID(0为顶级) */
    @Schema(description = "上级菜单ID(0为顶级)")
    private Long parentId;

    /** 菜单类型(1-目录 2-菜单 3-按钮/操作) */
    @Schema(description = "菜单类型(1-目录 2-菜单 3-按钮/操作)")
    private Integer menuType;

    /** 路由路径 */
    @Schema(description = "路由路径")
    private String path;

    /** 前端组件路径 */
    @Schema(description = "前端组件路径")
    private String component;

    /** 重定向路径 */
    @Schema(description = "重定向路径")
    private String redirect;

    /** 菜单图标 */
    @Schema(description = "菜单图标")
    private String icon;

    /** 是否外链(0-否 1-是) */
    @Schema(description = "是否外链(0-否 1-是)")
    private Integer isExternal;

    /** 是否缓存(0-否 1-是) */
    @Schema(description = "是否缓存(0-否 1-是)")
    private Integer isCache;

    /** 是否可见(0-隐藏 1-显示) */
    @Schema(description = "是否可见(0-隐藏 1-显示)")
    private Integer visible;

    /** 状态(0-禁用 1-启用) */
    @Schema(description = "状态(0-禁用 1-启用)")
    private Integer status;

    /** 排序号 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 关联权限编码 */
    @Schema(description = "关联权限编码")
    private String permCode;
}
