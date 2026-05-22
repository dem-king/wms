package com.wms.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.report.converter.ReportConverter;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.dto.TransferReportQueryDto;
import com.wms.report.domain.entity.ReportTransferDaily;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.TransferReportVo;
import com.wms.report.mapper.ReportTransferDailyMapper;
import com.wms.report.service.TransferReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 调拨统计报表服务实现类
 * 从report_transfer_daily预聚合表查询统计数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferReportServiceImpl implements TransferReportService {

    private final ReportTransferDailyMapper reportTransferDailyMapper;
    private final ReportConverter reportConverter;

    /**
     * 构建调拨查询条件
     *
     * @param queryDto 调拨报表查询参数
     * @return LambdaQueryWrapper
     */
    private LambdaQueryWrapper<ReportTransferDaily> buildWrapper(TransferReportQueryDto queryDto) {
        LambdaQueryWrapper<ReportTransferDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ReportTransferDaily::getStatDate, queryDto.getStartDate())
                .le(ReportTransferDaily::getStatDate, queryDto.getEndDate());
        if (queryDto.getFromWarehouseId() != null) {
            wrapper.eq(ReportTransferDaily::getFromWarehouseId, queryDto.getFromWarehouseId());
        }
        if (queryDto.getToWarehouseId() != null) {
            wrapper.eq(ReportTransferDaily::getToWarehouseId, queryDto.getToWarehouseId());
        }
        if (queryDto.getCategoryId() != null) {
            wrapper.eq(ReportTransferDaily::getCategoryId, queryDto.getCategoryId());
        }
        return wrapper;
    }

    @Override
    public TransferReportVo.SummaryVo getSummary(TransferReportQueryDto queryDto) {
        List<ReportTransferDaily> list = reportTransferDailyMapper.selectList(buildWrapper(queryDto));

        TransferReportVo.SummaryVo vo = new TransferReportVo.SummaryVo();
        vo.setTotalQuantity(list.stream().mapToInt(ReportTransferDaily::getTotalQuantity).sum());
        vo.setTotalAmount(list.stream().map(ReportTransferDaily::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        vo.setOrderCount(list.stream().mapToInt(ReportTransferDaily::getOrderCount).sum());

        // 按调出库房+调入库房+分类聚合
        Map<String, ReportTransferDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportTransferDaily daily : list) {
            String key = daily.getFromWarehouseId() + "_" + daily.getToWarehouseId() + "_" + daily.getCategoryId();
            categoryAgg.merge(key, daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(added.getTotalAmount()));
                existing.setOrderCount(existing.getOrderCount() + added.getOrderCount());
                return existing;
            });
        }

        Set<Long> categoryIds = categoryAgg.values().stream()
                .map(ReportTransferDaily::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        Set<Long> warehouseIds = new HashSet<>();
        for (ReportTransferDaily daily : categoryAgg.values()) {
            warehouseIds.add(daily.getFromWarehouseId());
            warehouseIds.add(daily.getToWarehouseId());
        }
        Map<Long, String> warehouseNameMap = reportConverter.buildWarehouseNameMap(warehouseIds);

        vo.setCategorySummaryList(categoryAgg.values().stream()
                .map(e -> reportConverter.toTransferCategorySummary(e, categoryNameMap, warehouseNameMap))
                .collect(Collectors.toList()));
        return vo;
    }

    @Override
    public TransferReportVo.TrendVo getTrend(TransferReportQueryDto queryDto) {
        List<ReportTransferDaily> list = reportTransferDailyMapper.selectList(buildWrapper(queryDto));

        String trendType = queryDto.getTrendType() != null ? queryDto.getTrendType() : ReportConstants.TREND_TYPE_DAILY;
        TransferReportVo.TrendVo vo = new TransferReportVo.TrendVo();
        vo.setTrendType(trendType);

        if (ReportConstants.TREND_TYPE_MONTHLY.equals(trendType)) {
            // 按月聚合
            Map<String, CommonReportVo.TrendItem> monthMap = new TreeMap<>();
            for (ReportTransferDaily daily : list) {
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
            for (ReportTransferDaily daily : list) {
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
    public TransferReportVo.DistributionVo getDistribution(TransferReportQueryDto queryDto) {
        List<ReportTransferDaily> list = reportTransferDailyMapper.selectList(buildWrapper(queryDto));

        // 按分类聚合
        Map<Long, ReportTransferDaily> categoryAgg = new LinkedHashMap<>();
        for (ReportTransferDaily daily : list) {
            categoryAgg.merge(daily.getCategoryId(), daily, (existing, added) -> {
                existing.setTotalQuantity(existing.getTotalQuantity() + added.getTotalQuantity());
                existing.setTotalAmount(existing.getTotalAmount().add(added.getTotalAmount()));
                return existing;
            });
        }

        int totalQuantity = categoryAgg.values().stream().mapToInt(ReportTransferDaily::getTotalQuantity).sum();
        Set<Long> categoryIds = categoryAgg.keySet();
        Map<Long, String> categoryNameMap = reportConverter.buildCategoryNameMap(categoryIds);

        TransferReportVo.DistributionVo vo = new TransferReportVo.DistributionVo();
        vo.setDistributionList(reportConverter.toDistributionList(
                new ArrayList<>(categoryAgg.values()), categoryNameMap, totalQuantity));
        return vo;
    }
}
