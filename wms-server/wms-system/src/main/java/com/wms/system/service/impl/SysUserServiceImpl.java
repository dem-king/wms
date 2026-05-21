package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.system.domain.dto.SysUserDto;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.domain.vo.SysUserVo;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 处理用户CRUD、密码重置、状态切换、角色分配等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    /** 默认重置密码，从配置文件读取 */
    @Value("${wms.default-password:Wms@2024}")
    private String defaultPassword;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );
    }

    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(newPasswordHash);
        return sysUserMapper.updateById(user) > 0;
    }

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

        PageResult<SysUserVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    @Override
    public SysUserVo getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户不存在");
        }
        return toVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVo create(SysUserDto dto) {
        // 校验用户名唯一性
        checkUsernameUnique(dto.getUsername(), null);
        SysUser user = new SysUser();
        copyDtoToEntity(dto, user);
        // 密码使用BCrypt加密存储
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = dto.getPassword() != null ? dto.getPassword() : defaultPassword;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserVo update(Long id, SysUserDto dto) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户不存在");
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户不存在");
        }
        // 逻辑删除用户
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setDelFlag(DelFlagConstants.DELETED);
  
        sysUserMapper.updateById(updateUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户不存在");
        }
        // 使用BCrypt加密默认密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(encoder.encode(defaultPassword));
        sysUserMapper.updateById(updateUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || user.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("用户不存在");
        }
        // 切换状态: 0->1, 1->0
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setStatus(user.getStatus() == BizConstants.STATUS_ENABLED ? BizConstants.STATUS_DISABLED : BizConstants.STATUS_ENABLED);
        sysUserMapper.updateById(updateUser);
    }

    @Override
    public List<Long> getUserRoles(Long id) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );
        return userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long id, List<Long> roleIds) {
        // 先逻辑删除旧的用户角色关联
        List<SysUserRole> oldUserRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, id)
        );
        for (SysUserRole oldRole : oldUserRoles) {
            SysUserRole updateRole = new SysUserRole();
            updateRole.setId(oldRole.getId());
            updateRole.setDelFlag(DelFlagConstants.DELETED);
 
            sysUserRoleMapper.updateById(updateRole);
        }
        // 批量插入新的用户角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(id, roleIds);
        }
    }

    /**
     * 批量保存用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
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
}
