package com.wms.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.exception.BizException;
import com.wms.system.converter.SysMessageConverter;
import com.wms.system.domain.constant.SysMessageConstants;
import com.wms.system.domain.dto.SysMessageCreateDto;
import com.wms.system.domain.dto.SysMessageQueryDto;
import com.wms.system.domain.entity.SysMessage;
import com.wms.system.mapper.SysMessageMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 站内信服务测试。
 */
@DisplayName("SysMessageServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class SysMessageServiceImplTest {

    @Mock
    private SysMessageMapper sysMessageMapper;

    @Spy
    private SysMessageConverter sysMessageConverter = new SysMessageConverter();

    @InjectMocks
    private SysMessageServiceImpl sysMessageService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("未读数量只统计当前用户未读消息")
    void shouldCountUnreadMessagesForCurrentUserOnly() {
        loginAs(1001L);
        when(sysMessageMapper.selectCount(any())).thenReturn(3L);

        Long count = sysMessageService.countUnread();

        assertEquals(3L, count);
        verify(sysMessageMapper).selectCount(any());
    }

    @Test
    @DisplayName("分页查询必须限定当前用户")
    void shouldPageCurrentUserMessages() {
        loginAs(1001L);
        SysMessageQueryDto query = new SysMessageQueryDto();
        query.setReadStatus(SysMessageConstants.READ_STATUS_UNREAD);
        when(sysMessageMapper.selectPage(any(), any())).thenReturn(new Page<SysMessage>(1, 20));

        sysMessageService.pageMessages(new PageParam(), query);

        verify(sysMessageMapper).selectPage(any(), any());
    }

    @Test
    @DisplayName("单条已读不能操作其他用户消息")
    void shouldRejectMarkReadForOtherUserMessage() {
        loginAs(1001L);
        SysMessage message = new SysMessage();
        message.setId(9001L);
        message.setReceiverId(2002L);
        message.setReadStatus(SysMessageConstants.READ_STATUS_UNREAD);
        when(sysMessageMapper.selectById(9001L)).thenReturn(message);

        assertThrows(BizException.class, () -> sysMessageService.markRead(9001L));

        verify(sysMessageMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("全部已读只更新当前用户未读消息")
    void shouldMarkAllUnreadMessagesForCurrentUserOnly() {
        loginAs(1001L);

        sysMessageService.markAllRead();

        ArgumentCaptor<SysMessage> messageCaptor = ArgumentCaptor.forClass(SysMessage.class);
        verify(sysMessageMapper).update(messageCaptor.capture(), any());
        assertEquals(SysMessageConstants.READ_STATUS_READ, messageCaptor.getValue().getReadStatus());
    }

    @Test
    @DisplayName("新增站内信对同一接收人和业务键幂等")
    void shouldCreateMessageOnlyWhenBusinessKeyNotExists() {
        SysMessageCreateDto dto = createApprovalTimeoutMessage();
        when(sysMessageMapper.selectCount(any())).thenReturn(0L);

        boolean created = sysMessageService.createIfAbsent(dto);

        assertTrue(created);
        ArgumentCaptor<SysMessage> messageCaptor = ArgumentCaptor.forClass(SysMessage.class);
        verify(sysMessageMapper).insert(messageCaptor.capture());
        assertEquals(1001L, messageCaptor.getValue().getReceiverId());
        assertEquals(SysMessageConstants.READ_STATUS_UNREAD, messageCaptor.getValue().getReadStatus());
    }

    @Test
    @DisplayName("业务键已存在时不重复写入站内信")
    void shouldSkipDuplicateMessageWhenBusinessKeyExists() {
        SysMessageCreateDto dto = createApprovalTimeoutMessage();
        when(sysMessageMapper.selectCount(any())).thenReturn(1L);

        boolean created = sysMessageService.createIfAbsent(dto);

        assertFalse(created);
        verify(sysMessageMapper, never()).insert(any());
    }

    private SysMessageCreateDto createApprovalTimeoutMessage() {
        SysMessageCreateDto dto = new SysMessageCreateDto();
        dto.setReceiverId(1001L);
        dto.setTitle("审批超时提醒");
        dto.setContent("审批单SP701已超时，请及时处理");
        dto.setMessageType(SysMessageConstants.TYPE_APPROVAL_TIMEOUT);
        dto.setMessageLevel(SysMessageConstants.LEVEL_WARNING);
        dto.setBusinessKey("approval-timeout:701");
        dto.setTargetUrl("/approval/pending?approvalId=701");
        return dto;
    }

    private void loginAs(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
