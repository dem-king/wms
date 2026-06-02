package com.wms.approval.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.approval.domain.constant.ApprovalConstants;
import com.wms.approval.domain.entity.WmsApprovalConfig;
import com.wms.approval.domain.entity.WmsApprovalNode;
import com.wms.approval.domain.entity.WmsApprovalOrder;
import com.wms.approval.domain.entity.WmsApprovalRecord;
import com.wms.approval.mapper.WmsApprovalConfigMapper;
import com.wms.approval.mapper.WmsApprovalNodeMapper;
import com.wms.approval.mapper.WmsApprovalOrderMapper;
import com.wms.approval.mapper.WmsApprovalRecordMapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.event.ApprovalResultEvent;
import com.wms.system.domain.constant.SysMessageConstants;
import com.wms.system.domain.dto.SysMessageCreateDto;
import com.wms.system.domain.entity.SysUserRole;
import com.wms.system.mapper.SysUserRoleMapper;
import com.wms.system.service.SysMessageService;
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
import java.util.Objects;
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
    private final WmsApprovalRecordMapper wmsApprovalRecordMapper;
    private final WmsApprovalNodeMapper wmsApprovalNodeMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysMessageService sysMessageService;
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
                insertTimeoutRecord(order, ApprovalConstants.RESULT_REJECTED,
                        ApprovalConstants.TIMEOUT_CANCEL_OPINION, elapsedHours, timeoutHours);
                eventPublisher.publishEvent(new ApprovalResultEvent(order.getBizId(), order.getBizType(), false));
                cancelledCount++;
                log.info("审批超时自动取消: approvalId={}, bizId={}, bizType={}, elapsedHours={}",
                        order.getId(), order.getBizId(), order.getBizType(), elapsedHours);
            } else {
                // 自动提醒：写入系统记录，便于在审批详情中追溯
                insertTimeoutRecord(order, null,
                        ApprovalConstants.TIMEOUT_REMIND_OPINION, elapsedHours, timeoutHours);
                remindedCount++;
                log.warn("审批超时提醒: approvalId={}, bizId={}, bizType={}, elapsedHours={}, timeoutHours={}",
                        order.getId(), order.getBizId(), order.getBizType(), elapsedHours, timeoutHours);
            }
            sendTimeoutMessages(order, config, elapsedHours, timeoutHours);
        }

        log.info("审批超时检查完成，提醒{}条，取消{}条", remindedCount, cancelledCount);
    }

    /**
     * 写入审批超时系统记录，保证自动提醒和自动取消都能在审批详情中追溯。
     *
     * @param order        审批单
     * @param result       审批结果，提醒记录为空
     * @param opinion      审批意见前缀
     * @param elapsedHours 已等待小时数
     * @param timeoutHours 超时阈值小时数
     */
    private void insertTimeoutRecord(WmsApprovalOrder order, Integer result, String opinion,
                                     long elapsedHours, int timeoutHours) {
        WmsApprovalRecord record = new WmsApprovalRecord();
        record.setApprovalId(order.getId());
        record.setStepOrder(order.getCurrentStep());
        record.setApproverName(ApprovalConstants.SYSTEM_APPROVER_NAME);
        record.setResult(result);
        record.setOpinion(opinion + "，已等待" + elapsedHours + "小时，超时阈值" + timeoutHours + "小时");
        record.setApproveTime(LocalDateTime.now());
        wmsApprovalRecordMapper.insert(record);
    }

    /**
     * 给当前审批节点的审批人发送审批超时站内信。
     *
     * @param order        审批单
     * @param config       审批配置
     * @param elapsedHours 已等待小时数
     * @param timeoutHours 超时阈值小时数
     */
    private void sendTimeoutMessages(WmsApprovalOrder order, WmsApprovalConfig config,
                                     long elapsedHours, int timeoutHours) {
        List<WmsApprovalNode> nodes = wmsApprovalNodeMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WmsApprovalNode>()
                        .eq(WmsApprovalNode::getConfigId, config.getId())
                        .eq(WmsApprovalNode::getStepOrder, order.getCurrentStep()));
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.stream()
                .flatMap(node -> resolveReceiverIds(node).stream())
                .distinct()
                .forEach(receiverId -> sysMessageService.createIfAbsent(
                        buildTimeoutMessage(order, receiverId, elapsedHours, timeoutHours)));
    }

    /**
     * 解析审批节点对应的站内信接收人。
     * 角色节点按角色下用户展开；库房管理员节点当前无法稳定定位到用户，暂不发送站内信。
     *
     * @param node 审批节点
     * @return 接收人ID集合
     */
    private List<Long> resolveReceiverIds(WmsApprovalNode node) {
        if (Objects.equals(node.getApproverType(), ApprovalConstants.APPROVER_TYPE_USER)) {
            return node.getApproverId() == null ? List.of() : List.of(node.getApproverId());
        }
        if (!Objects.equals(node.getApproverType(), ApprovalConstants.APPROVER_TYPE_ROLE)
                || node.getApproverId() == null) {
            return List.of();
        }

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, node.getApproverId()));
        if (userRoles == null || userRoles.isEmpty()) {
            return List.of();
        }
        return userRoles.stream()
                .map(SysUserRole::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 构建审批超时站内信创建参数。
     *
     * @param order        审批单
     * @param receiverId   接收人ID
     * @param elapsedHours 已等待小时数
     * @param timeoutHours 超时阈值小时数
     * @return 站内信创建参数
     */
    private SysMessageCreateDto buildTimeoutMessage(WmsApprovalOrder order, Long receiverId,
                                                    long elapsedHours, int timeoutHours) {
        SysMessageCreateDto dto = new SysMessageCreateDto();
        dto.setReceiverId(receiverId);
        dto.setTitle(ApprovalConstants.TIMEOUT_MESSAGE_TITLE);
        dto.setContent("审批单SP" + order.getId() + "已等待" + elapsedHours
                + "小时，超过" + timeoutHours + "小时超时阈值，请及时处理");
        dto.setMessageType(SysMessageConstants.TYPE_APPROVAL_TIMEOUT);
        dto.setMessageLevel(SysMessageConstants.LEVEL_WARNING);
        dto.setBusinessKey(ApprovalConstants.TIMEOUT_MESSAGE_BUSINESS_KEY_PREFIX + order.getId());
        dto.setTargetUrl(String.format(ApprovalConstants.TIMEOUT_MESSAGE_TARGET_URL_TEMPLATE, order.getId()));
        return dto;
    }
}
