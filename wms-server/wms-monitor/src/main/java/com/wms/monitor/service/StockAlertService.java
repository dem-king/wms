package com.wms.monitor.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.monitor.domain.vo.StockAlertVo;

/**
 * 库存预警服务接口
 * 提供预警记录分页查询、处理等操作
 */
public interface StockAlertService {

    /**
     * 分页查询库存预警记录
     *
     * @param pageParam   分页参数
     * @param alertType   预警类型(可选)
     * @param status      处理状态(可选)
     * @param warehouseId 库房ID(可选)
     * @return 分页结果
     */
    PageResult<StockAlertVo> page(PageParam pageParam, String alertType, String status, Long warehouseId);

    /**
     * 处理预警记录
     * 将指定预警记录状态更新为RESOLVED
     *
     * @param id 预警记录ID
     * @return 处理后的预警VO
     */
    StockAlertVo resolve(Long id);
}
