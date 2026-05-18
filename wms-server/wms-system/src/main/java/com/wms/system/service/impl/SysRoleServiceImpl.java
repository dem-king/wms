package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.system.domain.dto.SysRoleDto;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysRoleMenu;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysRoleVo;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysRoleMenuMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 * 处理角色CRUD、角色菜单/权限分配等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysRole> listAll() {
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
        );
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null || role.getDelFlag() == 1) {
            throw new BizException("角色不存在");
        }
        return role;
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        List<Long> roleIds = getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getStatus, 1)
        );
        return roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );
        return userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRoleVo create(SysRoleDto dto) {
        // 校验角色编码唯一性
        checkRoleCodeUnique(dto.getRoleCode(), null);
        SysRole role = new SysRole();
        copyDtoToEntity(dto, role);
        // 新增角色默认启用
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        sysRoleMapper.insert(role);
        return toVo(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRoleVo update(Long id, SysRoleDto dto) {
        SysRole existing = getById(id);
        // 校验角色编码唯一性(排除自身)
        checkRoleCodeUnique(dto.getRoleCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        sysRoleMapper.updateById(existing);
        return toVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysRole existing = getById(id);
        // 逻辑删除角色
        SysRole updateRole = new SysRole();
        updateRole.setId(id);
        updateRole.setDelFlag(1);
        updateRole.setLastOperType("d");
        sysRoleMapper.updateById(updateRole);
        // 清理角色关联的用户角色关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, id)
        );
        for (SysUserRole userRole : userRoles) {
            SysUserRole updateUserRole = new SysUserRole();
            updateUserRole.setId(userRole.getId());
            updateUserRole.setDelFlag(1);
            updateUserRole.setLastOperType("d");
            sysUserRoleMapper.updateById(updateUserRole);
        }
        // 清理角色关联的角色菜单关联
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, id)
        );
        for (SysRoleMenu roleMenu : roleMenus) {
            SysRoleMenu updateMenu = new SysRoleMenu();
            updateMenu.setId(roleMenu.getId());
            updateMenu.setDelFlag(1);
            updateMenu.setLastOperType("d");
            sysRoleMenuMapper.updateById(updateMenu);
        }
        // 清理角色关联的角色权限关联
        List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, id)
        );
        for (SysRolePermission rolePerm : rolePerms) {
            SysRolePermission updatePerm = new SysRolePermission();
            updatePerm.setId(rolePerm.getId());
            updatePerm.setDelFlag(1);
            updatePerm.setLastOperType("d");
            sysRolePermissionMapper.updateById(updatePerm);
        }
    }

    @Override
    public List<Long> getRoleMenus(Long id) {
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, id)
        );
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(Long id, List<Long> menuIds) {
        // 先逻辑删除旧的角色菜单关联
        List<SysRoleMenu> oldRoleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, id)
        );
        for (SysRoleMenu oldMenu : oldRoleMenus) {
            SysRoleMenu updateMenu = new SysRoleMenu();
            updateMenu.setId(oldMenu.getId());
            updateMenu.setDelFlag(1);
            updateMenu.setLastOperType("d");
            sysRoleMenuMapper.updateById(updateMenu);
        }
        // 批量插入新的角色菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(id);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public List<Long> getRolePermissions(Long id) {
        List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, id)
        );
        return rolePerms.stream().map(SysRolePermission::getPermId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermissions(Long id, List<Long> permIds) {
        // 先逻辑删除旧的角色权限关联
        List<SysRolePermission> oldRolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, id)
        );
        for (SysRolePermission oldPerm : oldRolePerms) {
            SysRolePermission updatePerm = new SysRolePermission();
            updatePerm.setId(oldPerm.getId());
            updatePerm.setDelFlag(1);
            updatePerm.setLastOperType("d");
            sysRolePermissionMapper.updateById(updatePerm);
        }
        // 批量插入新的角色权限关联
        if (permIds != null && !permIds.isEmpty()) {
            for (Long permId : permIds) {
                SysRolePermission rolePerm = new SysRolePermission();
                rolePerm.setRoleId(id);
                rolePerm.setPermId(permId);
                sysRolePermissionMapper.insert(rolePerm);
            }
        }
    }

    /**
     * 校验角色编码唯一性
     *
     * @param roleCode  角色编码
     * @param excludeId 排除的角色ID(更新时排除自身)
     */
    private void checkRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(SysRole::getId, excludeId);
        }
        Long count = sysRoleMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("角色编码已存在: " + roleCode);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(SysRoleDto dto, SysRole entity) {
        entity.setRoleName(dto.getRoleName());
        entity.setRoleCode(dto.getRoleCode());
        entity.setRoleDesc(dto.getRoleDesc());
        entity.setDataScope(dto.getDataScope());
        entity.setStatus(dto.getStatus());
    }

    /**
     * SysRole实体转SysRoleVo
     */
    private SysRoleVo toVo(SysRole role) {
        SysRoleVo vo = new SysRoleVo();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleDesc(role.getRoleDesc());
        vo.setDataScope(role.getDataScope());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}
