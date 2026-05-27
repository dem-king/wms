package com.wms.approval.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.approval.converter.ApprovalOrderConverter;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.dto.ApprovalActionDto;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.entity.WmsApprovalRecord;
import com.wms.approval.domain.vo.ApprovalOrderVo;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.approval.mapper.WmsApprovalRecordMapper;
import com.wms.approval.strategy.ApprovalStrategy;
import com.wms.approval.strategy.ApprovalStrategyFactory;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.event.ApprovalResultEvent;
import com.wms.common.exception.BizException;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.mapper.SysUserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 审批服务单元测试
 * 验证审批配置启用查询、步骤推进和审批/撤回权限校验
 */
@DisplayName("ApprovalServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class ApprovalServiceImplTest {

    @Mock
    private WmsApprovalOrderMapper wmsApprovalOrderMapper;

    @Mock
    private WmsApprovalRecordMapper wmsApprovalRecordMapper;

    @Mock
    private WmsApprovalConfigMapper wmsApprovalConfigMapper;

    @Mock
    private WmsApprovalNodeMapper wmsApprovalNodeMapper;

    @Spy
    private ApprovalOrderConverter approvalOrderConverter = new ApprovalOrderConverter();

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ApprovalStrategyFactory approvalStrategyFactory;

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

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private ApprovalServiceImpl approvalService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("免审配置启用且无节点时应直接发起审批")
    void shouldStartApprovalWhenAutoApproveEnabledWithoutNodes() {
        setCurrentUser(1001L, "applicant");
        WmsApprovalConfig config = buildConfig(501L, 5);
        config.setAutoApprove(BizConstants.STATUS_ENABLED);
        ApprovalStrategy strategy = mock(ApprovalStrategy.class);
        ApprovalOrderVo expected = new ApprovalOrderVo();
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectList(any())).thenReturn(List.of());
        when(approvalStrategyFactory.getStrategy(config, 0)).thenReturn(strategy);
        when(strategy.execute(any())).thenReturn(expected);

        ApprovalOrderVo result = approvalService.startApproval(9001L, 5);

        assertSame(expected, result);
        verify(approvalStrategyFactory).getStrategy(config, 0);
    }

    @Test
    @DisplayName("当前审批人通过中间步骤时应推进到下一步")
    void shouldAdvanceToNextStepWhenCurrentApproverApprovesIntermediateStep() {
        setCurrentUser(2001L, "approver-a");
        WmsApprovalOrder order = buildApprovingOrder(8001L, 9001L, 5, 1001L, 1, 2);
        WmsApprovalConfig config = buildConfig(501L, order.getBizType());
        WmsApprovalNode currentNode = buildUserNode(config.getId(), 1, 2001L);
        when(wmsApprovalOrderMapper.selectById(8001L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        approvalService.approve(8001L, buildActionDto("同意"));

        ArgumentCaptor<WmsApprovalRecord> recordCaptor = ArgumentCaptor.forClass(WmsApprovalRecord.class);
        ArgumentCaptor<WmsApprovalOrder> orderCaptor = ArgumentCaptor.forClass(WmsApprovalOrder.class);
        verify(wmsApprovalConfigMapper).selectOne(any());
        verify(wmsApprovalNodeMapper).selectOne(any());
        verify(wmsApprovalRecordMapper).insert(recordCaptor.capture());
        verify(wmsApprovalOrderMapper).updateById(orderCaptor.capture());
        verify(eventPublisher, never()).publishEvent(any());
        assertEquals(1, recordCaptor.getValue().getStepOrder());
        assertEquals(2001L, recordCaptor.getValue().getApproverId());
        assertEquals("approver-a", recordCaptor.getValue().getApproverName());
        assertEquals(ApprovalConstants.RESULT_APPROVED, recordCaptor.getValue().getResult());
        assertEquals(ApprovalConstants.STATUS_APPROVING, orderCaptor.getValue().getStatus());
        assertEquals(2, orderCaptor.getValue().getCurrentStep());
    }

    @Test
    @DisplayName("匹配角色节点时应允许审批通过")
    void shouldAllowApproveWhenCurrentUserHasRequiredRole() {
        setCurrentUserWithRoles(3001L, "role-approver", List.of("3001"));
        WmsApprovalOrder order = buildApprovingOrder(8005L, 9005L, 1, 1001L, 1, 2);
        WmsApprovalConfig config = buildConfig(504L, order.getBizType());
        WmsApprovalNode currentNode = buildRoleNode(config.getId(), 1, 3001L);
        when(wmsApprovalOrderMapper.selectById(8005L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        approvalService.approve(8005L, buildActionDto("角色通过"));

        verify(wmsApprovalRecordMapper).insert(any(WmsApprovalRecord.class));
        verify(wmsApprovalOrderMapper).updateById(any(WmsApprovalOrder.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("角色不匹配时审批应拒绝")
    void shouldRejectApproveWhenCurrentUserMissingRequiredRole() {
        setCurrentUserWithRoles(3002L, "other-role-user", List.of("3002"));
        WmsApprovalOrder order = buildApprovingOrder(8006L, 9006L, 1, 1001L, 1, 2);
        WmsApprovalConfig config = buildConfig(505L, order.getBizType());
        WmsApprovalNode currentNode = buildRoleNode(config.getId(), 1, 3001L);
        when(wmsApprovalOrderMapper.selectById(8006L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        BizException exception = assertThrows(BizException.class,
                () -> approvalService.approve(8006L, buildActionDto("角色越权")));

        assertEquals("当前用户不具备当前步骤审批角色", exception.getMessage());
        verify(wmsApprovalRecordMapper, never()).insert(any());
        verify(wmsApprovalOrderMapper, never()).updateById(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("库房管理员节点未接入时应按 fail-closed 拒绝审批")
    void shouldFailClosedWhenWarehouseAdminApproverTypeNotSupported() {
        setCurrentUser(4001L, "warehouse-admin");
        WmsApprovalOrder order = buildApprovingOrder(8007L, 9007L, 2, 1001L, 1, 2);
        WmsApprovalConfig config = buildConfig(506L, order.getBizType());
        WmsApprovalNode currentNode = buildWarehouseAdminNode(config.getId(), 1);
        when(wmsApprovalOrderMapper.selectById(8007L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        BizException exception = assertThrows(BizException.class,
                () -> approvalService.approve(8007L, buildActionDto("管理员审批")));

        assertEquals("库房管理员审批节点暂未支持，已拒绝当前审批请求", exception.getMessage());
        verify(wmsApprovalRecordMapper, never()).insert(any());
        verify(wmsApprovalOrderMapper, never()).updateById(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("最后一步审批通过时应发布审批通过事件")
    void shouldPublishApprovalPassedEventWhenFinalStepApproved() {
        setCurrentUser(2001L, "approver-final");
        WmsApprovalOrder order = buildApprovingOrder(8008L, 9008L, 5, 1001L, 2, 2);
        WmsApprovalConfig config = buildConfig(507L, order.getBizType());
        WmsApprovalNode currentNode = buildUserNode(config.getId(), 2, 2001L);
        when(wmsApprovalOrderMapper.selectById(8008L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        approvalService.approve(8008L, buildActionDto("最终通过"));

        ArgumentCaptor<ApprovalResultEvent> eventCaptor = ArgumentCaptor.forClass(ApprovalResultEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(9008L, eventCaptor.getValue().getBizId());
        assertEquals(5, eventCaptor.getValue().getBizType());
        assertEquals(true, eventCaptor.getValue().isApproved());
    }

    @Test
    @DisplayName("审批详情应补齐审批单号业务单号当前节点和申请人姓名")
    void shouldPopulateApprovalViewFieldsWhenGetApprovalById() {
        WmsApprovalOrder order = buildApprovingOrder(8100L, 9100L, 1, 1001L, 2, 3);
        order.setCreateBy("creator");
        order.setCreateTime(LocalDateTime.of(2026, 5, 27, 12, 30));
        WmsApprovalRecord record = new WmsApprovalRecord();
        record.setId(8200L);
        record.setApprovalId(order.getId());
        record.setStepOrder(1);
        record.setApproverId(2001L);
        record.setApproverName("审批人A");
        record.setResult(ApprovalConstants.RESULT_APPROVED);
        record.setOpinion("同意");
        record.setApproveTime(LocalDateTime.of(2026, 5, 27, 12, 35));
        WmsApprovalConfig config = buildConfig(508L, order.getBizType());
        WmsApprovalNode firstNode = buildUserNode(config.getId(), 1, 2001L);
        firstNode.setNodeName("一级审批");
        WmsApprovalNode currentNode = buildRoleNode(config.getId(), 2, 3001L);
        currentNode.setNodeName("二级审批");
        SysUser applicant = buildUser(1001L, "zhangsan", "张三");
        WmsInboundOrder inboundOrder = new WmsInboundOrder();
        inboundOrder.setId(order.getBizId());
        inboundOrder.setOrderNo("RK202605270001");
        when(wmsApprovalOrderMapper.selectById(order.getId())).thenReturn(order);
        when(wmsApprovalRecordMapper.selectList(any())).thenReturn(List.of(record));
        when(wmsApprovalConfigMapper.selectList(any())).thenReturn(List.of(config));
        when(wmsApprovalNodeMapper.selectList(any())).thenReturn(List.of(firstNode, currentNode));
        when(sysUserMapper.selectBatchIds(anyCollection())).thenReturn(List.of(applicant));
        when(wmsInboundOrderMapper.selectBatchIds(anyCollection())).thenReturn(List.of(inboundOrder));

        ApprovalOrderVo result = approvalService.getApprovalById(order.getId());

        assertEquals("SP8100", result.getApprovalNo());
        assertEquals("RK202605270001", result.getBizNo());
        assertEquals("二级审批", result.getCurrentNodeName());
        assertEquals("张三", result.getApplicantName());
        assertNotNull(result.getRecords());
        assertEquals(1, result.getRecords().size());
        assertEquals("一级审批", result.getRecords().get(0).getNodeName());
    }

    @Test
    @DisplayName("审批分页列表应批量补齐业务单号和申请人姓名")
    void shouldPopulateApprovalPageViewFieldsInBatch() {
        WmsApprovalOrder inboundApproval = buildApprovingOrder(8101L, 9101L, 1, 1001L, 1, 2);
        inboundApproval.setCreateBy("zhangsan");
        WmsApprovalOrder returnApproval = buildApprovingOrder(8102L, 9102L, 5, 1002L, 1, 1);
        returnApproval.setCreateBy("lisi");
        Page<WmsApprovalOrder> page = new Page<>(1, 20);
        page.setRecords(List.of(inboundApproval, returnApproval));
        page.setTotal(2);
        WmsApprovalConfig inboundConfig = buildConfig(601L, 1);
        WmsApprovalConfig returnConfig = buildConfig(602L, 5);
        WmsApprovalNode inboundNode = buildUserNode(601L, 1, 2001L);
        inboundNode.setNodeName("入库审批");
        WmsApprovalNode returnNode = buildUserNode(602L, 1, 2002L);
        returnNode.setNodeName("归还审批");
        SysUser zhangsan = buildUser(1001L, "zhangsan", "张三");
        SysUser lisi = buildUser(1002L, "lisi", "李四");
        WmsInboundOrder inboundOrder = new WmsInboundOrder();
        inboundOrder.setId(9101L);
        inboundOrder.setOrderNo("RK202605270002");
        WmsReturnOrder returnOrder = new WmsReturnOrder();
        returnOrder.setId(9102L);
        returnOrder.setOrderNo("GH202605270001");
        when(wmsApprovalOrderMapper.selectPage(any(Page.class), any())).thenReturn(page);
        when(wmsApprovalConfigMapper.selectList(any())).thenReturn(List.of(inboundConfig, returnConfig));
        when(wmsApprovalNodeMapper.selectList(any())).thenReturn(List.of(inboundNode, returnNode));
        when(sysUserMapper.selectBatchIds(anyCollection())).thenReturn(List.of(zhangsan, lisi));
        when(wmsInboundOrderMapper.selectBatchIds(anyCollection())).thenReturn(List.of(inboundOrder));
        when(wmsReturnOrderMapper.selectBatchIds(anyCollection())).thenReturn(List.of(returnOrder));

        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setSize(20);
        List<ApprovalOrderVo> records = approvalService.pageApprovals(pageParam, null, null).getRecords();

        assertEquals(2, records.size());
        assertEquals("SP8101", records.get(0).getApprovalNo());
        assertEquals("RK202605270002", records.get(0).getBizNo());
        assertEquals("张三", records.get(0).getApplicantName());
        assertEquals("入库审批", records.get(0).getCurrentNodeName());
        assertEquals("GH202605270001", records.get(1).getBizNo());
        assertEquals("李四", records.get(1).getApplicantName());
        assertEquals("归还审批", records.get(1).getCurrentNodeName());
    }

    @Test
    @DisplayName("非当前步骤审批人审批时应拒绝")
    void shouldRejectApproveWhenCurrentUserIsNotStepApprover() {
        setCurrentUser(9999L, "outsider");
        WmsApprovalOrder order = buildApprovingOrder(8002L, 9002L, 3, 1001L, 1, 2);
        WmsApprovalConfig config = buildConfig(502L, order.getBizType());
        WmsApprovalNode currentNode = buildUserNode(config.getId(), 1, 2001L);
        when(wmsApprovalOrderMapper.selectById(8002L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        BizException exception = assertThrows(BizException.class,
                () -> approvalService.approve(8002L, buildActionDto("越权审批")));

        assertEquals("当前用户不是当前步骤审批人", exception.getMessage());
        verify(wmsApprovalRecordMapper, never()).insert(any());
        verify(wmsApprovalOrderMapper, never()).updateById(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("非当前步骤审批人驳回时应拒绝")
    void shouldRejectRejectWhenCurrentUserIsNotStepApprover() {
        setCurrentUser(9999L, "outsider");
        WmsApprovalOrder order = buildApprovingOrder(8003L, 9003L, 4, 1001L, 1, 2);
        WmsApprovalConfig config = buildConfig(503L, order.getBizType());
        WmsApprovalNode currentNode = buildUserNode(config.getId(), 1, 2001L);
        when(wmsApprovalOrderMapper.selectById(8003L)).thenReturn(order);
        when(wmsApprovalConfigMapper.selectOne(any())).thenReturn(config);
        when(wmsApprovalNodeMapper.selectOne(any())).thenReturn(currentNode);

        BizException exception = assertThrows(BizException.class,
                () -> approvalService.reject(8003L, buildActionDto("越权驳回")));

        assertEquals("当前用户不是当前步骤审批人", exception.getMessage());
        verify(wmsApprovalRecordMapper, never()).insert(any());
        verify(wmsApprovalOrderMapper, never()).updateById(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("非申请人撤回审批时应拒绝")
    void shouldRejectRevokeWhenCurrentUserIsNotApplicant() {
        setCurrentUser(9999L, "outsider");
        WmsApprovalOrder order = buildApprovingOrder(8004L, 9004L, 2, 1001L, 1, 2);
        when(wmsApprovalOrderMapper.selectById(8004L)).thenReturn(order);

        BizException exception = assertThrows(BizException.class, () -> approvalService.revoke(8004L));

        assertEquals("仅申请人可以撤回审批", exception.getMessage());
        verify(wmsApprovalOrderMapper, never()).updateById(any());
        verifyNoInteractions(eventPublisher);
    }

    private void setCurrentUser(Long userId, String username) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null);
        authentication.setDetails(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setCurrentUserWithRoles(Long userId, String username, List<String> roleIds) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        roleIds.stream().map(roleId -> new SimpleGrantedAuthority("ROLE_" + roleId)).toList()
                );
        authentication.setDetails(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private WmsApprovalConfig buildConfig(Long configId, Integer bizType) {
        WmsApprovalConfig config = new WmsApprovalConfig();
        config.setId(configId);
        config.setBizType(bizType);
        config.setEnabled(BizConstants.STATUS_ENABLED);
        config.setAutoApprove(BizConstants.STATUS_DISABLED);
        config.setDelFlag(DelFlagConstants.NORMAL);
        return config;
    }

    private WmsApprovalNode buildUserNode(Long configId, int stepOrder, Long approverId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setConfigId(configId);
        node.setStepOrder(stepOrder);
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_USER);
        node.setApproverId(approverId);
        return node;
    }

    private SysUser buildUser(Long id, String username, String realName) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setRealName(realName);
        return user;
    }

    private WmsApprovalNode buildRoleNode(Long configId, int stepOrder, Long roleId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setConfigId(configId);
        node.setStepOrder(stepOrder);
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_ROLE);
        node.setApproverId(roleId);
        return node;
    }

    private WmsApprovalNode buildWarehouseAdminNode(Long configId, int stepOrder) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setConfigId(configId);
        node.setStepOrder(stepOrder);
        node.setApproverType(ApprovalConstants.APPROVER_TYPE_WAREHOUSE_ADMIN);
        return node;
    }

    private WmsApprovalOrder buildApprovingOrder(Long id,
                                                 Long bizId,
                                                 Integer bizType,
                                                 Long applicantId,
                                                 Integer currentStep,
                                                 Integer totalSteps) {
        WmsApprovalOrder order = new WmsApprovalOrder();
        order.setId(id);
        order.setBizId(bizId);
        order.setBizType(bizType);
        order.setStatus(ApprovalConstants.STATUS_APPROVING);
        order.setApplicantId(applicantId);
        order.setCurrentStep(currentStep);
        order.setTotalSteps(totalSteps);
        order.setDelFlag(DelFlagConstants.NORMAL);
        return order;
    }

    private ApprovalActionDto buildActionDto(String opinion) {
        ApprovalActionDto dto = new ApprovalActionDto();
        dto.setOpinion(opinion);
        return dto;
    }
}
