package com.wms.approval.task;

import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.entity.WmsApprovalRecord;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.approval.mapper.WmsApprovalRecordMapper;
import com.wms.common.event.ApprovalResultEvent;
import com.wms.system.domain.dto.SysMessageCreateDto;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 审批超时检查任务测试。
 * 验证超时提醒和自动取消都会留下可追溯的系统审批记录。
 */
@DisplayName("ApprovalTimeoutCheckTask 测试")
@ExtendWith(MockitoExtension.class)
class ApprovalTimeoutCheckTaskTest {

    @Mock
    private WmsApprovalOrderMapper wmsApprovalOrderMapper;

    @Mock
    private WmsApprovalConfigMapper wmsApprovalConfigMapper;

    @Mock
    private WmsApprovalRecordMapper wmsApprovalRecordMapper;

    @Mock
    private WmsApprovalNodeMapper wmsApprovalNodeMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @Mock
    private SysMessageService sysMessageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ApprovalTimeoutCheckTask approvalTimeoutCheckTask;

    @Test
    @DisplayName("超时提醒配置应写入系统提醒记录")
    void shouldInsertSystemReminderRecordWhenApprovalTimesOut() {
        WmsApprovalOrder order = timeoutOrder();
        WmsApprovalConfig config = timeoutConfig(ApprovalConstants.TIMEOUT_ACTION_REMIND);
        when(wmsApprovalOrderMapper.selectList(any())).thenReturn(List.of(order));
        when(wmsApprovalConfigMapper.selectList(any())).thenReturn(List.of(config));

        approvalTimeoutCheckTask.check();

        ArgumentCaptor<WmsApprovalRecord> recordCaptor = ArgumentCaptor.forClass(WmsApprovalRecord.class);
        verify(wmsApprovalRecordMapper).insert(recordCaptor.capture());
        WmsApprovalRecord record = recordCaptor.getValue();
        assertEquals(order.getId(), record.getApprovalId());
        assertEquals(order.getCurrentStep(), record.getStepOrder());
        assertEquals(ApprovalConstants.SYSTEM_APPROVER_NAME, record.getApproverName());
        assertNull(record.getResult());
        assertTrue(record.getOpinion().contains(ApprovalConstants.TIMEOUT_REMIND_OPINION));
        assertNotNull(record.getApproveTime());
        verify(wmsApprovalOrderMapper, never()).updateById(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("超时提醒应给当前指定用户审批人发送站内信")
    void shouldSendMessageToCurrentUserApproverWhenApprovalTimesOut() {
        WmsApprovalOrder order = timeoutOrder();
        WmsApprovalConfig config = timeoutConfig(ApprovalConstants.TIMEOUT_ACTION_REMIND);
        when(wmsApprovalOrderMapper.selectList(any())).thenReturn(List.of(order));
        when(wmsApprovalConfigMapper.selectList(any())).thenReturn(List.of(config));
        when(wmsApprovalNodeMapper.selectList(any())).thenReturn(List.of(userNode(config.getId(), order.getCurrentStep(), 1001L)));

        approvalTimeoutCheckTask.check();

        ArgumentCaptor<SysMessageCreateDto> messageCaptor = ArgumentCaptor.forClass(SysMessageCreateDto.class);
        verify(sysMessageService).createIfAbsent(messageCaptor.capture());
        SysMessageCreateDto message = messageCaptor.getValue();
        assertEquals(1001L, message.getReceiverId());
        assertTrue(message.getBusinessKey().contains(String.valueOf(order.getId())));
        assertEquals("/approval/pending?approvalId=701", message.getTargetUrl());
    }

    @Test
    @DisplayName("超时提醒应给当前角色审批节点下的所有用户发送站内信")
    void shouldSendMessageToRoleUsersWhenApprovalTimesOut() {
        WmsApprovalOrder order = timeoutOrder();
        WmsApprovalConfig config = timeoutConfig(ApprovalConstants.TIMEOUT_ACTION_REMIND);
        when(wmsApprovalOrderMapper.selectList(any())).thenReturn(List.of(order));
        when(wmsApprovalConfigMapper.selectList(any())).thenReturn(List.of(config));
        when(wmsApprovalNodeMapper.selectList(any()))
                .thenReturn(List.of(roleNode(config.getId(), order.getCurrentStep(), 3001L)));
        when(sysUserRoleMapper.selectList(any()))
                .thenReturn(List.of(userRole(1001L, 3001L), userRole(1002L, 3001L), userRole(1001L, 3001L)));

        approvalTimeoutCheckTask.check();

        ArgumentCaptor<SysMessageCreateDto> messageCaptor = ArgumentCaptor.forClass(SysMessageCreateDto.class);
        verify(sysMessageService, org.mockito.Mockito.times(2)).createIfAbsent(messageCaptor.capture());
        Set<Long> receiverIds = messageCaptor.getAllValues().stream()
                .map(SysMessageCreateDto::getReceiverId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(1001L, 1002L), receiverIds);
    }

    @Test
    @DisplayName("超时取消配置应写入系统驳回记录并发布业务回退事件")
    void shouldInsertSystemRejectedRecordAndPublishEventWhenApprovalTimeoutCancels() {
        WmsApprovalOrder order = timeoutOrder();
        WmsApprovalConfig config = timeoutConfig(ApprovalConstants.TIMEOUT_ACTION_CANCEL);
        when(wmsApprovalOrderMapper.selectList(any())).thenReturn(List.of(order));
        when(wmsApprovalConfigMapper.selectList(any())).thenReturn(List.of(config));

        approvalTimeoutCheckTask.check();

        ArgumentCaptor<WmsApprovalRecord> recordCaptor = ArgumentCaptor.forClass(WmsApprovalRecord.class);
        verify(wmsApprovalRecordMapper).insert(recordCaptor.capture());
        WmsApprovalRecord record = recordCaptor.getValue();
        assertEquals(order.getId(), record.getApprovalId());
        assertEquals(order.getCurrentStep(), record.getStepOrder());
        assertEquals(ApprovalConstants.SYSTEM_APPROVER_NAME, record.getApproverName());
        assertEquals(ApprovalConstants.RESULT_REJECTED, record.getResult());
        assertTrue(record.getOpinion().contains(ApprovalConstants.TIMEOUT_CANCEL_OPINION));
        assertNotNull(record.getApproveTime());
        verify(wmsApprovalOrderMapper).updateById(order);
        verify(eventPublisher).publishEvent(any(ApprovalResultEvent.class));
    }

    private WmsApprovalOrder timeoutOrder() {
        WmsApprovalOrder order = new WmsApprovalOrder();
        order.setId(701L);
        order.setBizId(801L);
        order.setBizType(5);
        order.setStatus(ApprovalConstants.STATUS_APPROVING);
        order.setCurrentStep(2);
        order.setCreateTime(LocalDateTime.now().minusHours(3));
        return order;
    }

    private WmsApprovalConfig timeoutConfig(Integer timeoutAction) {
        WmsApprovalConfig config = new WmsApprovalConfig();
        config.setId(901L);
        config.setBizType(5);
        config.setTimeoutHours(1);
        config.setTimeoutAction(timeoutAction);
        return config;
    }

    private WmsApprovalNode userNode(Long configId, Integer stepOrder, Long approverId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setConfigId(configId);
        node.setStepOrder(stepOrder);
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_USER);
        node.setApproverId(approverId);
        return node;
    }

    private WmsApprovalNode roleNode(Long configId, Integer stepOrder, Long roleId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setConfigId(configId);
        node.setStepOrder(stepOrder);
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_ROLE);
        node.setApproverId(roleId);
        return node;
    }

    private SysUserRole userRole(Long userId, Long roleId) {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRole;
    }
}
