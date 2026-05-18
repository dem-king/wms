package com.wms.item.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.item.domain.vo.StockVo;

import java.util.List;

/**
 * 库存服务接口
 * 提供库存分页查询、物品库存详情、库存预警等功能
 */
public interface StockService {

    /**
     * 库存分页列表
     * 支持按库房、类目、预警状态筛选
     *
     * @param pageParam   分页参数
     * @param warehouseId 库房ID(可选)
     * @param categoryId  主类目ID(可选)
     * @param alertOnly   是否仅显示预警库存(可选)
     * @return 分页结果
     */
    PageResult<StockVo> page(PageParam pageParam, Long warehouseId,
                             Long categoryId, Boolean alertOnly);

    /**
     * 根据物品ID获取库存详情(各库位)
     *
     * @param itemId 物品ID
     * @return 库存VO列表
     */
    List<StockVo> getByItemId(Long itemId);

    /**
     * 获取库存预警列表
     * 筛选库存数量低于安全库存的记录
     *
     * @return 预警库存VO列表
     */
    List<StockVo> getAlertList();
}
