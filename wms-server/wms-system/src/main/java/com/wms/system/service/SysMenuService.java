package com.wms.system.service;

import com.wms.system.domain.dto.SysMenuDto;
import com.wms.system.domain.entity.SysMenu;
import com.wms.system.domain.vo.MenuTreeVo;

import java.util.List;

/**
 * 菜单服务接口
 * 提供菜单CRUD、菜单树构建、用户菜单查询等功能
 */
public interface SysMenuService {

    /**
     * 获取所有菜单列表
     *
     * @return 菜单列表
     */
    List<SysMenu> listAll();

    /**
     * 根据ID获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单实体
     */
    SysMenu getById(Long id);

    /**
     * 新增菜单
     *
     * @param dto 菜单新增参数
     * @return 新增后的菜单实体
     */
    SysMenu create(SysMenuDto dto);

    /**
     * 更新菜单
     *
     * @param id  菜单ID
     * @param dto 菜单更新参数
     * @return 更新后的菜单实体
     */
    SysMenu update(Long id, SysMenuDto dto);

    /**
     * 删除菜单(逻辑删除)
     *
     * @param id 菜单ID
     */
    void delete(Long id);

    /**
     * 构建菜单树(全部菜单，用于菜单管理页面)
     *
     * @return 菜单树
     */
    List<MenuTreeVo> buildMenuTree();

    /**
     * 获取指定角色拥有的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 获取当前登录用户的菜单树(用于动态路由和侧边栏渲染)
     * 在Service内部通过SecurityUtil获取当前用户ID
     *
     * @return 菜单树(仅包含目录和菜单类型，不含按钮)
     */
    List<MenuTreeVo> getUserMenuTree();
}
