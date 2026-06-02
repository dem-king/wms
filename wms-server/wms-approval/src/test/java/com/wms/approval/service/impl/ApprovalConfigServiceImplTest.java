package com.wms.approval.service.impl;

import com.wms.approval.converter.ApprovalConfigConverter;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.dto.ApprovalConfigDto;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
import com.wms.system.domain.entity.SysRole;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.mapper.SysRoleMapper;
import com.wms.system.mapper.SysRolePermissionMapper;
import com.wms.system.mapper.SysUserMapper;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.mapper.SysPermissionMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 审批配置服务单元测试
 * 验证审批配置默认值写入行为
 */
@DisplayName("ApprovalConfigServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class ApprovalConfigServiceImplTest {

    @Mock
    private WmsApprovalConfigMapper wmsApprovalConfigMapper;

    @Mock
    private WmsApprovalNodeMapper wmsApprovalNodeMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Mock
    private SysPermissionMapper sysPermissionMapper;

    @Spy
    private ApprovalConfigConverter approvalConfigConverter = new ApprovalConfigConverter();

    @InjectMocks
    private ApprovalConfigServiceImpl approvalConfigService;

    @Test
    @DisplayName("创建免审配置时未传启用和超时值应写入默认值")
    void shouldApplyDefaultEnabledAndAutoApproveWhenValuesMissing() {
        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setRemark("默认配置");
        dto.setAutoApprove(BizConstants.STATUS_ENABLED);

        doAnswer(invocation -> {
            WmsApprovalConfig config = invocation.getArgument(0);
            config.setId(501L);
            return 1;
        }).when(wmsApprovalConfigMapper).insert(any(WmsApprovalConfig.class));
        when(wmsApprovalConfigMapper.selectById(501L)).thenAnswer(invocation -> {
            WmsApprovalConfig config = new WmsApprovalConfig();
            config.setId(501L);
            config.setBizType(dto.getBizType());
            config.setEnabled(BizConstants.STATUS_ENABLED);
            config.setAutoApprove(BizConstants.STATUS_ENABLED);
            config.setConfigName(dto.getConfigName());
            config.setRemark(dto.getRemark());
            config.setDelFlag(DelFlagConstants.NORMAL);
            return config;
        });
        when(wmsApprovalNodeMapper.selectList(any())).thenReturn(List.of());

        approvalConfigService.createConfig(dto);

        ArgumentCaptor<WmsApprovalConfig> configCaptor = ArgumentCaptor.forClass(WmsApprovalConfig.class);
        verify(wmsApprovalConfigMapper).insert(configCaptor.capture());
        assertEquals(BizConstants.STATUS_ENABLED, configCaptor.getValue().getEnabled());
        assertEquals(BizConstants.STATUS_ENABLED, configCaptor.getValue().getAutoApprove());
        assertEquals(ApprovalConstants.DEFAULT_TIMEOUT_HOURS, configCaptor.getValue().getTimeoutHours());
        assertEquals(ApprovalConstants.TIMEOUT_ACTION_REMIND, configCaptor.getValue().getTimeoutAction());
    }

    @Test
    @DisplayName("创建启用配置时同一业务类型已有启用配置应拒绝")
    void shouldRejectDuplicateEnabledConfigForSameBizType() {
        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setEnabled(BizConstants.STATUS_ENABLED);
        dto.setAutoApprove(BizConstants.STATUS_ENABLED);
        when(wmsApprovalConfigMapper.selectCount(any())).thenReturn(1L);

        assertThrows(com.wms.common.exception.BizException.class, () -> approvalConfigService.createConfig(dto));

        verify(wmsApprovalConfigMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建配置时应拒绝库房管理员等不支持的审批人类型")
    void shouldRejectUnsupportedApproverTypeWhenCreatingConfig() {
        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setEnabled(BizConstants.STATUS_DISABLED);
        dto.setNodes(List.of(buildNode(1, ApprovalConstants.APPROVER_TYPE_WAREHOUSE_ADMIN, null)));

        assertThrows(com.wms.common.exception.BizException.class, () -> approvalConfigService.createConfig(dto));

        verify(wmsApprovalConfigMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建配置时指定用户不存在应拒绝")
    void shouldRejectMissingUserApproverWhenCreatingConfig() {
        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setEnabled(BizConstants.STATUS_DISABLED);
        dto.setNodes(List.of(buildNode(1, ApprovalConstants.APPROVER_TYPE_USER, 2001L)));
        when(sysUserMapper.selectById(2001L)).thenReturn(null);

        assertThrows(com.wms.common.exception.BizException.class, () -> approvalConfigService.createConfig(dto));

        verify(wmsApprovalConfigMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建配置时指定角色禁用应拒绝")
    void shouldRejectDisabledRoleApproverWhenCreatingConfig() {
        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setEnabled(BizConstants.STATUS_DISABLED);
        dto.setNodes(List.of(buildNode(1, ApprovalConstants.APPROVER_TYPE_ROLE, 3001L)));
        SysRole role = new SysRole();
        role.setId(3001L);
        role.setStatus(BizConstants.STATUS_DISABLED);
        role.setDelFlag(DelFlagConstants.NORMAL);
        when(sysRoleMapper.selectById(3001L)).thenReturn(role);

        assertThrows(com.wms.common.exception.BizException.class, () -> approvalConfigService.createConfig(dto));

        verify(wmsApprovalConfigMapper, never()).insert(any());
    }

    @Test
    @DisplayName("更新为启用配置时同一业务类型已有其他启用配置应拒绝")
    void shouldRejectUpdateToDuplicateEnabledConfigForSameBizType() {
        WmsApprovalConfig existingConfig = new WmsApprovalConfig();
        existingConfig.setId(601L);
        existingConfig.setBizType(5);
        existingConfig.setEnabled(BizConstants.STATUS_DISABLED);
        existingConfig.setDelFlag(DelFlagConstants.NORMAL);
        when(wmsApprovalConfigMapper.selectById(601L)).thenReturn(existingConfig);
        when(wmsApprovalConfigMapper.selectCount(any())).thenReturn(1L);

        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setEnabled(BizConstants.STATUS_ENABLED);
        dto.setAutoApprove(BizConstants.STATUS_ENABLED);

        assertThrows(com.wms.common.exception.BizException.class, () -> approvalConfigService.updateConfig(601L, dto));

        verify(wmsApprovalConfigMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("更新配置时应显式按配置逻辑删除旧节点，避免旧节点反复累积")
    void shouldExplicitlyDeleteOldNodesByConfigIdWhenUpdatingConfig() {
        WmsApprovalConfig existingConfig = new WmsApprovalConfig();
        existingConfig.setId(602L);
        existingConfig.setBizType(1);
        existingConfig.setEnabled(BizConstants.STATUS_DISABLED);
        existingConfig.setDelFlag(DelFlagConstants.NORMAL);
        when(wmsApprovalConfigMapper.selectById(602L)).thenReturn(existingConfig);
        when(wmsApprovalConfigMapper.updateById(any(WmsApprovalConfig.class))).thenReturn(1);
        when(wmsApprovalNodeMapper.updateDelFlagByConfigId(602L, DelFlagConstants.DELETED, SecurityUtil.getCurrentUsername()))
                .thenReturn(2);
        when(wmsApprovalNodeMapper.selectList(any())).thenReturn(List.of());

        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(1);
        dto.setConfigName("入库审批");
        dto.setEnabled(BizConstants.STATUS_DISABLED);
        dto.setAutoApprove(BizConstants.STATUS_ENABLED);

        approvalConfigService.updateConfig(602L, dto);

        verify(wmsApprovalNodeMapper).updateDelFlagByConfigId(
                602L,
                DelFlagConstants.DELETED,
                SecurityUtil.getCurrentUsername()
        );
    }

    private ApprovalConfigDto.ApprovalNodeDto buildNode(int stepOrder, Integer approverType, Long approverId) {
        ApprovalConfigDto.ApprovalNodeDto node = new ApprovalConfigDto.ApprovalNodeDto();
        node.setStepOrder(stepOrder);
        node.setNodeName("审批节点" + stepOrder);
        node.setApproverType(approverType);
        node.setApproverId(approverId);
        return node;
    }

}
