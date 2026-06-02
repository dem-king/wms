package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.business.domain.entity.WmsOutboundDetail;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.report.domain.entity.ReportOutboundDaily;
import com.wms.report.mapper.ReportOutboundDailyMapper;
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
 * 出库日聚合定时任务
 * 每日00:02执行，聚合前一天已完成的出库单据到report_outbound_daily表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboundDailyAggregationTask {

    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsOutboundDetailMapper wmsOutboundDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportOutboundDailyMapper reportOutboundDailyMapper;

    /**
     * 出库日聚合定时任务
     * 查询前一天状态为已完成的出库单，按库房+分类聚合写入report_outbound_daily
     */
    @Scheduled(cron = "0 2 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始出库日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天完成的出库单
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<WmsOutboundOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(WmsOutboundOrder::getStatus, OrderStatusEnum.COMPLETED.getCode())
                    .ge(WmsOutboundOrder::getUpdateTime, dayStart)
                    .lt(WmsOutboundOrder::getUpdateTime, dayEnd);
            List<WmsOutboundOrder> orders = wmsOutboundOrderMapper.selectList(orderWrapper);

            if (orders.isEmpty()) {
                log.info("出库日聚合完成，无已完成出库单，统计日期: {}", statDate);
                return;
            }

            // 收集所有明细
            Set<Long> orderIds = orders.stream().map(WmsOutboundOrder::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<WmsOutboundDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(WmsOutboundDetail::getOrderId, orderIds);
            List<WmsOutboundDetail> allDetails = wmsOutboundDetailMapper.selectList(detailWrapper);

            // 批量查询物品获取分类ID
            Set<Long> itemIds = allDetails.stream().map(WmsOutboundDetail::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap;
            if (itemIds.isEmpty()) {
                itemCategoryMap = Collections.emptyMap();
            } else {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 构建订单ID到订单的映射
            Map<Long, WmsOutboundOrder> orderMap = orders.stream()
                    .collect(Collectors.toMap(WmsOutboundOrder::getId, o -> o));

            // 按库房+分类聚合(出库明细无金额字段，设为零)
            Map<String, ReportOutboundDaily> aggMap = new LinkedHashMap<>();
            for (WmsOutboundDetail detail : allDetails) {
                WmsOutboundOrder order = orderMap.get(detail.getOrderId());
                if (order == null) {
                    continue;
                }

                Long categoryId = itemCategoryMap.getOrDefault(detail.getItemId(), 0L);
                String key = order.getWarehouseId() + "_" + categoryId;

                ReportOutboundDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportOutboundDaily r = new ReportOutboundDaily();
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
            for (WmsOutboundOrder order : orders) {
                Set<String> orderCategoryKeys = allDetails.stream()
                        .filter(d -> d.getOrderId().equals(order.getId()))
                        .map(d -> {
                            Long categoryId = itemCategoryMap.getOrDefault(d.getItemId(), 0L);
                            return order.getWarehouseId() + "_" + categoryId;
                        })
                        .collect(Collectors.toSet());
                for (String key : orderCategoryKeys) {
                    ReportOutboundDaily agg = aggMap.get(key);
                    if (agg != null) {
                        agg.setOrderCount(agg.getOrderCount() + 1);
                    }
                }
            }

            // 逻辑删除该日期的旧数据（保证幂等）
            List<ReportOutboundDaily> oldRecords = reportOutboundDailyMapper.selectList(
                    new LambdaQueryWrapper<ReportOutboundDaily>()
                            .eq(ReportOutboundDaily::getStatDate, statDate));
            if (!oldRecords.isEmpty()) {
                List<ReportOutboundDaily> updateRecords = new ArrayList<>();
                for (ReportOutboundDaily oldRecord : oldRecords) {
                    ReportOutboundDaily updateRecord = new ReportOutboundDaily();
                    updateRecord.setId(oldRecord.getId());
                    updateRecord.setDelFlag(DelFlagConstants.DELETED);
                    updateRecords.add(updateRecord);
                }
                LogicDeleteHelper.markDeletedEntities(reportOutboundDailyMapper, ReportOutboundDaily.class, updateRecords);
            }

            for (ReportOutboundDaily agg : aggMap.values()) {
                reportOutboundDailyMapper.insert(agg);
            }

            log.info("出库日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("出库日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
