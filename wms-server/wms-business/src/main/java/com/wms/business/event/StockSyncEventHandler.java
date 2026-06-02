package com.wms.business.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.mapper.WmsStockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 库存同步事件处理器
 * 监听StockSyncEvent事件，更新wms_stock表的库存数量和金额
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockSyncEventHandler {

    private final WmsStockMapper wmsStockMapper;

    /**
     * 处理库存同步事件
     * 根据物品ID和库位ID查找库存记录，存在则更新数量，不存在则新增
     *
     * @param event 库存同步事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleStockSync(StockSyncEvent event) {
        log.info("处理库存同步事件: itemId={}, warehouseId={}, binId={}, quantity={}, type={}",
                event.getItemId(), event.getWarehouseId(), event.getBinId(),
                event.getQuantity(), event.getType());

        if (event.getBinId() == null) {
            throw new BizException("库位不能为空: itemId=" + event.getItemId()
                    + ", warehouseId=" + event.getWarehouseId());
        }

        // 查找对应库位的库存记录
        LambdaQueryWrapper<WmsStock> wrapper = new LambdaQueryWrapper<WmsStock>()
                .eq(WmsStock::getItemId, event.getItemId())
                .eq(WmsStock::getWarehouseId, event.getWarehouseId())
                .eq(WmsStock::getBinId, event.getBinId());
        WmsStock stock = wmsStockMapper.selectOne(wrapper);

        if (stock != null) {
            // 更新已有库存记录
            stock.setQuantity(stock.getQuantity() + event.getQuantity());
            // 库存数量不能小于0，超出库存时抛出业务异常
            if (stock.getQuantity() < 0) {
                throw new BizException("库存不足: itemId=" + event.getItemId()
                        + ", warehouseId=" + event.getWarehouseId()
                        + ", 当前库存=" + (stock.getQuantity() - event.getQuantity())
                        + ", 扣减数量=" + Math.abs(event.getQuantity()));
            }
            // 根据事件类型更新最后操作时间
            if ("IN".equals(event.getType())) {
                stock.setLastInboundTime(LocalDateTime.now());
            } else if ("OUT".equals(event.getType())) {
                stock.setLastOutboundTime(LocalDateTime.now());
            }
            wmsStockMapper.updateById(stock);
        } else {
            // 新增库存记录(入库场景)
            if (event.getQuantity() > 0) {
                stock = new WmsStock();
                stock.setItemId(event.getItemId());
                stock.setWarehouseId(event.getWarehouseId());
                stock.setBinId(event.getBinId());
                stock.setQuantity(event.getQuantity());
                stock.setLockedQuantity(0);
                if ("IN".equals(event.getType())) {
                    stock.setLastInboundTime(LocalDateTime.now());
                }
                wmsStockMapper.insert(stock);
            }
        }
    }
}
