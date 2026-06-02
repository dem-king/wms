package com.wms.approval.service.impl;

import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DataScopeConstants;
import com.wms.common.enums.BizTypeEnum;
import com.wms.system.domain.entity.SysPermission;
import com.wms.system.domain.entity.SysRole;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 审批可见性服务测试。
 */
@DisplayName("ApprovalVisibilityServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class ApprovalVisibilityServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Mock
    private SysDepartmentMapper sysDepartmentMapper;

    @Mock
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Mock
    private SysPermissionMapper sysPermissionMapper;

    @Mock
    private WmsInboundOrderMapper wmsInboundOrderMapper;

    @Mock
    private WmsOutboundOrderMapper wmsOutboundOrderMapper;

    @Mock
    private WmsReturnOrderMapper wmsReturnOrderMapper;

    @Mock
    private WmsScrapOrderMapper wmsScrapOrderMapper;

    @Mock
    private WmsTransferOrderMapper wmsTransferOrderMapper;

    @InjectMocks
    private ApprovalVisibilityServiceImpl approvalVisibilityService;

    @Test
    @DisplayName("指定用户有审批权限且仅本人范围命中申请人时可审批")
    void shouldAllowUserApproverWhenSelfScopeMatchesApplicant() {
        WmsApprovalNode node = buildUserNode(2001L);
        WmsOutboundOrder order = buildOutboundOrder(9001L, 2001L, 10L);
        when(wmsOutboundOrderMapper.selectById(9001L)).thenReturn(order);
        mockUserWithRole(2001L, 10L, 3001L, DataScopeConstants.SCOPE_SELF);
        mockRolePermissions(3001L, true);

        assertTrue(approvalVisibilityService.hasVisibleApproverForNode(
                node, 9001L, BizTypeEnum.OUTBOUND.getCode()));
    }

    @Test
    @DisplayName("指定用户缺少审批权限时不可审批")
    void shouldRejectUserApproverWithoutApprovalPermission() {
        WmsApprovalNode node = buildUserNode(2001L);
        WmsOutboundOrder order = buildOutboundOrder(9001L, 2001L, 10L);
        when(wmsOutboundOrderMapper.selectById(9001L)).thenReturn(order);
        mockUserWithRole(2001L, 10L, 3001L, DataScopeConstants.SCOPE_SELF);
        mockRolePermissions(3001L, false);

        assertFalse(approvalVisibilityService.hasVisibleApproverForNode(
                node, 9001L, BizTypeEnum.OUTBOUND.getCode()));
    }

    @Test
    @DisplayName("指定角色至少一个用户可见业务单据时可配置审批")
    void shouldAllowRoleApproverWhenAnyRoleUserCanViewOrder() {
        WmsApprovalNode node = buildRoleNode(3001L);
        WmsOutboundOrder order = buildOutboundOrder(9001L, 2001L, 10L);
        SysUserRole roleUser = new SysUserRole();
        roleUser.setUserId(2001L);
        roleUser.setRoleId(3001L);
        when(wmsOutboundOrderMapper.selectById(9001L)).thenReturn(order);
        when(sysUserRoleMapper.selectList(any())).thenReturn(List.of(roleUser));
        mockUserWithRole(2001L, 10L, 3001L, DataScopeConstants.SCOPE_SELF);
        mockRolePermissions(3001L, true);

        assertTrue(approvalVisibilityService.hasVisibleApproverForNode(
                node, 9001L, BizTypeEnum.OUTBOUND.getCode()));
    }

    private WmsApprovalNode buildUserNode(Long userId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_USER);
        node.setApproverId(userId);
        return node;
    }

    private WmsApprovalNode buildRoleNode(Long roleId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_ROLE);
        node.setApproverId(roleId);
        return node;
    }

    private WmsOutboundOrder buildOutboundOrder(Long id, Long applicantId, Long deptId) {
        WmsOutboundOrder order = new WmsOutboundOrder();
        order.setId(id);
        order.setApplicantId(applicantId);
        order.setDeptId(deptId);
        return order;
    }

    private void mockUserWithRole(Long userId, Long deptId, Long roleId, Integer dataScope) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setDeptId(deptId);
        user.setStatus(BizConstants.STATUS_ENABLED);
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setStatus(BizConstants.STATUS_ENABLED);
        role.setDataScope(dataScope);
        when(sysUserMapper.selectById(userId)).thenReturn(user);
        when(sysUserRoleMapper.selectList(any())).thenReturn(List.of(userRole));
        when(sysRoleMapper.selectList(any())).thenReturn(List.of(role));
    }

    private void mockRolePermissions(Long roleId, boolean allowed) {
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermId(4001L);
        SysPermission permission = new SysPermission();
        permission.setId(4001L);
        permission.setPermCode(allowed ? "approval:pending:approve" : "approval:history:list");
        permission.setStatus(BizConstants.STATUS_ENABLED);
        when(sysRolePermissionMapper.selectList(any())).thenReturn(List.of(rolePermission));
        when(sysPermissionMapper.selectList(any())).thenReturn(List.of(permission));
    }
}
