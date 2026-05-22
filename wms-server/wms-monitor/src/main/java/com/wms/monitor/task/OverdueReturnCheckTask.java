package com.wms.monitor.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.business.domain.entity.WmsOutboundDetail;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.monitor.domain.constant.MonitorConstants;
import com.wms.monitor.domain.entity.MonitorOverdueReturn;
import com.wms.monitor.mapper.MonitorOverdueReturnMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 逾期归还检查定时任务
 * 每天9:00执行，扫描已完成的领用出库单中预计归还日期已过的记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueReturnCheckTask {

    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsOutboundDetailMapper wmsOutboundDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final MonitorOverdueReturnMapper monitorOverdueReturnMapper;

    /**
     * 逾期归还检查定时任务
     * 查询领用出库单(orderType=1, status=COMPLETED, expectedReturnDate < now)
     * 关联出库明细获取物品信息，计算逾期天数和提醒级别
     * 去重：已有PENDING记录则更新overdueDays和alertLevel
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void check() {
        log.info("逾期归还检查开始");
        LocalDate today = LocalDate.now();

        // 查询领用出库且已完成、预计归还日期已过的出库单
        LambdaQueryWrapper<WmsOutboundOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(WmsOutboundOrder::getOrderType, MonitorConstants.OUTBOUND_TYPE_BORROW)
                .eq(WmsOutboundOrder::getStatus, MonitorConstants.OUTBOUND_STATUS_COMPLETED)
                .lt(WmsOutboundOrder::getExpectedReturnDate, today);
        List<WmsOutboundOrder> overdueOrders = wmsOutboundOrderMapper.selectList(orderWrapper);

        if (overdueOrders.isEmpty()) {
            log.info("逾期归还检查完成，无逾期归还记录");
            return;
        }

        // 批量查出库明细
        Set<Long> orderIds = overdueOrders.stream().map(WmsOutboundOrder::getId).collect(Collectors.toSet());
        List<WmsOutboundDetail> allDetails = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>().in(WmsOutboundDetail::getOrderId, orderIds));
        Map<Long, List<WmsOutboundDetail>> detailMap = allDetails.stream()
                .collect(Collectors.groupingBy(WmsOutboundDetail::getOrderId));

        // 批量查物品
        Set<Long> itemIds = allDetails.stream().map(WmsOutboundDetail::getItemId).collect(Collectors.toSet());
        Map<Long, WmsItem> itemMap = itemIds.isEmpty() ? Collections.emptyMap() :
                wmsItemMapper.selectBatchIds(itemIds).stream().collect(Collectors.toMap(WmsItem::getId, i -> i));

        // 查询所有未处理的逾期归还记录
        LambdaQueryWrapper<MonitorOverdueReturn> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(MonitorOverdueReturn::getStatus, MonitorConstants.ALERT_STATUS_PENDING);
        List<MonitorOverdueReturn> pendingRecords = monitorOverdueReturnMapper.selectList(pendingWrapper);
        // 按出库单ID+物品ID构建已存在记录的映射
        Map<String, MonitorOverdueReturn> existingMap = pendingRecords.stream()
                .collect(Collectors.toMap(
                        r -> r.getOrderId() + "_" + r.getItemId(),
                        r -> r,
                        (a, b) -> a
                ));

        // 收集当前仍逾期的key
        Set<String> currentOverdueKeys = new HashSet<>();

        for (WmsOutboundOrder order : overdueOrders) {
            List<WmsOutboundDetail> details = detailMap.getOrDefault(order.getId(), Collections.emptyList());
            // 计算逾期天数
            LocalDate expectedDate = order.getExpectedReturnDate() != null
                    ? order.getExpectedReturnDate().toLocalDate() : today;
            long overdueDays = ChronoUnit.DAYS.between(expectedDate, today);
            if (overdueDays <= 0) {
                continue;
            }

            String alertLevel = calculateAlertLevel(overdueDays);

            for (WmsOutboundDetail detail : details) {
                String key = order.getId() + "_" + detail.getItemId();
                currentOverdueKeys.add(key);

                WmsItem item = itemMap.get(detail.getItemId());
                String itemName = item != null ? item.getItemName() : "";
                String itemCode = item != null ? item.getItemCode() : "";

                MonitorOverdueReturn existing = existingMap.get(key);
                if (existing != null) {
                    // 已有PENDING记录，更新逾期天数和提醒级别
                    existing.setOverdueDays((int) overdueDays);
                    existing.setAlertLevel(alertLevel);
                    monitorOverdueReturnMapper.updateById(existing);
                } else {
                    // 新增逾期归还记录
                    MonitorOverdueReturn record = new MonitorOverdueReturn();
                    record.setOrderId(order.getId());
                    record.setOrderNo(order.getOrderNo());
                    record.setItemId(detail.getItemId());
                    record.setItemName(itemName);
                    record.setItemCode(itemCode);
                    record.setBorrowQuantity(detail.getQuantity());
                    record.setBorrowerName(order.getReceiver());
                    record.setBorrowTime(order.getCreateTime());
                    record.setExpectedReturnDate(expectedDate);
                    record.setOverdueDays((int) overdueDays);
                    record.setAlertLevel(alertLevel);
                    record.setStatus(MonitorConstants.ALERT_STATUS_PENDING);
                    monitorOverdueReturnMapper.insert(record);
                }
            }
        }

        // 恢复正常：PENDING记录但当前不再逾期的标记为RESOLVED
        for (MonitorOverdueReturn record : pendingRecords) {
            String key = record.getOrderId() + "_" + record.getItemId();
            if (!currentOverdueKeys.contains(key)) {
                record.setStatus(MonitorConstants.ALERT_STATUS_RESOLVED);
                monitorOverdueReturnMapper.updateById(record);
            }
        }

        log.info("逾期归还检查完成");
    }

    /**
     * 根据逾期天数计算提醒级别
     *
     * @param overdueDays 逾期天数
     * @return 提醒级别
     */
    private String calculateAlertLevel(long overdueDays) {
        if (overdueDays > MonitorConstants.OVERDUE_URGENT_THRESHOLD) {
            return MonitorConstants.ALERT_LEVEL_URGENT;
        } else if (overdueDays > MonitorConstants.OVERDUE_IMPORTANT_THRESHOLD) {
            return MonitorConstants.ALERT_LEVEL_IMPORTANT;
        } else {
            return MonitorConstants.ALERT_LEVEL_NORMAL;
        }
    }
}
