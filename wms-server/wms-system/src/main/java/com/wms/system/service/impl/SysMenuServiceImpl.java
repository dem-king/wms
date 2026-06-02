package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.util.SecurityUtil;
import com.wms.system.domain.dto.SysMenuDto;
import com.wms.system.domain.constant.SysMenuConstants;
import com.wms.system.domain.entity.SysMenu;
import com.wms.system.domain.entity.SysRoleMenu;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.MenuTreeVo;
import com.wms.system.converter.SysMenuConverter;
import com.wms.system.mapper.SysMenuMapper;
import com.wms.system.mapper.SysRoleMenuMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 * 处理菜单CRUD、菜单树构建、用户菜单查询等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuConverter sysMenuConverter;

    /**
     * 查询所有菜单(树形结构)
     * 
     * @return 菜单树VO列表
     */
    @Override
    public List<MenuTreeVo> listAll() {
        return buildMenuTree();
    }

    /**
     * 根据ID查询菜单详情
     * 
     * @param id 菜单ID
     * @return 菜单树VO
     */
    @Override
    public MenuTreeVo getById(Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null || menu.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("菜单不存在");
        }
        return sysMenuConverter.toVo(menu);
    }

    /**
     * 新增菜单
     * 校验编码唯一性，默认启用且可见
     * 
     * @param dto 菜单新增参数
     * @return 新增后的菜单树VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuTreeVo create(SysMenuDto dto) {
        // 校验菜单编码唯一性
        checkMenuCodeUnique(dto.getMenuCode(), null);
        SysMenu menu = new SysMenu();
        copyDtoToEntity(dto, menu);
        // 新增菜单默认启用
        if (menu.getStatus() == null) {
            menu.setStatus(SysMenuConstants.STATUS_ENABLED);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(SysMenuConstants.VISIBLE_YES);
        }
        sysMenuMapper.insert(menu);
        return sysMenuConverter.toVo(menu);
    }

    /**
     * 更新菜单
     * 
     * @param id 菜单ID
     * @param dto 菜单更新参数
     * @return 更新后的菜单树VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuTreeVo update(Long id, SysMenuDto dto) {
        SysMenu existing = sysMenuMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("菜单不存在");
        }
        // 校验菜单编码唯一性(排除自身)
        checkMenuCodeUnique(dto.getMenuCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        sysMenuMapper.updateById(existing);
        return sysMenuConverter.toVo(existing);
    }

    /**
     * 删除菜单(逻辑删除)
     * 存在子菜单时不允许删除
     * 
     * @param id 菜单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 校验是否存在子菜单
        Long childCount = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id)
        );
        if (childCount > 0) {
            throw new BizException("存在子菜单，无法删除");
        }
        // 逻辑删除菜单
        LogicDeleteHelper.markDeleted(sysMenuMapper, SysMenu.class, id);
    }

    /**
     * 构建完整菜单树
     * 
     * @return 菜单树VO列表
     */
    @Override
    public List<MenuTreeVo> buildMenuTree() {
        List<SysMenu> allMenus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .orderByAsc(SysMenu::getSortOrder)
        );
        return buildTree(allMenus);
    }

    /**
     * 查询角色关联的菜单ID列表
     * 
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId)
        );
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    /**
     * 查询当前用户的菜单树
     * 根据用户角色关联获取已授权的目录和菜单，并补全父级菜单
     * 
     * @return 用户菜单树VO列表
     */
    @Override
    public List<MenuTreeVo> getUserMenuTree() {
        Long userId = SecurityUtil.getCurrentUserId();
        // 1. 查询用户关联的角色ID列表
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();

        // 2. 查询角色关联的菜单ID列表
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .in(SysRoleMenu::getRoleId, roleIds)
        );
        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().toList();

        // 3. 查询菜单详情：仅目录(1)和菜单(2)类型，启用且可见
        List<SysMenu> menus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getId, menuIds)
                        .in(SysMenu::getMenuType, SysMenuConstants.MENU_TYPE_DIR, SysMenuConstants.MENU_TYPE_MENU)
                        .eq(SysMenu::getStatus, SysMenuConstants.STATUS_ENABLED)
                        .eq(SysMenu::getVisible, SysMenuConstants.VISIBLE_YES)
                        .orderByAsc(SysMenu::getSortOrder)
        );

        // 4. 补全父级菜单：如果子菜单被授权但父目录未被授权，需将父目录也加入
        Set<Long> allMenuIds = new HashSet<>(menuIds);
        menus.forEach(m -> allMenuIds.add(m.getId()));
        Set<Long> parentIdSet = menus.stream()
                .map(SysMenu::getParentId)
                .filter(pid -> pid != null && pid != 0 && !allMenuIds.contains(pid))
                .collect(Collectors.toSet());

        if (!parentIdSet.isEmpty()) {
            // 递归补全所有缺失的父级
            Set<Long> needAddIds = new HashSet<>(parentIdSet);
            while (!needAddIds.isEmpty()) {
                List<SysMenu> parentMenus = sysMenuMapper.selectList(
                        new LambdaQueryWrapper<SysMenu>()
                                .in(SysMenu::getId, needAddIds)
                                .eq(SysMenu::getStatus, SysMenuConstants.STATUS_ENABLED)
                );
                menus.addAll(parentMenus);
                allMenuIds.addAll(needAddIds);
                // 查找这些父菜单的父级
                needAddIds = parentMenus.stream()
                        .map(SysMenu::getParentId)
                        .filter(pid -> pid != null && pid != 0 && !allMenuIds.contains(pid))
                        .collect(Collectors.toSet());
            }
        }

        // 5. 构建树形结构
        return buildTree(menus);
    }

    /**
     * 将菜单列表构建为树形结构
     *
     * @param menus 菜单列表
     * @return 树形菜单列表
     */
    private List<MenuTreeVo> buildTree(List<SysMenu> menus) {
        List<MenuTreeVo> voList = menus.stream().map(sysMenuConverter::toVo).collect(Collectors.toList());
        Map<Long, List<MenuTreeVo>> groupedByParent = voList.stream()
                .collect(Collectors.groupingBy(MenuTreeVo::getParentId));
        voList.forEach(vo -> vo.setChildren(groupedByParent.getOrDefault(vo.getId(), Collections.emptyList())));
        // 返回顶级菜单(parentId=0)
        return groupedByParent.getOrDefault(0L, Collections.emptyList());
    }

    /**
     * 校验菜单编码唯一性
     *
     * @param menuCode 菜单编码
     * @param excludeId 排除的菜单ID(更新时排除自身)
     */
    private void checkMenuCodeUnique(String menuCode, Long excludeId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuCode, menuCode);
        if (excludeId != null) {
            wrapper.ne(SysMenu::getId, excludeId);
        }
        Long count = sysMenuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("菜单编码已存在: " + menuCode);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(SysMenuDto dto, SysMenu entity) {
        entity.setMenuName(dto.getMenuName());
        entity.setMenuCode(dto.getMenuCode());
        entity.setParentId(dto.getParentId());
        entity.setMenuType(dto.getMenuType());
        entity.setPath(dto.getPath());
        entity.setComponent(dto.getComponent());
        entity.setRedirect(dto.getRedirect());
        entity.setIcon(dto.getIcon());
        entity.setIsExternal(dto.getIsExternal());
        entity.setIsCache(dto.getIsCache());
        entity.setVisible(dto.getVisible());
        entity.setStatus(dto.getStatus());
        entity.setSortOrder(dto.getSortOrder());
        entity.setPermCode(dto.getPermCode());
    }
}
