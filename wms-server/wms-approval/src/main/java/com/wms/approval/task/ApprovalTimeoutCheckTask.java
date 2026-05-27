package com.wms.approval.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.event.ApprovalResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 审批超时检查定时任务
 * 每小时执行，扫描审批中状态且超过配置超时阈值的审批单
 * 根据配置的超时处理方式执行自动提醒或自动取消
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalTimeoutCheckTask {

    private final WmsApprovalOrderMapper wmsApprovalOrderMapper;
    private final WmsApprovalConfigMapper wmsApprovalConfigMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 审批超时检查定时任务
     * 查询审批中状态的审批单，按业务类型匹配审批配置的超时阈值
     * 超时后根据配置执行自动提醒(仅记录日志)或自动取消(发布驳回事件)
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void check() {
        log.info("审批超时检查开始");
        LocalDateTime now = LocalDateTime.now();

        // 查询所有审批中状态的审批单
        List<WmsApprovalOrder> approvingOrders = wmsApprovalOrderMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalOrder>()
                        .eq(WmsApprovalOrder::getStatus, ApprovalConstants.STATUS_APPROVING));

        if (approvingOrders.isEmpty()) {
            log.info("审批超时检查完成，无审批中的审批单");
            return;
        }

        // 批量查询审批配置(按业务类型)
        Set<Integer> bizTypes = approvingOrders.stream()
                .map(WmsApprovalOrder::getBizType)
                .collect(Collectors.toSet());
        List<WmsApprovalConfig> configs = wmsApprovalConfigMapper.selectList(
                new LambdaQueryWrapper<WmsApprovalConfig>()
                        .in(WmsApprovalConfig::getBizType, bizTypes)
                        .eq(WmsApprovalConfig::getEnabled, BizConstants.STATUS_ENABLED));
        Map<Integer, WmsApprovalConfig> configMap = configs.stream()
                .collect(Collectors.toMap(WmsApprovalConfig::getBizType, Function.identity(), (a, b) -> a));

        int remindedCount = 0;
        int cancelledCount = 0;

        for (WmsApprovalOrder order : approvingOrders) {
            WmsApprovalConfig config = configMap.get(order.getBizType());
            if (config == null) {
                continue;
            }

            // 获取超时阈值，未配置则使用默认值
            int timeoutHours = config.getTimeoutHours() != null
                    ? config.getTimeoutHours() : ApprovalConstants.DEFAULT_TIMEOUT_HOURS;

            // 计算审批单等待时长(小时)
            long elapsedHours = ChronoUnit.HOURS.between(order.getCreateTime(), now);
            if (elapsedHours < timeoutHours) {
                continue;
            }

            // 获取超时处理方式，未配置则默认为自动提醒
            int timeoutAction = config.getTimeoutAction() != null
                    ? config.getTimeoutAction() : ApprovalConstants.TIMEOUT_ACTION_REMIND;

            if (timeoutAction == ApprovalConstants.TIMEOUT_ACTION_CANCEL) {
                // 自动取消：标记审批单为已驳回并发布审批结果事件
                order.setStatus(ApprovalConstants.STATUS_REJECTED);
                wmsApprovalOrderMapper.updateById(order);
                eventPublisher.publishEvent(new ApprovalResultEvent(order.getBizId(), order.getBizType(), false));
                cancelledCount++;
                log.info("审批超时自动取消: approvalId={}, bizId={}, bizType={}, elapsedHours={}",
                        order.getId(), order.getBizId(), order.getBizType(), elapsedHours);
            } else {
                // 自动提醒：仅记录日志，实际场景可接入消息通知服务
                remindedCount++;
                log.warn("审批超时提醒: approvalId={}, bizId={}, bizType={}, elapsedHours={}, timeoutHours={}",
                        order.getId(), order.getBizId(), order.getBizType(), elapsedHours, timeoutHours);
            }
        }

        log.info("审批超时检查完成，提醒{}条，取消{}条", remindedCount, cancelledCount);
    }
}
