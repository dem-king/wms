package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.report.domain.entity.ReportInboundDaily;
import com.wms.report.mapper.ReportInboundDailyMapper;
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
 * 入库日聚合定时任务
 * 每日00:01执行，聚合前一天已完成的入库单据到report_inbound_daily表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboundDailyAggregationTask {

    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsInboundDetailMapper wmsInboundDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportInboundDailyMapper reportInboundDailyMapper;

    /**
     * 入库日聚合定时任务
     * 查询前一天状态为已完成的入库单，按库房+分类聚合写入report_inbound_daily
     */
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始入库日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天完成的入库单
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<WmsInboundOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(WmsInboundOrder::getStatus, OrderStatusEnum.COMPLETED.getCode())
                    .ge(WmsInboundOrder::getUpdateTime, dayStart)
                    .lt(WmsInboundOrder::getUpdateTime, dayEnd);
            List<WmsInboundOrder> orders = wmsInboundOrderMapper.selectList(orderWrapper);

            if (orders.isEmpty()) {
                log.info("入库日聚合完成，无已完成入库单，统计日期: {}", statDate);
                return;
            }

            // 收集所有明细
            Set<Long> orderIds = orders.stream().map(WmsInboundOrder::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<WmsInboundDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(WmsInboundDetail::getOrderId, orderIds);
            List<WmsInboundDetail> allDetails = wmsInboundDetailMapper.selectList(detailWrapper);

            // 批量查询物品获取分类ID，避免N+1
            Set<Long> itemIds = allDetails.stream().map(WmsInboundDetail::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap;
            if (itemIds.isEmpty()) {
                itemCategoryMap = Collections.emptyMap();
            } else {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 构建订单ID到订单的映射
            Map<Long, WmsInboundOrder> orderMap = orders.stream()
                    .collect(Collectors.toMap(WmsInboundOrder::getId, o -> o));

            // 按库房+分类聚合
            Map<String, ReportInboundDaily> aggMap = new LinkedHashMap<>();
            for (WmsInboundDetail detail : allDetails) {
                WmsInboundOrder order = orderMap.get(detail.getOrderId());
                if (order == null) {
                    continue;
                }

                Long categoryId = itemCategoryMap.getOrDefault(detail.getItemId(), 0L);
                String key = order.getWarehouseId() + "_" + categoryId;

                ReportInboundDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportInboundDaily r = new ReportInboundDaily();
                    r.setStatDate(statDate);
                    r.setWarehouseId(order.getWarehouseId());
                    r.setCategoryId(categoryId);
                    r.setTotalQuantity(0);
                    r.setTotalAmount(BigDecimal.ZERO);
                    r.setOrderCount(0);
                    return r;
                });
                agg.setTotalQuantity(agg.getTotalQuantity() + detail.getQuantity());
                agg.setTotalAmount(agg.getTotalAmount().add(
                        detail.getAmount() != null ? detail.getAmount() : BigDecimal.ZERO));
            }

            // 计算每个库房+分类组合的订单数
            for (WmsInboundOrder order : orders) {
                Set<String> orderCategoryKeys = allDetails.stream()
                        .filter(d -> d.getOrderId().equals(order.getId()))
                        .map(d -> {
                            Long categoryId = itemCategoryMap.getOrDefault(d.getItemId(), 0L);
                            return order.getWarehouseId() + "_" + categoryId;
                        })
                        .collect(Collectors.toSet());
                for (String key : orderCategoryKeys) {
                    ReportInboundDaily agg = aggMap.get(key);
                    if (agg != null) {
                        agg.setOrderCount(agg.getOrderCount() + 1);
                    }
                }
            }

            // 逻辑删除该日期的旧数据（保证幂等）
            List<ReportInboundDaily> oldRecords = reportInboundDailyMapper.selectList(
                    new LambdaQueryWrapper<ReportInboundDaily>()
                            .eq(ReportInboundDaily::getStatDate, statDate));
            if (!oldRecords.isEmpty()) {
                List<ReportInboundDaily> updateRecords = new ArrayList<>();
                for (ReportInboundDaily oldRecord : oldRecords) {
                    ReportInboundDaily updateRecord = new ReportInboundDaily();
                    updateRecord.setId(oldRecord.getId());
                    updateRecord.setDelFlag(DelFlagConstants.DELETED);
                    updateRecords.add(updateRecord);
                }
                LogicDeleteHelper.markDeletedEntities(reportInboundDailyMapper, ReportInboundDaily.class, updateRecords);
            }

            for (ReportInboundDaily agg : aggMap.values()) {
                reportInboundDailyMapper.insert(agg);
            }

            log.info("入库日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("入库日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
