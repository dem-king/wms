package com.wms.approval.strategy;

import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.common.constant.BizConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 审批策略工厂
 * 根据审批配置(autoApprove和节点数)选择对应的审批策略
 */
@Component
@RequiredArgsConstructor
public class ApprovalStrategyFactory {

    private final FreeApprovalStrategy freeApprovalStrategy;
    private final SingleApprovalStrategy singleApprovalStrategy;
    private final MultiApprovalStrategy multiApprovalStrategy;

    /**
     * 根据审批配置获取对应的审批策略
     * 免审配置返回FreeApprovalStrategy，单节点返回SingleApprovalStrategy，多节点返回MultiApprovalStrategy
     *
     * @param config 审批配置
     * @param nodeCount 审批节点数量
     * @return 对应的审批策略实例
     */
    public ApprovalStrategy getStrategy(WmsApprovalConfig config, int nodeCount) {
        // 免审配置
        if (config.getAutoApprove() != null && config.getAutoApprove() == BizConstants.STATUS_ENABLED) {
            return freeApprovalStrategy;
        }
        // 按审批节点数量区分单级/多级
        if (nodeCount == ApprovalConstants.FIRST_STEP_ORDER) {
            return singleApprovalStrategy;
        }
        return multiApprovalStrategy;
    }
}
