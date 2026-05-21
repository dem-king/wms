package com.wms.system.service;

import com.wms.system.domain.dto.SysRoleDto;
import com.wms.system.domain.vo.SysRoleVo;

import java.util.List;

/**
 * 角色服务接口
 * 提供角色CRUD、角色菜单/权限分配等功能
 */
public interface SysRoleService {

    /**
     * 获取所有角色列表
     *
     * @return 角色VO列表
     */
    List<SysRoleVo> listAll();

    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色VO
     */
    SysRoleVo getById(Long id);

    /**
     * 根据用户ID获取角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getRoleCodesByUserId(Long userId);

    /**
     * 根据用户ID获取角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 新增角色
     *
     * @param dto 角色新增参数
     * @return 新增后的角色VO
     */
    SysRoleVo create(SysRoleDto dto);

    /**
     * 更新角色
     *
     * @param id  角色ID
     * @param dto 角色更新参数
     * @return 更新后的角色VO
     */
    SysRoleVo update(Long id, SysRoleDto dto);

    /**
     * 删除角色(逻辑删除)
     * 同时清理角色关联的用户、菜单、权限数据
     *
     * @param id 角色ID
     */
    void delete(Long id);

    /**
     * 查询角色关联的菜单ID列表
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenus(Long id);

    /**
     * 分配角色菜单
     * 先删除旧关联再批量插入新关联
     *
     * @param id      角色ID
     * @param menuIds 菜单ID列表
     */
    void assignRoleMenus(Long id, List<Long> menuIds);

    /**
     * 查询角色关联的权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissions(Long id);

    /**
     * 分配角色权限
     * 先删除旧关联再批量插入新关联
     *
     * @param id      角色ID
     * @param permIds 权限ID列表
     */
    void assignRolePermissions(Long id, List<Long> permIds);
}
