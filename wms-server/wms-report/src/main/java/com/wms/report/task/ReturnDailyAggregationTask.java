package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnDetailMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.report.domain.constant.ReportConstants;
import com.wms.report.domain.entity.ReportReturnDaily;
import com.wms.report.mapper.ReportReturnDailyMapper;
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
 * 归还日聚合定时任务
 * 每日00:04执行，聚合前一天已完成的归还单据到report_return_daily表
 * 归还单无warehouseId，需通过关联出库单获取
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReturnDailyAggregationTask {

    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsReturnDetailMapper wmsReturnDetailMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportReturnDailyMapper reportReturnDailyMapper;

    /**
     * 归还日聚合定时任务
     * 查询前一天状态为已完成的归还单，通过关联出库单获取库房ID，按库房+分类聚合写入report_return_daily
     */
    @Scheduled(cron = "0 4 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始归还日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天完成的归还单
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<WmsReturnOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(WmsReturnOrder::getStatus, OrderStatusEnum.COMPLETED.getCode())
                    .ge(WmsReturnOrder::getUpdateTime, dayStart)
                    .lt(WmsReturnOrder::getUpdateTime, dayEnd);
            List<WmsReturnOrder> orders = wmsReturnOrderMapper.selectList(orderWrapper);

            if (orders.isEmpty()) {
                log.info("归还日聚合完成，无已完成归还单，统计日期: {}", statDate);
                return;
            }

            // 批量查询关联出库单获取warehouseId
            Set<Long> outboundOrderIds = orders.stream()
                    .map(WmsReturnOrder::getOutboundOrderId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Map<Long, WmsOutboundOrder> outboundOrderMap = new HashMap<>();
            if (!outboundOrderIds.isEmpty()) {
                outboundOrderMap = wmsOutboundOrderMapper.selectBatchIds(outboundOrderIds).stream()
                        .collect(Collectors.toMap(WmsOutboundOrder::getId, o -> o));
            }

            // 收集所有明细
            Set<Long> orderIds = orders.stream().map(WmsReturnOrder::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<WmsReturnDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(WmsReturnDetail::getOrderId, orderIds);
            List<WmsReturnDetail> allDetails = wmsReturnDetailMapper.selectList(detailWrapper);

            // 批量查询物品获取分类ID
            Set<Long> itemIds = allDetails.stream().map(WmsReturnDetail::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap;
            if (itemIds.isEmpty()) {
                itemCategoryMap = Collections.emptyMap();
            } else {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 构建归还单ID到归还单的映射
            Map<Long, WmsReturnOrder> returnOrderMap = orders.stream()
                    .collect(Collectors.toMap(WmsReturnOrder::getId, o -> o));

            // 按库房+分类聚合
            Map<String, ReportReturnDaily> aggMap = new LinkedHashMap<>();
            for (WmsReturnDetail detail : allDetails) {
                WmsReturnOrder returnOrder = returnOrderMap.get(detail.getOrderId());
                if (returnOrder == null) {
                    continue;
                }

                // 通过关联出库单获取库房ID
                Long warehouseId = 0L;
                if (returnOrder.getOutboundOrderId() != null) {
                    WmsOutboundOrder outboundOrder = outboundOrderMap.get(returnOrder.getOutboundOrderId());
                    if (outboundOrder != null) {
                        warehouseId = outboundOrder.getWarehouseId();
                    }
                }
                final Long finalWarehouseId = warehouseId;

                Long categoryId = itemCategoryMap.getOrDefault(detail.getItemId(), 0L);
                String key = finalWarehouseId + "_" + categoryId;

                ReportReturnDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportReturnDaily r = new ReportReturnDaily();
                    r.setStatDate(statDate);
                    r.setWarehouseId(finalWarehouseId);
                    r.setCategoryId(categoryId);
                    r.setTotalQuantity(0);
                    r.setNormalQuantity(0);
                    r.setDamagedQuantity(0);
                    r.setOrderCount(0);
                    return r;
                });
                agg.setTotalQuantity(agg.getTotalQuantity() + detail.getQuantity());
                // 按物品状态区分正常/损坏归还
                if (detail.getConditionStatus() != null
                        && detail.getConditionStatus() == ReportConstants.CONDITION_NORMAL) {
                    agg.setNormalQuantity(agg.getNormalQuantity() + detail.getQuantity());
                } else if (detail.getConditionStatus() != null
                        && detail.getConditionStatus() == ReportConstants.CONDITION_DAMAGED) {
                    agg.setDamagedQuantity(agg.getDamagedQuantity() + detail.getQuantity());
                }
            }

            // 计算每个库房+分类组合的订单数
            for (WmsReturnOrder returnOrder : orders) {
                Long warehouseId = 0L;
                if (returnOrder.getOutboundOrderId() != null) {
                    WmsOutboundOrder outboundOrder = outboundOrderMap.get(returnOrder.getOutboundOrderId());
                    if (outboundOrder != null) {
                        warehouseId = outboundOrder.getWarehouseId();
                    }
                }
                final Long finalWarehouseId = warehouseId;
                Set<String> orderCategoryKeys = allDetails.stream()
                        .filter(d -> d.getOrderId().equals(returnOrder.getId()))
                        .map(d -> {
                            Long categoryId = itemCategoryMap.getOrDefault(d.getItemId(), 0L);
                            return finalWarehouseId + "_" + categoryId;
                        })
                        .collect(Collectors.toSet());
                for (String key : orderCategoryKeys) {
                    ReportReturnDaily agg = aggMap.get(key);
                    if (agg != null) {
                        agg.setOrderCount(agg.getOrderCount() + 1);
                    }
                }
            }

            // 先删后插保证幂等
            LambdaQueryWrapper<ReportReturnDaily> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(ReportReturnDaily::getStatDate, statDate);
            reportReturnDailyMapper.delete(deleteWrapper);

            for (ReportReturnDaily agg : aggMap.values()) {
                reportReturnDailyMapper.insert(agg);
            }

            log.info("归还日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("归还日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
