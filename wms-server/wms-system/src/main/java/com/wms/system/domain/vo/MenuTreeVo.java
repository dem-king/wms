package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 菜单树节点VO
 * 用于前端动态路由和侧边栏渲染
 */
@Data
@Schema(description = "菜单树节点")
public class MenuTreeVo {

    /** 菜单ID */
    @Schema(description = "菜单ID")
    private Long id;

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String menuName;

    /** 菜单编码 */
    @Schema(description = "菜单编码")
    private String menuCode;

    /** 上级菜单ID */
    @Schema(description = "上级菜单ID")
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

    /** 子菜单列表 */
    @Schema(description = "子菜单列表")
    private List<MenuTreeVo> children;
}
