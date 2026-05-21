package com.wms.approval.listener;

import com.wms.approval.service.ApprovalService;
import com.wms.common.event.ApprovalRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 审批请求事件监听器
 * 监听业务模块发布的ApprovalRequestEvent，调用审批服务发起审批流程
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalRequestEventListener {

    private final ApprovalService approvalService;

    /**
     * 处理审批请求事件
     * 调用审批服务发起审批流程
     *
     * @param event 审批请求事件
     */
    @EventListener
    public void handleApprovalRequest(ApprovalRequestEvent event) {
        log.info("收到审批请求事件: bizId={}, bizType={}", event.getBizId(), event.getBizType());
        approvalService.startApproval(event.getBizId(), event.getBizType());
    }
}
