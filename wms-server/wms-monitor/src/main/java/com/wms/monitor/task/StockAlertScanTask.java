package com.wms.monitor.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.monitor.domain.constant.MonitorConstants;
import com.wms.monitor.domain.entity.MonitorStockAlert;
import com.wms.monitor.mapper.MonitorStockAlertMapper;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存预警扫描定时任务
 * 每天8:00执行，扫描库存与阈值对比，生成/更新预警记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockAlertScanTask {

    private final WmsStockMapper wmsStockMapper;
    private final WmsItemMapper wmsItemMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final MonitorStockAlertMapper monitorStockAlertMapper;

    /**
     * 库存预警扫描定时任务
     * 查询库存表按物品+库房汇总，对比物品的库存上下限阈值
     * 去重：已有PENDING记录则更新triggerTime和currentQuantity，无则插入
     * 恢复正常：库存正常的物品，其PENDING记录标记RESOLVED
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void scan() {
        log.info("库存预警扫描开始");
        LocalDateTime now = LocalDateTime.now();

        // 查询所有库存记录
        List<WmsStock> stockList = wmsStockMapper.selectList(new LambdaQueryWrapper<>());
        if (stockList.isEmpty()) {
            log.info("库存预警扫描完成，无库存记录");
            return;
        }

        // 按物品ID+库房ID汇总库存数量
        Map<String, Integer> stockSummary = new LinkedHashMap<>();
        Map<String, Long> itemIdMap = new HashMap<>();
        Map<String, Long> warehouseIdMap = new HashMap<>();
        for (WmsStock stock : stockList) {
            String key = stock.getItemId() + "_" + stock.getWarehouseId();
            stockSummary.merge(key, stock.getQuantity(), Integer::sum);
            itemIdMap.putIfAbsent(key, stock.getItemId());
            warehouseIdMap.putIfAbsent(key, stock.getWarehouseId());
        }

        // 批量查询物品信息
        Set<Long> itemIds = itemIdMap.values().stream().collect(Collectors.toSet());
        Map<Long, WmsItem> itemMap = wmsItemMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(WmsItem::getId, item -> item));

        // 批量查询库房信息
        Set<Long> warehouseIds = warehouseIdMap.values().stream().collect(Collectors.toSet());
        Map<Long, WmsWarehouse> warehouseMap = wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, w -> w));

        // 查询所有未处理的预警记录
        LambdaQueryWrapper<MonitorStockAlert> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(MonitorStockAlert::getStatus, MonitorConstants.ALERT_STATUS_PENDING);
        List<MonitorStockAlert> pendingAlerts = monitorStockAlertMapper.selectList(pendingWrapper);
        // 按物品ID+库房ID+预警类型构建已存在预警的映射
        Map<String, MonitorStockAlert> existingAlertMap = pendingAlerts.stream()
                .collect(Collectors.toMap(
                        a -> a.getItemId() + "_" + a.getWarehouseId() + "_" + a.getAlertType(),
                        a -> a,
                        (a, b) -> a
                ));

        // 收集需要标记为RESOLVED的预警记录ID
        Set<Long> resolvedIds = new HashSet<>();
        // 收集当前触发的预警key，用于判断哪些PENDING记录应该RESOLVED
        Set<String> currentAlertKeys = new HashSet<>();

        for (Map.Entry<String, Integer> entry : stockSummary.entrySet()) {
            String key = entry.getKey();
            Long itemId = itemIdMap.get(key);
            Long warehouseId = warehouseIdMap.get(key);
            Integer currentQty = entry.getValue();

            WmsItem item = itemMap.get(itemId);
            if (item == null) {
                continue;
            }
            WmsWarehouse warehouse = warehouseMap.get(warehouseId);
            String warehouseName = warehouse != null ? warehouse.getWarehouseName() : "";

            // 检查库存不足预警
            if (item.getStockLowerLimit() != null && currentQty < item.getStockLowerLimit()) {
                String alertKey = itemId + "_" + warehouseId + "_" + MonitorConstants.ALERT_TYPE_STOCK_LOW;
                currentAlertKeys.add(alertKey);
                upsertAlert(alertKey, existingAlertMap, itemId, item, warehouseId, warehouseName,
                        currentQty, item.getStockLowerLimit(), MonitorConstants.ALERT_TYPE_STOCK_LOW, now);
            }

            // 检查库存超储预警
            if (item.getStockUpperLimit() != null && currentQty > item.getStockUpperLimit()) {
                String alertKey = itemId + "_" + warehouseId + "_" + MonitorConstants.ALERT_TYPE_STOCK_HIGH;
                currentAlertKeys.add(alertKey);
                upsertAlert(alertKey, existingAlertMap, itemId, item, warehouseId, warehouseName,
                        currentQty, item.getStockUpperLimit(), MonitorConstants.ALERT_TYPE_STOCK_HIGH, now);
            }
        }

        // 恢复正常：PENDING记录但当前不再触发的标记为RESOLVED
        for (MonitorStockAlert alert : pendingAlerts) {
            String alertKey = alert.getItemId() + "_" + alert.getWarehouseId() + "_" + alert.getAlertType();
            if (!currentAlertKeys.contains(alertKey)) {
                alert.setStatus(MonitorConstants.ALERT_STATUS_RESOLVED);
                monitorStockAlertMapper.updateById(alert);
            }
        }

        log.info("库存预警扫描完成");
    }

    /**
     * 新增或更新预警记录
     *
     * @param alertKey          预警唯一标识(itemId_warehouseId_alertType)
     * @param existingAlertMap  已存在的PENDING预警映射
     * @param itemId            物品ID
     * @param item              物品实体
     * @param warehouseId       库房ID
     * @param warehouseName     库房名称
     * @param currentQty        当前库存量
     * @param thresholdValue    触发阈值
     * @param alertType         预警类型
     * @param now               当前时间
     */
    private void upsertAlert(String alertKey, Map<String, MonitorStockAlert> existingAlertMap,
                             Long itemId, WmsItem item, Long warehouseId, String warehouseName,
                             Integer currentQty, Integer thresholdValue, String alertType, LocalDateTime now) {
        MonitorStockAlert existing = existingAlertMap.get(alertKey);
        if (existing != null) {
            // 已有PENDING记录，更新触发时间和当前库存量
            existing.setCurrentQuantity(currentQty);
            existing.setThresholdValue(thresholdValue);
            existing.setTriggerTime(now);
            monitorStockAlertMapper.updateById(existing);
        } else {
            // 新增预警记录
            MonitorStockAlert alert = new MonitorStockAlert();
            alert.setAlertType(alertType);
            alert.setItemId(itemId);
            alert.setItemName(item.getItemName());
            alert.setItemCode(item.getItemCode());
            alert.setWarehouseId(warehouseId);
            alert.setWarehouseName(warehouseName);
            alert.setCurrentQuantity(currentQty);
            alert.setThresholdValue(thresholdValue);
            alert.setStatus(MonitorConstants.ALERT_STATUS_PENDING);
            alert.setTriggerTime(now);
            monitorStockAlertMapper.insert(alert);
        }
    }
}
