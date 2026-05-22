package com.wms.monitor.converter;

import com.wms.monitor.domain.entity.MonitorOverdueReturn;
import com.wms.monitor.domain.entity.MonitorStockAlert;
import com.wms.monitor.domain.vo.OverdueReturnVo;
import com.wms.monitor.domain.vo.StockAlertVo;
import org.springframework.stereotype.Component;

/**
 * 监控模块转换器
 * 负责预警和逾期归还实体到视图对象的转换
 */
@Component
public class MonitorConverter {

    /**
     * 库存预警实体转视图对象
     *
     * @param entity 库存预警实体
     * @return 库存预警视图对象
     */
    public StockAlertVo toStockAlertVo(MonitorStockAlert entity) {
        StockAlertVo vo = new StockAlertVo();
        vo.setId(entity.getId());
        vo.setAlertType(entity.getAlertType());
        vo.setItemId(entity.getItemId());
        vo.setItemName(entity.getItemName());
        vo.setItemCode(entity.getItemCode());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setCurrentQuantity(entity.getCurrentQuantity());
        vo.setThresholdValue(entity.getThresholdValue());
        vo.setStatus(entity.getStatus());
        vo.setTriggerTime(entity.getTriggerTime());
        return vo;
    }

    /**
     * 逾期归还实体转视图对象
     *
     * @param entity 逾期归还实体
     * @return 逾期归还视图对象
     */
    public OverdueReturnVo toOverdueReturnVo(MonitorOverdueReturn entity) {
        OverdueReturnVo vo = new OverdueReturnVo();
        vo.setId(entity.getId());
        vo.setOrderId(entity.getOrderId());
        vo.setOrderNo(entity.getOrderNo());
        vo.setItemId(entity.getItemId());
        vo.setItemName(entity.getItemName());
        vo.setItemCode(entity.getItemCode());
        vo.setBorrowQuantity(entity.getBorrowQuantity());
        vo.setBorrowerName(entity.getBorrowerName());
        vo.setBorrowTime(entity.getBorrowTime());
        vo.setExpectedReturnDate(entity.getExpectedReturnDate());
        vo.setOverdueDays(entity.getOverdueDays());
        vo.setAlertLevel(entity.getAlertLevel());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
