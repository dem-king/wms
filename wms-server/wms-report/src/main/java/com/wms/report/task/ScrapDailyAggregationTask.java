package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.constant.DelFlagConstants;
import com.wms.business.domain.entity.WmsScrapDetail;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.mapper.WmsScrapDetailMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.report.domain.entity.ReportScrapDaily;
import com.wms.report.mapper.ReportScrapDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报废日聚合定时任务
 * 每日00:05执行，聚合前一天已完成的报废单据到report_scrap_daily表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScrapDailyAggregationTask {

    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsScrapDetailMapper wmsScrapDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportScrapDailyMapper reportScrapDailyMapper;

    /**
     * 报废日聚合定时任务
     * 查询前一天状态为已完成的报废单，按库房+分类聚合写入report_scrap_daily
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始报废日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天完成的报废单
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<WmsScrapOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(WmsScrapOrder::getStatus, OrderStatusEnum.COMPLETED.getCode())
                    .ge(WmsScrapOrder::getUpdateTime, dayStart)
                    .lt(WmsScrapOrder::getUpdateTime, dayEnd);
            List<WmsScrapOrder> orders = wmsScrapOrderMapper.selectList(orderWrapper);

            if (orders.isEmpty()) {
                log.info("报废日聚合完成，无已完成报废单，统计日期: {}", statDate);
                return;
            }

            // 收集所有明细
            Set<Long> orderIds = orders.stream().map(WmsScrapOrder::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<WmsScrapDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(WmsScrapDetail::getOrderId, orderIds);
            List<WmsScrapDetail> allDetails = wmsScrapDetailMapper.selectList(detailWrapper);

            // 批量查询物品获取分类ID，避免N+1
            Set<Long> itemIds = allDetails.stream().map(WmsScrapDetail::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap;
            if (itemIds.isEmpty()) {
                itemCategoryMap = Collections.emptyMap();
            } else {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 构建订单ID到订单的映射
            Map<Long, WmsScrapOrder> orderMap = orders.stream()
                    .collect(Collectors.toMap(WmsScrapOrder::getId, o -> o));

            // 按库房+分类聚合(报废明细无金额字段，设为零)
            Map<String, ReportScrapDaily> aggMap = new LinkedHashMap<>();
            for (WmsScrapDetail detail : allDetails) {
                WmsScrapOrder order = orderMap.get(detail.getOrderId());
                if (order == null) {
                    continue;
                }

                Long categoryId = itemCategoryMap.getOrDefault(detail.getItemId(), 0L);
                String key = order.getWarehouseId() + "_" + categoryId;

                ReportScrapDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportScrapDaily r = new ReportScrapDaily();
                    r.setStatDate(statDate);
                    r.setWarehouseId(order.getWarehouseId());
                    r.setCategoryId(categoryId);
                    r.setTotalQuantity(0);
                    r.setTotalAmount(BigDecimal.ZERO);
                    r.setOrderCount(0);
                    return r;
                });
                agg.setTotalQuantity(agg.getTotalQuantity() + detail.getQuantity());
            }

            // 计算每个库房+分类组合的订单数
            for (WmsScrapOrder order : orders) {
                Set<String> orderCategoryKeys = allDetails.stream()
                        .filter(d -> d.getOrderId().equals(order.getId()))
                        .map(d -> {
                            Long categoryId = itemCategoryMap.getOrDefault(d.getItemId(), 0L);
                            return order.getWarehouseId() + "_" + categoryId;
                        })
                        .collect(Collectors.toSet());
                for (String key : orderCategoryKeys) {
                    ReportScrapDaily agg = aggMap.get(key);
                    if (agg != null) {
                        agg.setOrderCount(agg.getOrderCount() + 1);
                    }
                }
            }

            // 逻辑删除该日期的旧数据（保证幂等）
            List<ReportScrapDaily> oldRecords = reportScrapDailyMapper.selectList(
                    new LambdaQueryWrapper<ReportScrapDaily>()
                            .eq(ReportScrapDaily::getStatDate, statDate));
            if (!oldRecords.isEmpty()) {
                List<ReportScrapDaily> updateRecords = new ArrayList<>();
                for (ReportScrapDaily oldRecord : oldRecords) {
                    ReportScrapDaily updateRecord = new ReportScrapDaily();
                    updateRecord.setId(oldRecord.getId());
                    updateRecord.setDelFlag(DelFlagConstants.DELETED);
                    updateRecords.add(updateRecord);
                }
                Db.updateBatchById(updateRecords);
            }

            for (ReportScrapDaily agg : aggMap.values()) {
                reportScrapDailyMapper.insert(agg);
            }

            log.info("报废日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("报废日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
