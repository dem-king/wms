package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.entity.ReportScrapDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.ScrapReportVo;
import com.wms.report.mapper.ReportScrapDailyMapper;
import com.wms.report.service.ScrapReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报废统计报表服务实现类
 * 从report_scrap_daily预聚合表查询统计数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapReportServiceImpl implements ScrapReportService {

    private final ReportScrapDailyMapper reportScrapDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建通用查询条件
     *
     * @param queryDto 报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportScrapDaily> buildWrapper(ReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportScrapDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportScrapDaily::getStatDate, queryDto.getStartDate())
                .le(ReportScrapDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportScrapDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportScrapDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    @Override
    public ScrapReportVo.SummaryVo getSummary(ReportQueryDto queryDto) {
        List<ReportScrapDaily> list = reportScrapDailyMapper.selectList(buildWrapper(queryDto));

        ScrapReportVo.SummaryVo vo = new ScrapReportVo.SummaryVo();
        vo.setTotalQuantity(list.stream().mapToInt(ReportScrapDaily::getTotalQuantity).sum());
        vo.setTotalAmount(list.stream().map(ReportScrapDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setOrderCount(list.stream().mapToInt(ReportScrapDaily::getOrderCount).sum());

        // 按分类聚合(同一分类可能跨多天多库房)
        Map<Long, ReportScrapDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportScrapDaily daily : list) {
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
                .map(e -> reportConverter.toScrapCategorySummary(e, categoryNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public ScrapReportVo.TrendVo getTrend(ReportQueryDto queryDto) {
        List<ReportScrapDaily> list = reportScrapDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        ScrapReportVo.TrendVo vo = new ScrapReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            // 按月聚合
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportScrapDaily daily : list) {
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
            for (ReportScrapDaily daily : list) {
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
    public ScrapReportVo.DistributionVo getDistribution(ReportQueryDto queryDto) {
        List<ReportScrapDaily> list = reportScrapDailyMapper.selectList(buildWrapper(queryDto));

        // 按分类聚合
        Map<Long, ReportScrapDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportScrapDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(added.getTotalAmount()));
                return existing;
            });
        }

        int totalQuantity = categoryAgg.values().stream().mapToInt(ReportScrapDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        ScrapReportVo.DistributionVo vo = new ScrapReportVo.DistributionVo();
        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(categoryAgg.values()), categoryNameMap, totalQuantity));
        return vo;
    }
}
