package com.wms.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.report.domain.entity.ReportStockDaily;
import com.wms.report.mapper.ReportStockDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存日快照定时任务
 * 每日00:03执行，对当前库存表按库房+分类生成快照写入report_stock_daily表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockDailySnapshotTask {

    private final WmsStockMapper wmsStockMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ReportStockDailyMapper reportStockDailyMapper;

    /**
     * 库存日快照定时任务
     * 查询当前wms_stock表，按库房+分类汇总写入report_stock_daily
     */
    @Scheduled(cron = "0 3 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void aggregate() {
        LocalDate statDate = LocalDate.now();
        log.info("开始库存日快照，快照日期: {}", statDate);

        try {
            // 查询当前所有库存记录
            List<WmsStock> stockList = wmsStockMapper.selectList(new LambdaQueryWrapper<>());

            if (stockList.isEmpty()) {
                log.info("库存日快照完成，无库存数据，快照日期: {}", statDate);
                return;
            }

            // 批量查询物品获取分类ID
            Set<Long> itemIds = stockList.stream().map(WmsStock::getItemId).collect(Collectors.toSet());
            Map<Long, Long> itemCategoryMap = new HashMap<>();
            if (!itemIds.isEmpty()) {
                itemCategoryMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, WmsItem::getCategoryId, (a, b) -> a));
            }

            // 按库房+分类聚合
            Map<String, ReportStockDaily> aggMap = new LinkedHashMap<>();
            for (WmsStock stock : stockList) {
                Long categoryId = itemCategoryMap.getOrDefault(stock.getItemId(), 0L);
                String key = stock.getWarehouseId() + "_" + categoryId;

                ReportStockDaily agg = aggMap.computeIfAbsent(key, k -> {
                    ReportStockDaily r = new ReportStockDaily();
                    r.setStatDate(statDate);
                    r.setWarehouseId(stock.getWarehouseId());
                    r.setCategoryId(categoryId);
                    r.setTotalQuantity(0);
                    r.setTotalAmount(BigDecimal.ZERO);
                    return r;
                });
                agg.setTotalQuantity(agg.getTotalQuantity() + stock.getQuantity());
                agg.setTotalAmount(agg.getTotalAmount().add(
                        stock.getAmount() != null ? stock.getAmount() : BigDecimal.ZERO));
            }

            // 逻辑删除该日期的旧数据（保证幂等）
            List<ReportStockDaily> oldRecords = reportStockDailyMapper.selectList(
                    new LambdaQueryWrapper<ReportStockDaily>()
                            .eq(ReportStockDaily::getStatDate, statDate));
            if (!oldRecords.isEmpty()) {
                List<ReportStockDaily> updateRecords = new ArrayList<>();
                for (ReportStockDaily oldRecord : oldRecords) {
                    ReportStockDaily updateRecord = new ReportStockDaily();
                    updateRecord.setId(oldRecord.getId());
                    updateRecord.setDelFlag(DelFlagConstants.DELETED);
                    updateRecords.add(updateRecord);
                }
                LogicDeleteHelper.markDeletedEntities(reportStockDailyMapper, ReportStockDaily.class, updateRecords);
            }

            for (ReportStockDaily agg : aggMap.values()) {
                reportStockDailyMapper.insert(agg);
            }

            log.info("库存日快照完成，快照日期: {}，快照记录数: {}", statDate, aggMap.size());
        } catch (Exception e) {
            log.error("库存日快照失败，快照日期: {}", statDate, e);
            throw e;
        }
    }
}
