package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.entity.MonitorStockAlert;
import com.wms.report.domain.entity.ReportAlertDaily;
import com.wms.report.mapper.MonitorStockAlertMapper;
import com.wms.report.mapper.ReportAlertDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预警日聚合定时任务
 * 每日00:07执行，聚合前一天预警记录到report_alert_daily表
 * 注意：依赖P4-38的monitor_stock_alert表，当前使用基础版本的MonitorStockAlert实体
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertDailyAggregationTask {

    private final MonitorStockAlertMapper monitorStockAlertMapper;
    private final ReportAlertDailyMapper reportAlertDailyMapper;

    /**
     * 预警日聚合定时任务
     * 查询前一天产生的预警记录，按库房+预警类型聚合写入report_alert_daily
     */
    @Scheduled(cron = "0 7 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始预警日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天产生的预警记录
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<MonitorStockAlert> alertWrapper = new LambdaQueryWrapper<>();
            alertWrapper.ge(MonitorStockAlert::getCreateTime, dayStart)
                    .lt(MonitorStockAlert::getCreateTime, dayEnd);
            List<MonitorStockAlert> alerts = monitorStockAlertMapper.selectList(alertWrapper);

            if (alerts.isEmpty()) {
                log.info("预警日聚合完成，无预警记录，统计日期: {}", statDate);
                return;
            }

            // 按库房+预警类型聚合
            Map<String, ReportAlertDaily> aggMap = new LinkedHashMap<>();
            for (MonitorStockAlert alert : alerts) {
                String key = alert.getWarehouseId() + "_" + alert.getAlertType();

                ReportAlertDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportAlertDaily r = new ReportAlertDaily();
                    r.setStatDate(statDate);
                    r.setWarehouseId(alert.getWarehouseId());
                    r.setAlertType(alert.getAlertType());
                    r.setTriggerCount(0);
                    r.setAffectedItemCount(0);
                    r.setResolvedCount(0);
                    return r;
                });
                // 每条预警记录记为一次触发
                agg.setTriggerCount(agg.getTriggerCount() + 1);
                // 已处理的预警计数：优先判断status字段，兼容isResolved字段
                boolean resolved = "RESOLVED".equals(alert.getStatus())
                        || (alert.getIsResolved() != null && alert.getIsResolved() == ReportConstants.ALERT_RESOLVED_YES);
                if (resolved) {
                    agg.setResolvedCount(agg.getResolvedCount() + 1);
                }
            }

            // 计算涉及物品种类数(按库房+预警类型去重物品ID)
            Map<String, Set<Long>> typeItemMap = new HashMap<>();
            for (MonitorStockAlert alert : alerts) {
                String key = alert.getWarehouseId() + "_" + alert.getAlertType();
                typeItemMap.computeIfAbsent(key, k -> new HashSet<>()).add(alert.getItemId());
            }
            for (Map.Entry<String, Set<Long>> entry : typeItemMap.entrySet()) {
                ReportAlertDaily agg = aggMap.get(entry.getKey());
                if (agg != null) {
                    agg.setAffectedItemCount(entry.getValue().size());
                }
            }

            // 先删后插保证幂等
            LambdaQueryWrapper<ReportAlertDaily> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(ReportAlertDaily::getStatDate, statDate);
            reportAlertDailyMapper.delete(deleteWrapper);

            for (ReportAlertDaily agg : aggMap.values()) {
                reportAlertDailyMapper.insert(agg);
            }

            log.info("预警日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("预警日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
