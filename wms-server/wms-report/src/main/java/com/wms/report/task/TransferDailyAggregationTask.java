package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.business.domain.entity.WmsTransferDetail;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.report.domain.entity.ReportTransferDaily;
import com.wms.report.mapper.ReportTransferDailyMapper;
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
 * 调拨日聚合定时任务
 * 每日00:06执行，聚合前一天已完成的调拨单据到report_transfer_daily表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferDailyAggregationTask {

    private final WmsTransferOrderMapper wmsTransferOrderMapper;
    private final WmsTransferDetailMapper wmsTransferDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportTransferDailyMapper reportTransferDailyMapper;

    /**
     * 调拨日聚合定时任务
     * 查询前一天状态为已完成的调拨单，按调出库房+调入库房+分类聚合写入report_transfer_daily
     */
    @Scheduled(cron = "0 6 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now().minusDays(1);
        log.info("开始调拨日聚合，统计日期: {}", statDate);

        try {
            // 查询前一天完成的调拨单
            LocalDateTime dayStart = statDate.atStartOfDay();
            LocalDateTime dayEnd = statDate.plusDays(1).atStartOfDay();
            LambdaQueryWrapper<WmsTransferOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(WmsTransferOrder::getStatus, OrderStatusEnum.COMPLETED.getCode())
                    .ge(WmsTransferOrder::getUpdateTime, dayStart)
                    .lt(WmsTransferOrder::getUpdateTime, dayEnd);
            List<WmsTransferOrder> orders = wmsTransferOrderMapper.selectList(orderWrapper);

            if (orders.isEmpty()) {
                log.info("调拨日聚合完成，无已完成调拨单，统计日期: {}", statDate);
                return;
            }

            // 收集所有明细
            Set<Long> orderIds = orders.stream().map(WmsTransferOrder::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<WmsTransferDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.in(WmsTransferDetail::getOrderId, orderIds);
            List<WmsTransferDetail> allDetails = wmsTransferDetailMapper.selectList(detailWrapper);

            // 批量查询物品获取分类ID，避免N+1
            Set<Long> itemIds = allDetails.stream().map(WmsTransferDetail::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap;
            if (itemIds.isEmpty()) {
                itemCategoryMap = Collections.emptyMap();
            } else {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 构建订单ID到订单的映射
            Map<Long, WmsTransferOrder> orderMap = orders.stream()
                    .collect(Collectors.toMap(WmsTransferOrder::getId, o -> o));

            // 按调出库房+调入库房+分类聚合(调拨明细无金额字段，设为零)
            Map<String, ReportTransferDaily> aggMap = new LinkedHashMap<>();
            for (WmsTransferDetail detail : allDetails) {
                WmsTransferOrder order = orderMap.get(detail.getOrderId());
                if (order == null) {
                    continue;
                }

                Long categoryId = itemCategoryMap.getOrDefault(detail.getItemId(), 0L);
                String key = order.getFromWarehouseId() + "_" + order.getToWarehouseId() + "_" + categoryId;

                ReportTransferDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportTransferDaily r = new ReportTransferDaily();
                    r.setStatDate(statDate);
                    r.setFromWarehouseId(order.getFromWarehouseId());
                    r.setToWarehouseId(order.getToWarehouseId());
                    r.setCategoryId(categoryId);
                    r.setTotalQuantity(0);
                    r.setTotalAmount(BigDecimal.ZERO);
                    r.setOrderCount(0);
                    return r;
                });
                agg.setTotalQuantity(agg.getTotalQuantity() + detail.getQuantity());
            }

            // 计算每个调出库房+调入库房+分类组合的订单数
            for (WmsTransferOrder order : orders) {
                Set<String> orderCategoryKeys = allDetails.stream()
                        .filter(d -> d.getOrderId().equals(order.getId()))
                        .map(d -> {
                            Long categoryId = itemCategoryMap.getOrDefault(d.getItemId(), 0L);
                            return order.getFromWarehouseId() + "_" + order.getToWarehouseId() + "_" + categoryId;
                        })
                        .collect(Collectors.toSet());
                for (String key : orderCategoryKeys) {
                    ReportTransferDaily agg = aggMap.get(key);
                    if (agg != null) {
                        agg.setOrderCount(agg.getOrderCount() + 1);
                    }
                }
            }

            // 逻辑删除该日期的旧数据（保证幂等）
            List<ReportTransferDaily> oldRecords = reportTransferDailyMapper.selectList(
                    new LambdaQueryWrapper<ReportTransferDaily>()
                            .eq(ReportTransferDaily::getStatDate, statDate));
            if (!oldRecords.isEmpty()) {
                List<ReportTransferDaily> updateRecords = new ArrayList<>();
                for (ReportTransferDaily oldRecord : oldRecords) {
                    ReportTransferDaily updateRecord = new ReportTransferDaily();
                    updateRecord.setId(oldRecord.getId());
                    updateRecord.setDelFlag(DelFlagConstants.DELETED);
                    updateRecords.add(updateRecord);
                }
                LogicDeleteHelper.markDeletedEntities(reportTransferDailyMapper, ReportTransferDaily.class, updateRecords);
            }

            for (ReportTransferDaily agg : aggMap.values()) {
                reportTransferDailyMapper.insert(agg);
            }

            log.info("调拨日聚合完成，统计日期: {}，聚合记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("调拨日聚合失败，统计日期: {}", statDate, e);
            throw e;
        }
    }
}
