package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.system.domain.dto.SysPermissionDto;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysPermissionVo;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 处理权限CRUD、用户权限编码查询等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<String> getPermCodesByUserId(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();

        List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds)
        );
        if (rolePerms.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> permIds = rolePerms.stream().map(SysRolePermission::getPermId).distinct().toList();

        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .in(SysPermission::getId, permIds)
                        .eq(SysPermission::getStatus, 1)
        );
        return permissions.stream().map(SysPermission::getPermCode).collect(Collectors.toList());
    }

    @Override
    public List<SysPermissionVo> listAll() {
        List<SysPermission> list = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
        );
        return list.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public SysPermissionVo getById(Long id) {
        SysPermission perm = sysPermissionMapper.selectById(id);
        if (perm == null || perm.getDelFlag() == 1) {
            throw new BizException("权限不存在");
        }
        return toVo(perm);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysPermissionVo create(SysPermissionDto dto) {
        // 校验权限编码唯一性
        checkPermCodeUnique(dto.getPermCode(), null);
        SysPermission perm = new SysPermission();
        copyDtoToEntity(dto, perm);
        // 新增权限默认启用
        if (perm.getStatus() == null) {
            perm.setStatus(1);
        }
        sysPermissionMapper.insert(perm);
        return toVo(perm);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysPermissionVo update(Long id, SysPermissionDto dto) {
        SysPermission existing = sysPermissionMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("权限不存在");
        }
        // 校验权限编码唯一性(排除自身)
        checkPermCodeUnique(dto.getPermCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        sysPermissionMapper.updateById(existing);
        return toVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysPermission existing = sysPermissionMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("权限不存在");
        }
        // 逻辑删除权限
        SysPermission updatePerm = new SysPermission();
        updatePerm.setId(id);
        updatePerm.setDelFlag(1);
        updatePerm.setLastOperType("d");
        sysPermissionMapper.updateById(updatePerm);
    }

    /**
     * 校验权限编码唯一性
     *
     * @param permCode  权限编码
     * @param excludeId 排除的权限ID(更新时排除自身)
     */
    private void checkPermCodeUnique(String permCode, Long excludeId) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getPermCode, permCode);
        if (excludeId != null) {
            wrapper.ne(SysPermission::getId, excludeId);
        }
        Long count = sysPermissionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("权限编码已存在: " + permCode);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(SysPermissionDto dto, SysPermission entity) {
        entity.setPermName(dto.getPermName());
        entity.setPermCode(dto.getPermCode());
        entity.setPermType(dto.getPermType());
        entity.setParentId(dto.getParentId());
        entity.setMenuId(dto.getMenuId());
        entity.setStatus(dto.getStatus());
    }

    /**
     * SysPermission实体转SysPermissionVo
     */
    private SysPermissionVo toVo(SysPermission perm) {
        SysPermissionVo vo = new SysPermissionVo();
        vo.setId(perm.getId());
        vo.setPermName(perm.getPermName());
        vo.setPermCode(perm.getPermCode());
        vo.setPermType(perm.getPermType());
        vo.setParentId(perm.getParentId());
        vo.setMenuId(perm.getMenuId());
        vo.setStatus(perm.getStatus());
        vo.setCreateTime(perm.getCreateTime());
        return vo;
    }
}
