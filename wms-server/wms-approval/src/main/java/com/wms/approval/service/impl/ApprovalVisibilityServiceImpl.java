package com.wms.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.service.ApprovalVisibilityService;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DataScopeConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.constant.PermissionConstants;
import com.wms.common.enums.BizTypeEnum;
import com.wms.system.domain.entity.SysDepartment;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysRoleDept;
import com.wms.system.domain.entity.SysRolePermission;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.mapper.SysDepartmentMapper;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.system.mapper.SysRoleDeptMapper;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 审批数据可见性服务实现类。
 * 复用系统角色数据范围规则校验审批人是否能看到具体业务单据。
 */
@Service
@RequiredArgsConstructor
public class ApprovalVisibilityServiceImpl implements ApprovalVisibilityService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRoleDeptMapper sysRoleDeptMapper;
    private final SysDepartmentMapper sysDepartmentMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsTransferOrderMapper wmsTransferOrderMapper;

    /**
     * 判断审批节点是否至少存在一个可见该业务单据的审批人。
     *
     * @param node    审批节点
     * @param bizId   业务单据ID
     * @param bizType 业务类型
     * @return 是否存在可见审批人
     */
    @Override
    public boolean hasVisibleApproverForNode(WmsApprovalNode node, Long bizId, Integer bizType) {
        if (node == null || node.getApproverType() == null) {
            return false;
        }
        BizResource resource = resolveBizResource(bizId, bizType);
        if (resource == null) {
            return false;
        }
        if (Objects.equals(node.getApproverType(), ApprovalConstants.APPROVER_TYPE_USER)) {
            return canUserApprove(node.getApproverId(), resource);
        }
        if (Objects.equals(node.getApproverType(), ApprovalConstants.APPROVER_TYPE_ROLE)) {
            return findEnabledUserIdsByRoleId(node.getApproverId()).stream()
                    .anyMatch(userId -> canUserApprove(userId, resource));
        }
        return false;
    }

    /**
     * 判断指定用户是否可审批该审批单。
     *
     * @param userId 审批人ID
     * @param order  审批单
     * @return 是否可审批
     */
    @Override
    public boolean canApprove(Long userId, WmsApprovalOrder order) {
        if (order == null || userId == null) {
            return false;
        }
        BizResource resource = resolveBizResource(order.getBizId(), order.getBizType());
        return resource != null && canUserApprove(userId, resource);
    }

    private boolean canUserApprove(Long userId, BizResource resource) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || Objects.equals(user.getDelFlag(), DelFlagConstants.DELETED)
                || !Objects.equals(user.getStatus(), BizConstants.STATUS_ENABLED)) {
            return false;
        }
        List<SysRole> roles = getEnabledRoles(userId);
        if (roles.isEmpty() || !hasApprovalPermission(roles)) {
            return false;
        }
        if (roles.stream().anyMatch(role -> safeScope(role) == DataScopeConstants.SCOPE_ALL)) {
            return true;
        }
        for (SysRole role : roles) {
            int scope = safeScope(role);
            if (scope == DataScopeConstants.SCOPE_SELF && matchesSelfScope(userId, resource)) {
                return true;
            }
            if (scope == DataScopeConstants.SCOPE_DEPT && matchesDeptScope(user.getDeptId(), resource)) {
                return true;
            }
            if (scope == DataScopeConstants.SCOPE_DEPT_AND_CHILD
                    && matchesAnyDept(getDeptAndChildren(user.getDeptId()), resource)) {
                return true;
            }
            if (scope == DataScopeConstants.SCOPE_CUSTOM
                    && matchesAnyDept(getCustomDeptIds(role.getId()), resource)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesSelfScope(Long userId, BizResource resource) {
        return Objects.equals(userId, resource.applicantId()) || Objects.equals(userId, resource.operatorId());
    }

    private boolean matchesDeptScope(Long deptId, BizResource resource) {
        return deptId != null && Objects.equals(deptId, resource.deptId());
    }

    private boolean matchesAnyDept(Collection<Long> deptIds, BizResource resource) {
        return resource.deptId() != null && deptIds != null && deptIds.contains(resource.deptId());
    }

    private List<SysRole> getEnabledRoles(Long userId) {
        List<Long> roleIds = sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getStatus, BizConstants.STATUS_ENABLED));
    }

    private boolean hasApprovalPermission(List<SysRole> roles) {
        Set<Long> roleIds = roles.stream().map(SysRole::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return false;
        }
        Set<Long> permIds = sysRolePermissionMapper.selectList(
                        new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, roleIds))
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

    private List<Long> findEnabledUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return List.of();
        }
        return sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId))
                .stream()
                .map(SysUserRole::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .filter(this::isEnabledUser)
                .toList();
    }

    private boolean isEnabledUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        return user != null && !Objects.equals(user.getDelFlag(), DelFlagConstants.DELETED)
                && Objects.equals(user.getStatus(), BizConstants.STATUS_ENABLED);
    }

    private Set<Long> getCustomDeptIds(Long roleId) {
        if (roleId == null) {
            return Set.of();
        }
        return sysRoleDeptMapper.selectList(
                        new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId))
                .stream()
                .map(SysRoleDept::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Long> getDeptAndChildren(Long deptId) {
        if (deptId == null) {
            return Set.of();
        }
        List<SysDepartment> departments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<>());
        Set<Long> result = new HashSet<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(deptId);
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (!result.add(current)) {
                continue;
            }
            departments.stream()
                    .filter(dept -> Objects.equals(dept.getParentId(), current))
                    .map(SysDepartment::getId)
                    .filter(Objects::nonNull)
                    .forEach(queue::add);
        }
        return result;
    }

    private int safeScope(SysRole role) {
        return role.getDataScope() == null ? DataScopeConstants.SCOPE_SELF : role.getDataScope();
    }

    private BizResource resolveBizResource(Long bizId, Integer bizType) {
        BizTypeEnum type = BizTypeEnum.of(bizType);
        if (type == null || bizId == null) {
            return null;
        }
        return switch (type) {
            case INBOUND -> resolveInbound(bizId);
            case OUTBOUND -> resolveOutbound(bizId);
            case SCRAP -> resolveScrap(bizId);
            case TRANSFER -> resolveTransfer(bizId);
            case RETURN -> resolveReturn(bizId);
        };
    }

    private BizResource resolveInbound(Long bizId) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(bizId);
        if (order == null || Objects.equals(order.getDelFlag(), DelFlagConstants.DELETED)) {
            return null;
        }
        return new BizResource(null, null, order.getOperatorId());
    }

    private BizResource resolveOutbound(Long bizId) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(bizId);
        if (order == null || Objects.equals(order.getDelFlag(), DelFlagConstants.DELETED)) {
            return null;
        }
        return new BizResource(order.getApplicantId(), order.getDeptId(), order.getOperatorId());
    }

    private BizResource resolveScrap(Long bizId) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(bizId);
        if (order == null || Objects.equals(order.getDelFlag(), DelFlagConstants.DELETED)) {
            return null;
        }
        return new BizResource(order.getApplicantId(), null, null);
    }

    private BizResource resolveTransfer(Long bizId) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(bizId);
        if (order == null || Objects.equals(order.getDelFlag(), DelFlagConstants.DELETED)) {
            return null;
        }
        return new BizResource(order.getApplicantId(), null, null);
    }

    private BizResource resolveReturn(Long bizId) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(bizId);
        if (order == null || Objects.equals(order.getDelFlag(), DelFlagConstants.DELETED)
                || order.getOutboundOrderId() == null) {
            return null;
        }
        return resolveOutbound(order.getOutboundOrderId());
    }

    private record BizResource(Long applicantId, Long deptId, Long operatorId) {
    }
}
