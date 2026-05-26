package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.AlertReportQueryDto;
import com.wms.report.domain.entity.ReportAlertDaily;
import com.wms.report.domain.vo.AlertReportVo;
import com.wms.report.mapper.ReportAlertDailyMapper;
import com.wms.report.service.AlertReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预警统计报表服务实现类
 * 从report_alert_daily预聚合表查询统计数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertReportServiceImpl implements AlertReportService {

    private final ReportAlertDailyMapper reportAlertDailyMapper;

    /**
     * 构建预警查询条件
     *
     * @param queryDto 预警报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportAlertDaily> buildWrapper(AlertReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportAlertDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportAlertDaily::getStatDate, queryDto.getStartDate())
                .le(ReportAlertDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportAlertDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getAlertType() != null) {
            wrapper.eq(ReportAlertDaily::getAlertType, queryDto.getAlertType());
        }
        return wrapper;
    }

    /**
     * 获取预警类型名称
     *
     * @param alertType 预警类型
     * @return 预警类型名称
     */
    private String getAlertTypeName(String alertType) {
        if (ReportConstants.ALERT_TYPE_STOCK_LOW.equals(alertType)) {
            return ReportConstants.ALERT_TYPE_NAME_STOCK_LOW;
        } else if (ReportConstants.ALERT_TYPE_STOCK_HIGH.equals(alertType)) {
            return ReportConstants.ALERT_TYPE_NAME_STOCK_HIGH;
        }
        return alertType;
    }

    /**
     * 获取预警统计汇总
     * 按预警类型聚合
     * 
     * @param queryDto 预警报表查询参数
     * @return 汇总VO
     */
    @Override
    public AlertReportVo.SummaryVo getSummary(AlertReportQueryDto queryDto) {
        List<ReportAlertDaily> list = reportAlertDailyMapper.selectList(buildWrapper(queryDto));

        AlertReportVo.SummaryVo vo = new AlertReportVo.SummaryVo();
        vo.setTotalTriggerCount(list.stream().mapToInt(ReportAlertDaily::getTriggerCount).sum());
        vo.setTotalAffectedItemCount(list.stream().mapToInt(ReportAlertDaily::getAffectedItemCount).sum());
        vo.setTotalResolvedCount(list.stream().mapToInt(ReportAlertDaily::getResolvedCount).sum());

        // 按预警类型聚合
        Map<String, ReportAlertDaily> typeAgg = new LinkedHashMap<>();
        for (ReportAlertDaily daily : list) {
            typeAgg.merge(daily.getAlertType(), daily, (existing, added) -> {
                existing.setTriggerCount(existing.getTriggerCount() + added.getTriggerCount());
                existing.setAffectedItemCount(existing.getAffectedItemCount() + added.getAffectedItemCount());
                existing.setResolvedCount(existing.getResolvedCount() + added.getResolvedCount());
                return existing;
            });
        }

        List<AlertReportVo.AlertSummaryItem> alertSummaryList = typeAgg.entrySet().stream()
                .map(entry -> {
                    AlertReportVo.AlertSummaryItem item = new AlertReportVo.AlertSummaryItem();
                    item.setAlertType(entry.getKey());
                    item.setAlertTypeName(getAlertTypeName(entry.getKey()));
                    item.setTriggerCount(entry.getValue().getTriggerCount());
                    item.setAffectedItemCount(entry.getValue().getAffectedItemCount());
                    item.setResolvedCount(entry.getValue().getResolvedCount());
                    return item;
                })
                .collect(Collectors.toList());
        vo.setAlertSummaryList(alertSummaryList);
        return vo;
    }

    /**
     * 获取预警趋势数据
     * 支持按日或按月聚合
     * 
     * @param queryDto 预警报表查询参数
     * @return 趋势VO
     */
    @Override
    public AlertReportVo.TrendVo getTrend(AlertReportQueryDto queryDto) {
        List<ReportAlertDaily> list = reportAlertDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        AlertReportVo.TrendVo vo = new AlertReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            // 按月聚合
            Map<String, AlertReportVo.AlertTrendItem> monthMap = new TreeMap<>();
            for (ReportAlertDaily daily : list) {
                String monthKey = daily.getStatDate().getYear() + "-" +
                        String.format("%02d", daily.getStatDate().getMonthValue());
                AlertReportVo.AlertTrendItem item = monthMap.computeIfAbsent(monthKey, k -> {
                    AlertReportVo.AlertTrendItem t = new AlertReportVo.AlertTrendItem();
                    t.setDate(k);
                    t.setTriggerCount(0);
                    t.setAffectedItemCount(0);
                    return t;
                });
                item.setTriggerCount(item.getTriggerCount() + daily.getTriggerCount());
                item.setAffectedItemCount(item.getAffectedItemCount() + daily.getAffectedItemCount());
            }
            vo.setTrendList(new ArrayList<>(monthMap.values()));
        } else {
            // 按日聚合
            Map<String, AlertReportVo.AlertTrendItem> dayMap = new TreeMap<>();
            for (ReportAlertDaily daily : list) {
                String dayKey = daily.getStatDate().toString();
                AlertReportVo.AlertTrendItem item = dayMap.computeIfAbsent(dayKey, k -> {
                    AlertReportVo.AlertTrendItem t = new AlertReportVo.AlertTrendItem();
                    t.setDate(k);
                    t.setTriggerCount(0);
                    t.setAffectedItemCount(0);
                    return t;
                });
                item.setTriggerCount(item.getTriggerCount() + daily.getTriggerCount());
                item.setAffectedItemCount(item.getAffectedItemCount() + daily.getAffectedItemCount());
            }
            vo.setTrendList(new ArrayList<>(dayMap.values()));
        }
        return vo;
    }

    /**
     * 获取预警分布数据
     * 按预警类型聚合计算占比
     * 
     * @param queryDto 预警报表查询参数
     * @return 分布VO
     */
    @Override
    public AlertReportVo.DistributionVo getDistribution(AlertReportQueryDto queryDto) {
        List<ReportAlertDaily> list = reportAlertDailyMapper.selectList(buildWrapper(queryDto));

        // 按预警类型聚合
        Map<String, ReportAlertDaily> typeAgg = new LinkedHashMap<>();
        for (ReportAlertDaily daily : list) {
            typeAgg.merge(daily.getAlertType(), daily, (existing, added) -> {
                existing.setTriggerCount(existing.getTriggerCount() + added.getTriggerCount());
                return existing;
            });
        }

        int totalTriggerCount = typeAgg.values().stream().mapToInt(ReportAlertDaily::getTriggerCount).sum();

        List<AlertReportVo.AlertDistributionItem> distributionList = typeAgg.entrySet().stream()
                .map(entry -> {
                    AlertReportVo.AlertDistributionItem item = new AlertReportVo.AlertDistributionItem();
                    item.setAlertType(entry.getKey());
                    item.setAlertTypeName(getAlertTypeName(entry.getKey()));
                    item.setTriggerCount(entry.getValue().getTriggerCount());
                    if (totalTriggerCount > 0 && item.getTriggerCount() != null) {
                        item.setPercentage(BigDecimal.valueOf(item.getTriggerCount())
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(totalTriggerCount), 2, RoundingMode.HALF_UP));
                    } else {
                        item.setPercentage(BigDecimal.ZERO);
                    }
                    return item;
                })
                .collect(Collectors.toList());

        AlertReportVo.DistributionVo vo = new AlertReportVo.DistributionVo();
        vo.setDistributionList(distributionList);
        return vo;
    }
}
