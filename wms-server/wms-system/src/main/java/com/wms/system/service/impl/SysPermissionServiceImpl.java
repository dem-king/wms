package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.event.PermissionCacheEvictEvent;
import com.wms.common.exception.BizException;
import com.wms.system.converter.SysPermissionConverter;
import com.wms.system.domain.dto.SysPermissionDto;
import com.wms.system.domain.entity.SysMenu;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysPermissionVo;
import com.wms.system.mapper.SysMenuMapper;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private final SysMenuMapper sysMenuMapper;
    private final SysPermissionConverter sysPermissionConverter;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 查询用户启用的权限编码列表
     * 
     * @param userId 用户ID
     * @return 权限编码列表
     */
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
                        .eq(SysPermission::getStatus, BizConstants.STATUS_ENABLED)
        );
        return permissions.stream().map(SysPermission::getPermCode).collect(Collectors.toList());
    }

    /**
     * 鍒嗛〉鏌ヨ鏉冮檺鍒楄〃
     *
     * @param pageParam 鍒嗛〉鍙傛暟
     * @param permName 鏉冮檺鍚嶇О
     * @param permCode 鏉冮檺缂栫爜
     * @return 鏉冮檺鍒嗛〉缁撴灉
     */
    @Override
    public PageResult<SysPermissionVo> page(PageParam pageParam, String permName, String permCode) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(permName)) {
            wrapper.like(SysPermission::getPermName, permName);
        }
        if (StringUtils.hasText(permCode)) {
            wrapper.like(SysPermission::getPermCode, permCode);
        }
        wrapper.orderByDesc(SysPermission::getCreateTime);

        Page<SysPermission> page = sysPermissionMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);
        List<SysPermissionVo> records = sysPermissionConverter.toVoList(page.getRecords(), buildMenuNameMap(page.getRecords()));

        PageResult<SysPermissionVo> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 查询所有权限列表
     * 
     * @return 权限VO列表
     */
    @Override
    public List<SysPermissionVo> listAll() {
        List<SysPermission> list = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
        );
        return sysPermissionConverter.toVoList(list, buildMenuNameMap(list));
    }

    /**
     * 根据ID查询权限详情
     * 
     * @param id 权限ID
     * @return 权限VO
     */
    @Override
    public SysPermissionVo getById(Long id) {
        SysPermission perm = sysPermissionMapper.selectById(id);
        if (perm == null || perm.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("权限不存在");
        }
        return sysPermissionConverter.toVo(perm, buildMenuNameMap(List.of(perm)).get(perm.getMenuId()));
    }

    /**
     * 新增权限
     * 校验编码唯一性，默认启用
     * 
     * @param dto 权限新增参数
     * @return 新增后的权限VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysPermissionVo create(SysPermissionDto dto) {
        // 校验权限编码唯一性
        checkPermCodeUnique(dto.getPermCode(), null);
        SysPermission perm = new SysPermission();
        copyDtoToEntity(dto, perm);
        // 新增权限默认启用
        if (perm.getStatus() == null) {
            perm.setStatus(BizConstants.STATUS_ENABLED);
        }
        sysPermissionMapper.insert(perm);
        return sysPermissionConverter.toVo(perm);
    }

    /**
     * 更新权限
     * 
     * @param id 权限ID
     * @param dto 权限更新参数
     * @return 更新后的权限VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysPermissionVo update(Long id, SysPermissionDto dto) {
        SysPermission existing = sysPermissionMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("权限不存在");
        }
        // 校验权限编码唯一性(排除自身)
        Set<Long> affectedUserIds = findAffectedUserIdsByPermissionIds(List.of(id));
        checkPermCodeUnique(dto.getPermCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        sysPermissionMapper.updateById(existing);
        publishPermissionCacheEvictEvent(affectedUserIds);
        return sysPermissionConverter.toVo(existing);
    }

    /**
     * 删除权限(逻辑删除)
     * 
     * @param id 权限ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysPermission existing = sysPermissionMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("权限不存在");
        }
        // 逻辑删除权限
        Set<Long> affectedUserIds = findAffectedUserIdsByPermissionIds(List.of(id));
        SysPermission updatePerm = new SysPermission();
        updatePerm.setId(id);
        updatePerm.setDelFlag(DelFlagConstants.DELETED);
   
        sysPermissionMapper.updateById(updatePerm);
        publishPermissionCacheEvictEvent(affectedUserIds);
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
    private Map<Long, String> buildMenuNameMap(List<SysPermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> menuIds = permissions.stream()
                .map(SysPermission::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (menuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return sysMenuMapper.selectBatchIds(menuIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SysMenu::getId, SysMenu::getMenuName, (left, right) -> left));
    }

    private Set<Long> findAffectedUserIdsByPermissionIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Set.of();
        }
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getPermId, permissionIds)
        );
        List<Long> roleIds = rolePermissions.stream()
                .map(SysRolePermission::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roleIds.isEmpty()) {
            return Set.of();
        }
        return sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>()
                                .in(SysUserRole::getRoleId, roleIds)
                ).stream()
                .map(SysUserRole::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void publishPermissionCacheEvictEvent(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        applicationEventPublisher.publishEvent(new PermissionCacheEvictEvent(userIds));
    }
}
