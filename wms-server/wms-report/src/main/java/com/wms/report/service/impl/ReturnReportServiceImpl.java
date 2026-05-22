package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.ReportQueryDto;
import com.wms.report.domain.entity.ReportReturnDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.ReturnReportVo;
import com.wms.report.mapper.ReportReturnDailyMapper;
import com.wms.report.service.ReturnReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 借还统计报表服务实现类
 * 从report_return_daily预聚合表查询统计数据
 * 额外计算正常归还率
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnReportServiceImpl implements ReturnReportService {

    private final ReportReturnDailyMapper reportReturnDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建通用查询条件
     *
     * @param queryDto 报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportReturnDaily> buildWrapper(ReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportReturnDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportReturnDaily::getStatDate, queryDto.getStartDate())
                .le(ReportReturnDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getWarehouseId() != null) {
            wrapper.eq(ReportReturnDaily::getWarehouseId, queryDto.getWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportReturnDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    @Override
    public ReturnReportVo.SummaryVo getSummary(ReportQueryDto queryDto) {
        List<ReportReturnDaily> list = reportReturnDailyMapper.selectList(buildWrapper(queryDto));

        ReturnReportVo.SummaryVo vo = new ReturnReportVo.SummaryVo();
        vo.setTotalQuantity(list.stream().mapToInt(ReportReturnDaily::getTotalQuantity).sum());
        vo.setNormalQuantity(list.stream().mapToInt(ReportReturnDaily::getNormalQuantity).sum());
        vo.setDamagedQuantity(list.stream().mapToInt(ReportReturnDaily::getDamagedQuantity).sum());
        vo.setOrderCount(list.stream().mapToInt(ReportReturnDaily::getOrderCount).sum());

        // 计算正常归还率
        if (vo.getTotalQuantity() > 0) {
            vo.setNormalRate(BigDecimal.valueOf(vo.getNormalQuantity())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(vo.getTotalQuantity()), 2, RoundingMode.HALF_UP));
        } else {
            vo.setNormalRate(BigDecimal.ZERO);
        }

        // 按分类聚合
        Map<Long, ReportReturnDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportReturnDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setNormalQuantity(existing.getNormalQuantity() + added.getNormalQuantity());
                existing.setDamagedQuantity(existing.getDamagedQuantity() + added.getDamagedQuantity());
                existing.setOrderCount(existing.getOrderCount() + added.getOrderCount());
                return existing;
            });
        }

        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);
        vo.setCategorySummaryList(categoryAgg.values().stream()
                .map(e -> reportConverter.toReturnCategorySummary(e, categoryNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public ReturnReportVo.TrendVo getTrend(ReportQueryDto queryDto) {
        List<ReportReturnDaily> list = reportReturnDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        ReturnReportVo.TrendVo vo = new ReturnReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportReturnDaily daily : list) {
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
            }
            vo.setTrendList(new ArrayList<>(monthMap.values()));
        } else {
            Map<String, CommonReportVo.TrendItem> dayMap = new TreeMap<>();
            for (ReportReturnDaily daily : list) {
                String dayKey = daily.getStatDate().toString();
                CommonReportVo.TrendItem item = dayMap.computeIfAbsent(dayKey, k -> {
                    CommonReportVo.TrendItem t = new CommonReportVo.TrendItem();
                    t.setDate(k);
                    t.setQuantity(0);
                    t.setAmount(BigDecimal.ZERO);
                    return t;
                });
                item.setQuantity(item.getQuantity() + daily.getTotalQuantity());
            }
            vo.setTrendList(new ArrayList<>(dayMap.values()));
        }
        return vo;
    }

    @Override
    public ReturnReportVo.DistributionVo getDistribution(ReportQueryDto queryDto) {
        List<ReportReturnDaily> list = reportReturnDailyMapper.selectList(buildWrapper(queryDto));

        Map<Long, ReportReturnDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportReturnDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                return existing;
            });
        }

        int totalQuantity = categoryAgg.values().stream().mapToInt(ReportReturnDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        ReturnReportVo.DistributionVo vo = new ReturnReportVo.DistributionVo();
        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(categoryAgg.values()), categoryNameMap, totalQuantity));
        return vo;
    }
}
