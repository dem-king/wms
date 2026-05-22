package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.entity.ReportInboundDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.InboundReportVo;
import com.wms.report.mapper.ReportInboundDailyMapper;
import com.wms.report.service.InboundReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入库统计报表服务实现类
 * 从report_inbound_daily预聚合表查询统计数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InboundReportServiceImpl implements InboundReportService {

    private final ReportInboundDailyMapper reportInboundDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建通用查询条件
     *
     * @param queryDto 报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportInboundDaily> buildWrapper(ReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportInboundDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportInboundDaily::getStatDate, queryDto.getStartDate())
                .le(ReportInboundDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportInboundDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportInboundDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    @Override
    public InboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto) {
        List<ReportInboundDaily> list = reportInboundDailyMapper.selectList(buildWrapper(queryDto));

        InboundReportVo.SummaryVo vo = new InboundReportVo.SummaryVo();
        vo.setTotalQuantity(list.stream().mapToInt(ReportInboundDaily::getTotalQuantity).sum());
        vo.setTotalAmount(list.stream().map(ReportInboundDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setOrderCount(list.stream().mapToInt(ReportInboundDaily::getOrderCount).sum());

        // 按分类聚合(同一分类可能跨多天多库房)
        Map<Long, ReportInboundDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportInboundDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(added.getTotalAmount()));
                existing.setOrderCount(existing.getOrderCount() + added.getOrderCount());
                return existing;
            });
        }

        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);
        vo.setCategorySummaryList(categoryAgg.values().stream()
                .map(e -> reportConverter.toInboundCategorySummary(e, categoryNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public InboundReportVo.TrendVo getTrend(ReportQueryDto queryDto) {
        List<ReportInboundDaily> list = reportInboundDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        InboundReportVo.TrendVo vo = new InboundReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            // 按月聚合
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportInboundDaily daily : list) {
                String monthKey = daily.getStatDate().getYear() + "-" +
                        String.format("%02d", daily.getStatDate().getMonthValue());
                CommonReportVo.TrendItem item = monthMap.computeIfAbsent(monthKey, k -> {
                    CommonReportVo.TrendItem t = new CommonReportVo.TrendItem();
                    t.setDate(k);
                    t.setQuantity(0);
                    t.setAmount(BigDecimal.ZERO);
                    return t;
                });
                item.setQuantity(item.getQuantity() + daily.getTotalQuantity());
                item.setAmount(item.getAmount().add(daily.getTotalAmount()));
            }
            vo.setTrendList(new ArrayList<>(monthMap.values()));
        } else {
            // 按日聚合
            Map<String, CommonReportVo.TrendItem> dayMap = new TreeMap<>();
            for (ReportInboundDaily daily : list) {
                String dayKey = daily.getStatDate().toString();
                CommonReportVo.TrendItem item = dayMap.computeIfAbsent(dayKey, k -> {
                    CommonReportVo.TrendItem t = new CommonReportVo.TrendItem();
                    t.setDate(k);
                    t.setQuantity(0);
                    t.setAmount(BigDecimal.ZERO);
                    return t;
                });
                item.setQuantity(item.getQuantity() + daily.getTotalQuantity());
                item.setAmount(item.getAmount().add(daily.getTotalAmount()));
            }
            vo.setTrendList(new ArrayList<>(dayMap.values()));
        }
        return vo;
    }

    @Override
    public InboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto) {
        List<ReportInboundDaily> list = reportInboundDailyMapper.selectList(buildWrapper(queryDto));

        // 按分类聚合
        Map<Long, ReportInboundDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportInboundDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(added.getTotalAmount()));
                return existing;
            });
        }

        int totalQuantity = categoryAgg.values().stream().mapToInt(ReportInboundDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        InboundReportVo.DistributionVo vo = new InboundReportVo.DistributionVo();
        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(categoryAgg.values()), categoryNameMap, totalQuantity));
        return vo;
    }
}
