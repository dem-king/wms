package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.constant.PermissionConstants;
import com.wms.common.event.PermissionCacheEvictEvent;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.system.domain.dto.SysUserDto;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysUserVo;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.wms.system.manager.SysConfigManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 处理用户CRUD、密码重置、状态切换、角色分配等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SysConfigManager configManager;

    @Value("${wms.default-password}")
    private String defaultPassword;

    /**
     * 根据用户名查询用户实体(含密码哈希)
     * 
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );
    }

    /**
     * 更新用户密码哈希
     * 
     * @param userId 用户ID
     * @param newPasswordHash 新密码哈希
     * @return 是否更新成功
     */
    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(newPasswordHash);
        return sysUserMapper.updateById(user) > 0;
    }

    /**
     * 分页查询用户
     * 
     * @param pageParam 分页参数
     * @param username 用户名(可选，模糊匹配)
     * @param realName 真实姓名(可选，模糊匹配)
     * @param status 状态(可选)
     * @return 用户分页结果
     */
    @Override
    public PageResult<SysUserVo> page(PageParam pageParam, String username, String realName, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>();
        // 模糊查询用户名
        if (username != null && !username.isBlank()) {
            wrapper.like(SysUser::getUsername, username);
        }
        // 模糊查询真实姓名
        if (realName != null && !realName.isBlank()) {
            wrapper.like(SysUser::getRealName, realName);
        }
        // 精确查询状态
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = sysUserMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        List<SysUserVo> records = page.getRecords().stream()
                .map(this::toBasicVo)
                .collect(Collectors.toList());
        fillUserRoles(records);

        PageResult<SysUserVo> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询用户详情
     * 
     * @param id 用户ID
     * @return 用户VO
     */
    @Override
    public SysUserVo getVoByUsernameExact(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username.trim()));
        if (user == null || Objects.equals(user.getDelFlag(), DelFlagConstants.DELETED)) {
            return null;
        }
        SysUserVo vo = toVo(user);
        vo.setHasApprovalPermission(hasApprovalPermission(user.getId()));
        return vo;
    }

    @Override
    public SysUserVo getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户已删除");
        }
        return toVo(user);
    }

    /**
     * 更新用户个人资料
     * 
     * @param id 用户ID
     * @param realName 真实姓名
     * @param phone 手机号
     * @param email 邮箱
     * @param avatar 头像
     * @return 更新后的用户VO
     */
    /**
     * 更新用户
     * 编辑时不修改密码，同步更新角色关联
     * 
     * @param id 用户ID
     * @param dto 用户更新参数
     * @return 更新后的用户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVo updateProfile(Long id, String realName, String phone, String email, String avatar) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new BizException("用户不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户已删除");
        }

        existing.setRealName(realName);
        existing.setPhone(phone);
        existing.setEmail(email);
        existing.setAvatar(avatar);
        sysUserMapper.updateById(existing);
        return toVo(existing);
    }

    /**
     * 新增用户
     * 校验用户名唯一性，密码BCrypt加密，默认启用
     * 
     * @param dto 用户新增参数
     * @return 新增后的用户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVo create(SysUserDto dto) {
        // 校验用户名唯一性
        checkUsernameUnique(dto.getUsername(), null);
        SysUser user = new SysUser();
        copyDtoToEntity(dto, user);
        // 密码使用BCrypt加密存储
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 从数据库配置读取默认密码，支持动态修改
        String configuredDefaultPassword = configManager.getValue("wms.security.default-password", defaultPassword);
        String password = dto.getPassword() != null ? dto.getPassword() : configuredDefaultPassword;
        user.setPassword(encoder.encode(password));
        // 新增用户默认启用
        if (user.getStatus() == null) {
            user.setStatus(BizConstants.STATUS_ENABLED);
        }
        sysUserMapper.insert(user);
        // 保存用户角色关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            saveUserRoles(user.getId(), dto.getRoleIds());
        }
        return toVo(user);
    }

    /**
     * 更新用户
     * 编辑时不修改密码，同步更新角色关联
     * 
     * @param id 用户ID
     * @param dto 用户更新参数
     * @return 更新后的用户VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVo update(Long id, SysUserDto dto) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new BizException("用户不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户已删除");
        }
        // 校验用户名唯一性(排除自身)
        checkUsernameUnique(dto.getUsername(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改密码(密码修改通过专门接口)
        existing.setPassword(null);
        sysUserMapper.updateById(existing);
        // 如果dto中包含roleIds则同步更新用户角色关联
        if (dto.getRoleIds() != null) {
            assignRoles(id, dto.getRoleIds());
        }
        return toVo(existing);
    }

    /**
     * 删除用户(逻辑删除)
     * 
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户已删除");
        }
        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(sysUserMapper, SysUser.class, id);
    }

    /**
     * 重置用户密码为默认密码
     * 
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户已删除");
        }
        // 使用BCrypt加密默认密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 从数据库配置读取默认密码，支持动态修改
        String configuredDefaultPassword = configManager.getValue("wms.security.default-password", defaultPassword);
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(encoder.encode(configuredDefaultPassword));
        sysUserMapper.updateById(updateUser);
    }

    /**
     * 切换用户状态(启用/禁用)
     * 
     * @param id 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户已删除");
        }
        // 切换状态: 0->1, 1->0
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(user.getStatus() == BizConstants.STATUS_ENABLED ? BizConstants.STATUS_DISABLED : BizConstants.STATUS_ENABLED);
        sysUserMapper.updateById(updateUser);
    }

    /**
     * 查询用户的角色ID列表
     * 
     * @param id 用户ID
     * @return 角色ID列表
     */
    @Override
    public List<Long> getUserRoles(Long id) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );
        return userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    /**
     * 分配用户角色
     * 先逻辑删除旧关联，再批量保存新关联
     * 
     * @param id 用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long id, List<Long> roleIds) {
        List<SysUserRole> oldUserRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );

        Set<Long> targetRoleIds = normalizeRoleIds(roleIds);
        Set<Long> currentRoleIds = oldUserRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toSet());

        // 仅逻辑删除被移除的角色，避免“删后重插同一角色”触发唯一索引冲突。
        List<SysUserRole> removedRoles = new java.util.ArrayList<>();
        for (SysUserRole oldRole : oldUserRoles) {
            if (!targetRoleIds.contains(oldRole.getRoleId())) {
                SysUserRole updateRole = new SysUserRole();
                updateRole.setId(oldRole.getId());
                updateRole.setDelFlag(DelFlagConstants.DELETED);
                removedRoles.add(updateRole);
            }
        }
        if (!removedRoles.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(sysUserRoleMapper, SysUserRole.class, removedRoles);
        }

        for (Long roleId : targetRoleIds) {
            if (!currentRoleIds.contains(roleId)) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(id);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
        applicationEventPublisher.publishEvent(new PermissionCacheEvictEvent(Set.of(id)));
    }

    /**
     * 批量保存用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : normalizeRoleIds(roleIds)) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }

    /**
     * 角色ID去重并过滤空值
     */
    private Set<Long> normalizeRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        return roleIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 校验用户名唯一性
     *
     * @param username  用户名
     * @param excludeId 排除的用户ID(更新时排除自身)
     */
    private void checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        Long count = sysUserMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("用户名已存在: " + username);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(SysUserDto dto, SysUser entity) {
        entity.setUsername(dto.getUsername());
        entity.setRealName(dto.getRealName());
        entity.setDeptId(dto.getDeptId());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAvatar(dto.getAvatar());
        entity.setStatus(dto.getStatus());
    }

    /**
     * SysUser实体转SysUserVo
     */
    private SysUserVo toVo(SysUser user) {
        SysUserVo vo = toBasicVo(user);
        List<Long> roleIds = getUserRoles(user.getId());
        vo.setRoleIds(roleIds);
        vo.setRoleNames(buildRoleNames(roleIds));
        return vo;
    }

    /**
     * SysUser实体转基础SysUserVo，不含角色信息
     */
    private SysUserVo toBasicVo(SysUser user) {
        SysUserVo vo = new SysUserVo();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setDeptId(user.getDeptId());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    /**
     * 批量回填用户角色信息，避免分页查询产生N+1
     */
    private void fillUserRoles(List<SysUserVo> userVos) {
        if (userVos == null || userVos.isEmpty()) {
            return;
        }

        List<Long> userIds = userVos.stream()
                .map(SysUserVo::getId)
                .filter(Objects::nonNull)
                .toList();
        if (userIds.isEmpty()) {
            return;
        }

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds)
        );
        if (userRoles.isEmpty()) {
            userVos.forEach(vo -> {
                vo.setRoleIds(List.of());
                vo.setRoleNames(List.of());
            });
            return;
        }

        Map<Long, List<Long>> roleIdsByUserId = userRoles.stream()
                .collect(Collectors.groupingBy(
                        SysUserRole::getUserId,
                        Collectors.mapping(SysUserRole::getRoleId, Collectors.toList())
                ));
        Set<Long> allRoleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, String> roleNameMap = allRoleIds.isEmpty()
                ? Map.of()
                : sysRoleMapper.selectBatchIds(allRoleIds).stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(SysRole::getId, SysRole::getRoleName));

        userVos.forEach(vo -> {
            List<Long> roleIds = roleIdsByUserId.getOrDefault(vo.getId(), List.of());
            vo.setRoleIds(roleIds);
            vo.setRoleNames(roleIds.stream()
                    .map(roleNameMap::get)
                    .filter(Objects::nonNull)
                    .toList());
        });
    }

    /**
     * 根据角色ID列表查询角色名称列表
     */
    private List<String> buildRoleNames(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        return sysRoleMapper.selectBatchIds(roleIds).stream()
                .filter(Objects::nonNull)
                .map(SysRole::getRoleName)
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean hasApprovalPermission(Long userId) {
        List<Long> roleIds = getUserRoles(userId);
        if (roleIds.isEmpty()) {
            return false;
        }
        Set<Long> enabledRoleIds = sysRoleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>()
                                .in(SysRole::getId, roleIds)
                                .eq(SysRole::getStatus, BizConstants.STATUS_ENABLED))
                .stream()
                .map(SysRole::getId)
                .collect(Collectors.toSet());
        if (enabledRoleIds.isEmpty()) {
            return false;
        }
        Set<Long> permIds = sysRolePermissionMapper.selectList(
                        new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, enabledRoleIds))
                .stream()
                .map(SysRolePermission::getPermId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (permIds.isEmpty()) {
            return false;
        }
        return sysPermissionMapper.selectList(
                        new LambdaQueryWrapper<SysPermission>()
                                .in(SysPermission::getId, permIds)
                                .eq(SysPermission::getStatus, BizConstants.STATUS_ENABLED))
                .stream()
                .anyMatch(permission -> PermissionConstants.APPROVAL_PENDING_APPROVE.equals(permission.getPermCode()));
    }
}
