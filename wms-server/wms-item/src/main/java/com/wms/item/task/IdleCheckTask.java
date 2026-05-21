package com.wms.item.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.enums.ItemStatusEnum;
import com.wms.item.domain.constant.LabelConstants;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 闲置检测定时任务
 * 每天凌晨2点扫描库存表，将超过闲置阈值天数未出库的物品状态标记为闲置
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IdleCheckTask {

    private final WmsStockMapper wmsStockMapper;
    private final WmsItemMapper wmsItemMapper;

    /**
     * 闲置检测定时任务
     * 查询lastOutboundTime超过闲置阈值天数的库存记录，将对应物品状态更新为闲置
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkIdle() {
        // 计算闲置判定阈值时间点
        LocalDateTime threshold = LocalDateTime.now().minusDays(LabelConstants.IDLE_THRESHOLD_DAYS);
        log.info("闲置检测开始，阈值时间: {}", threshold);

        // 查询最后出库时间早于阈值的库存记录
        List<WmsStock> idleStocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .lt(WmsStock::getLastOutboundTime, threshold));
        if (idleStocks.isEmpty()) {
            log.info("闲置检测完成，无闲置物品");
            return;
        }

        // 收集需要标记闲置的物品ID
        Set<Long> idleItemIds = idleStocks.stream()
                .map(WmsStock::getItemId)
                .collect(Collectors.toSet());

        // 批量查询物品，筛选出非闲置状态的物品
        List<WmsItem> itemsToUpdate = wmsItemMapper.selectBatchIds(idleItemIds).stream()
                .filter(item -> item.getStatus() != null
                        && item.getStatus() != ItemStatusEnum.IDLE.getCode())
                .collect(Collectors.toList());

        // 逐个更新为闲置状态
        int count = 0;
        for (WmsItem item : itemsToUpdate) {
            item.setStatus(ItemStatusEnum.IDLE.getCode());
            wmsItemMapper.updateById(item);
            count++;
        }

        log.info("闲置检测完成，标记闲置物品数: {}", count);
    }
}
