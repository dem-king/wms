package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.entity.ReportStockDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.StockReportVo;
import com.wms.report.mapper.ReportStockDailyMapper;
import com.wms.report.service.StockReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存统计报表服务实现类
 * 从report_stock_daily预聚合表查询统计数据
 * 库存为存量型数据，汇总取时间范围内最新一天的快照
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockReportServiceImpl implements StockReportService {

    private final ReportStockDailyMapper reportStockDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建通用查询条件
     *
     * @param queryDto 报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportStockDaily> buildWrapper(ReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportStockDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportStockDaily::getStatDate, queryDto.getStartDate())
                .le(ReportStockDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportStockDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportStockDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    /**
     * 获取库存统计汇总
     * 取时间范围内最新一天快照
     * 
     * @param queryDto 报表查询参数
     * @return 汇总VO
     */
    @Override
    public StockReportVo.SummaryVo getSummary(ReportQueryDto queryDto) {
        // 库存汇总取时间范围内最新一天的快照
        LambdaQueryWrapper<ReportStockDaily> wrapper = buildWrapper(queryDto);
        wrapper.orderByDesc(ReportStockDaily::getStatDate).last("LIMIT 1");
        ReportStockDaily latest = reportStockDailyMapper.selectOne(wrapper);

        StockReportVo.SummaryVo vo = new StockReportVo.SummaryVo();
        if (latest == null) {
            vo.setTotalQuantity(0);
            vo.setTotalAmount(BigDecimal.ZERO);
            vo.setCategorySummaryList(Collections.emptyList());
            return vo;
        }

        // 查询该日期所有记录(同一天可能有多个分类)
        LambdaQueryWrapper<ReportStockDaily> dayWrapper = new LambdaQueryWrapper<>();
        dayWrapper.eq(ReportStockDaily::getStatDate, latest.getStatDate());
        if (queryDto.getWarehouseId() != null) {
            dayWrapper.eq(ReportStockDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            dayWrapper.eq(ReportStockDaily::getCategoryId, queryDto.getCategoryId());
        }
        List<ReportStockDaily> dayList = reportStockDailyMapper.selectList(dayWrapper);

        vo.setTotalQuantity(dayList.stream().mapToInt(ReportStockDaily::getTotalQuantity).sum());
        vo.setTotalAmount(dayList.stream().map(ReportStockDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Set<Long> categoryIds = dayList.stream().map(ReportStockDaily::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);
        vo.setCategorySummaryList(dayList.stream()
                .map(e -> reportConverter.toStockCategorySummary(e, categoryNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    /**
     * 获取库存趋势数据
     * 支持按日或按月(取月末快照)聚合
     * 
     * @param queryDto 报表查询参数
     * @return 趋势VO
     */
    @Override
    public StockReportVo.TrendVo getTrend(ReportQueryDto queryDto) {
        List<ReportStockDaily> list = reportStockDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        StockReportVo.TrendVo vo = new StockReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            // 按月取月末最后一天的快照值
            Map<String, ReportStockDaily> lastDayPerMonth = new TreeMap<>();
            for (ReportStockDaily daily : list) {
                String monthKey = daily.getStatDate().getYear() + "-" +
                        String.format("%02d", daily.getStatDate().getMonthValue());
                ReportStockDaily existing = lastDayPerMonth.get(monthKey);
                if (existing == null || daily.getStatDate().isAfter(existing.getStatDate())) {
                    lastDayPerMonth.put(monthKey, daily);
                }
            }
            // 按月聚合同一天多个分类的数据
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportStockDaily daily : list) {
                String monthKey = daily.getStatDate().getYear() + "-" +
                        String.format("%02d", daily.getStatDate().getMonthValue());
                if (!lastDayPerMonth.get(monthKey).getStatDate().equals(daily.getStatDate())) {
                    continue;
                }
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
            for (ReportStockDaily daily : list) {
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
     * 获取库存分布数据
     * 取最新一天快照，按分类聚合计算占比
     * 
     * @param queryDto 报表查询参数
     * @return 分布VO
     */
    @Override
    public StockReportVo.DistributionVo getDistribution(ReportQueryDto queryDto) {
        // 分布取最新一天快照
        LambdaQueryWrapper<ReportStockDaily> wrapper = buildWrapper(queryDto);
        wrapper.orderByDesc(ReportStockDaily::getStatDate).last("LIMIT 1");
        ReportStockDaily latest = reportStockDailyMapper.selectOne(wrapper);

        StockReportVo.DistributionVo vo = new StockReportVo.DistributionVo();
        if (latest == null) {
            vo.setDistributionList(Collections.emptyList());
            return vo;
        }

        LambdaQueryWrapper<ReportStockDaily> dayWrapper = new LambdaQueryWrapper<>();
        dayWrapper.eq(ReportStockDaily::getStatDate, latest.getStatDate());
        if (queryDto.getWarehouseId() != null) {
            dayWrapper.eq(ReportStockDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            dayWrapper.eq(ReportStockDaily::getCategoryId, queryDto.getCategoryId());
        }
        List<ReportStockDaily> dayList = reportStockDailyMapper.selectList(dayWrapper);

        int totalQuantity = dayList.stream().mapToInt(ReportStockDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = dayList.stream().map(ReportStockDaily::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(dayList), categoryNameMap, totalQuantity));
        return vo;
    }
}
