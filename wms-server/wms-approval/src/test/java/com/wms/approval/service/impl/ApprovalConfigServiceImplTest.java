package com.wms.approval.service.impl;

import com.wms.approval.converter.ApprovalConfigConverter;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.dto.ApprovalConfigDto;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
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

    @Spy
    private ApprovalConfigConverter approvalConfigConverter = new ApprovalConfigConverter();

    @InjectMocks
    private ApprovalConfigServiceImpl approvalConfigService;

    @Test
    @DisplayName("创建配置时未传开关值应写入启用和非免审默认值")
    void shouldApplyDefaultEnabledAndAutoApproveWhenValuesMissing() {
        ApprovalConfigDto dto = new ApprovalConfigDto();
        dto.setBizType(5);
        dto.setConfigName("归还审批");
        dto.setRemark("默认配置");
        dto.setNodes(List.of());

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
            config.setAutoApprove(BizConstants.STATUS_DISABLED);
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
        assertEquals(BizConstants.STATUS_DISABLED, configCaptor.getValue().getAutoApprove());
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
        dto.setNodes(List.of());
        when(wmsApprovalConfigMapper.selectCount(any())).thenReturn(1L);

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
        dto.setNodes(List.of());

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
        dto.setNodes(List.of());

        approvalConfigService.updateConfig(602L, dto);

        verify(wmsApprovalNodeMapper).updateDelFlagByConfigId(
                602L,
                DelFlagConstants.DELETED,
                SecurityUtil.getCurrentUsername()
        );
    }

}
