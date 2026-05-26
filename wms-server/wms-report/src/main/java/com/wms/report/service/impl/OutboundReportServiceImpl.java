package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.entity.ReportOutboundDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.OutboundReportVo;
import com.wms.report.mapper.ReportOutboundDailyMapper;
import com.wms.report.service.OutboundReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出库统计报表服务实现类
 * 从report_outbound_daily预聚合表查询统计数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundReportServiceImpl implements OutboundReportService {

    private final ReportOutboundDailyMapper reportOutboundDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建通用查询条件
     *
     * @param queryDto 报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportOutboundDaily> buildWrapper(ReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportOutboundDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportOutboundDaily::getStatDate, queryDto.getStartDate())
                .le(ReportOutboundDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportOutboundDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportOutboundDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    /**
     * 获取OutboundReport统计汇总
     * 按分类聚合统计数量、金额和单据数
     * 
     * @param queryDto 报表查询参数
     * @return 汇总VO
     */
    @Override
    public OutboundReportVo.SummaryVo getSummary(ReportQueryDto queryDto) {
        List<ReportOutboundDaily> list = reportOutboundDailyMapper.selectList(buildWrapper(queryDto));

        OutboundReportVo.SummaryVo vo = new OutboundReportVo.SummaryVo();
        vo.setTotalQuantity(list.stream().mapToInt(ReportOutboundDaily::getTotalQuantity).sum());
        vo.setTotalAmount(list.stream().map(ReportOutboundDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setOrderCount(list.stream().mapToInt(ReportOutboundDaily::getOrderCount).sum());

        // 按分类聚合
        Map<Long, ReportOutboundDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportOutboundDaily daily : list) {
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
                .map(e -> reportConverter.toOutboundCategorySummary(e, categoryNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    /**
     * 获取OutboundReport趋势数据
     * 支持按日或按月聚合
     * 
     * @param queryDto 报表查询参数
     * @return 趋势VO
     */
    @Override
    public OutboundReportVo.TrendVo getTrend(ReportQueryDto queryDto) {
        List<ReportOutboundDaily> list = reportOutboundDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        OutboundReportVo.TrendVo vo = new OutboundReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportOutboundDaily daily : list) {
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
            Map<String, CommonReportVo.TrendItem> dayMap = new TreeMap<>();
            for (ReportOutboundDaily daily : list) {
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

    /**
     * 获取OutboundReport分布数据
     * 按分类聚合计算占比
     * 
     * @param queryDto 报表查询参数
     * @return 分布VO
     */
    @Override
    public OutboundReportVo.DistributionVo getDistribution(ReportQueryDto queryDto) {
        List<ReportOutboundDaily> list = reportOutboundDailyMapper.selectList(buildWrapper(queryDto));

        Map<Long, ReportOutboundDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportOutboundDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(added.getTotalAmount()));
                return existing;
            });
        }

        int totalQuantity = categoryAgg.values().stream().mapToInt(ReportOutboundDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        OutboundReportVo.DistributionVo vo = new OutboundReportVo.DistributionVo();
        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(categoryAgg.values()), categoryNameMap, totalQuantity));
        return vo;
    }
}
