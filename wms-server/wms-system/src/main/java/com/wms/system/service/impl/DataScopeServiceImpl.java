package com.wms.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DataScopeConstants;
import com.wms.common.datascope.DataScopeCondition;
import com.wms.common.util.SecurityUtil;
import com.wms.system.domain.entity.SysDepartment;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysRoleDept;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.mapper.SysDepartmentMapper;
import com.wms.system.mapper.SysRoleDeptMapper;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.DataScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据范围服务实现类。
 */
@Service
@RequiredArgsConstructor
public class DataScopeServiceImpl implements DataScopeService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRoleDeptMapper sysRoleDeptMapper;
    private final SysDepartmentMapper sysDepartmentMapper;

    /**
     * 获取当前登录用户的数据范围条件。
     *
     * @return 数据范围条件
     */
    @Override
    public DataScopeCondition getCurrentDataScope() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return DataScopeCondition.restricted(Collections.emptySet(), null, false);
        }
        SysUser user = sysUserMapper.selectById(userId);
        Long currentDeptId = user == null ? null : user.getDeptId();
        List<SysRole> roles = getEnabledRoles(userId);
        if (roles.isEmpty()) {
            return DataScopeCondition.restricted(Collections.emptySet(), userId, true);
        }
        if (roles.stream().anyMatch(role -> DataScopeConstants.SCOPE_ALL == safeScope(role))) {
            return DataScopeCondition.all();
        }

        Set<Long> deptIds = new HashSet<>();
        boolean selfScope = false;
        Set<Long> customRoleIds = new HashSet<>();
        for (SysRole role : roles) {
            int scope = safeScope(role);
            if (scope == DataScopeConstants.SCOPE_CUSTOM) {
                customRoleIds.add(role.getId());
            } else if (scope == DataScopeConstants.SCOPE_DEPT && currentDeptId != null) {
                deptIds.add(currentDeptId);
            } else if (scope == DataScopeConstants.SCOPE_DEPT_AND_CHILD && currentDeptId != null) {
                deptIds.addAll(getDeptAndChildren(currentDeptId));
            } else if (scope == DataScopeConstants.SCOPE_SELF) {
                selfScope = true;
            }
        }
        deptIds.addAll(getCustomDeptIds(customRoleIds));
        return DataScopeCondition.restricted(deptIds, userId, selfScope);
    }

    private List<SysRole> getEnabledRoles(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).distinct().toList();
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getStatus, BizConstants.STATUS_ENABLED)
        );
    }

    private Set<Long> getCustomDeptIds(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        return sysRoleDeptMapper.selectList(
                new LambdaQueryWrapper<SysRoleDept>()
                        .in(SysRoleDept::getRoleId, roleIds)
        ).stream().map(SysRoleDept::getDeptId).collect(Collectors.toSet());
    }

    private Set<Long> getDeptAndChildren(Long deptId) {
        List<SysDepartment> departments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>());
        Map<Long, List<SysDepartment>> childrenByParentId = departments.stream()
                .collect(Collectors.groupingBy(SysDepartment::getParentId));
        Set<Long> result = new HashSet<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(deptId);
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (!result.add(current)) {
                continue;
            }
            for (SysDepartment child : childrenByParentId.getOrDefault(current, Collections.emptyList())) {
                queue.add(child.getId());
            }
        }
        return result;
    }

    private int safeScope(SysRole role) {
        return role.getDataScope() == null ? DataScopeConstants.SCOPE_SELF : role.getDataScope();
    }
}
