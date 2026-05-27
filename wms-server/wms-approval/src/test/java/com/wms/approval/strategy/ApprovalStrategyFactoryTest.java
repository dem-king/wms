package com.wms.approval.strategy;

import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.common.constant.BizConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

/**
 * 审批策略工厂单元测试
 * 验证免审、单级、多级审批的策略选择逻辑
 */
@DisplayName("ApprovalStrategyFactory 测试")
class ApprovalStrategyFactoryTest {

    @Test
    @DisplayName("免审配置启用时应返回免审策略")
    void shouldReturnFreeStrategyWhenAutoApproveEnabled() {
        FreeApprovalStrategy freeApprovalStrategy = mock(FreeApprovalStrategy.class);
        SingleApprovalStrategy singleApprovalStrategy = mock(SingleApprovalStrategy.class);
        MultiApprovalStrategy multiApprovalStrategy = mock(MultiApprovalStrategy.class);
        ApprovalStrategyFactory factory = new ApprovalStrategyFactory(
                freeApprovalStrategy,
                singleApprovalStrategy,
                multiApprovalStrategy
        );
        WmsApprovalConfig config = new WmsApprovalConfig();
        config.setAutoApprove(BizConstants.STATUS_ENABLED);

        ApprovalStrategy strategy = factory.getStrategy(config, 2);

        assertSame(freeApprovalStrategy, strategy);
    }

    @Test
    @DisplayName("单节点审批时应返回单级审批策略")
    void shouldReturnSingleStrategyWhenOnlyOneNodeExists() {
        FreeApprovalStrategy freeApprovalStrategy = mock(FreeApprovalStrategy.class);
        SingleApprovalStrategy singleApprovalStrategy = mock(SingleApprovalStrategy.class);
        MultiApprovalStrategy multiApprovalStrategy = mock(MultiApprovalStrategy.class);
        ApprovalStrategyFactory factory = new ApprovalStrategyFactory(
                freeApprovalStrategy,
                singleApprovalStrategy,
                multiApprovalStrategy
        );
        WmsApprovalConfig config = new WmsApprovalConfig();
        config.setAutoApprove(BizConstants.STATUS_DISABLED);

        ApprovalStrategy strategy = factory.getStrategy(config, 1);

        assertSame(singleApprovalStrategy, strategy);
    }
}
