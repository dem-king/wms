package com.wms.approval.strategy;

import com.wms.approval.converter.ApprovalOrderConverter;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * 审批策略执行单元测试
 * 验证单级、多级审批在创建审批单时的初始步骤设置
 */
@DisplayName("ApprovalStrategy 执行测试")
@ExtendWith(MockitoExtension.class)
class ApprovalStrategyExecutionTest {

    @Mock
    private WmsApprovalOrderMapper wmsApprovalOrderMapper;

    private final ApprovalOrderConverter approvalOrderConverter = new ApprovalOrderConverter();

    @Test
    @DisplayName("单级审批创建后应从第1步开始")
    void shouldStartSingleApprovalFromFirstStep() {
        SingleApprovalStrategy strategy = new SingleApprovalStrategy(wmsApprovalOrderMapper, approvalOrderConverter);
        ApprovalContext context = ApprovalContext.builder()
                .bizId(9001L)
                .bizType(1)
                .applicantId(1001L)
                .nodes(List.of(buildNode(1, 2001L)))
                .build();

        strategy.execute(context);

        ArgumentCaptor<WmsApprovalOrder> orderCaptor = ArgumentCaptor.forClass(WmsApprovalOrder.class);
        verify(wmsApprovalOrderMapper).insert(orderCaptor.capture());
        assertEquals(ApprovalConstants.STATUS_APPROVING, orderCaptor.getValue().getStatus());
        assertEquals(1, orderCaptor.getValue().getCurrentStep());
        assertEquals(1, orderCaptor.getValue().getTotalSteps());
    }

    @Test
    @DisplayName("多级审批创建后应从第1步开始")
    void shouldStartMultiApprovalFromFirstStep() {
        MultiApprovalStrategy strategy = new MultiApprovalStrategy(wmsApprovalOrderMapper, approvalOrderConverter);
        ApprovalContext context = ApprovalContext.builder()
                .bizId(9002L)
                .bizType(2)
                .applicantId(1002L)
                .nodes(List.of(buildNode(1, 2001L), buildNode(2, 2002L)))
                .build();

        strategy.execute(context);

        ArgumentCaptor<WmsApprovalOrder> orderCaptor = ArgumentCaptor.forClass(WmsApprovalOrder.class);
        verify(wmsApprovalOrderMapper).insert(orderCaptor.capture());
        assertEquals(ApprovalConstants.STATUS_APPROVING, orderCaptor.getValue().getStatus());
        assertEquals(1, orderCaptor.getValue().getCurrentStep());
        assertEquals(2, orderCaptor.getValue().getTotalSteps());
    }

    private WmsApprovalNode buildNode(int stepOrder, Long approverId) {
        WmsApprovalNode node = new WmsApprovalNode();
        node.setStepOrder(stepOrder);
        node.setApproverId(approverId);
        return node;
    }
}
