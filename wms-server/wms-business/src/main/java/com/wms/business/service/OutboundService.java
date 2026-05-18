package com.wms.business.service;

import com.wms.business.domain.dto.OutboundOrderDto;
import com.wms.business.domain.vo.OutboundOrderVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 出库单服务接口
 * 提供出库单的创建、更新、提交、删除、分页查询等功能
 */
public interface OutboundService {

    /**
     * 出库单分页列表
     * 支持按库房、出库类型、状态、单号筛选
     *
     * @param pageParam   分页参数
     * @param warehouseId 库房ID(可选)
     * @param orderType   出库类型(可选)
     * @param status      状态(可选)
     * @param orderNo     出库单号(可选，模糊匹配)
     * @return 分页结果
     */
    PageResult<OutboundOrderVo> pageOrders(PageParam pageParam, Long warehouseId,
                                           Integer orderType, Integer status, String orderNo);

    /**
     * 根据ID获取出库单详情(含明细)
     *
     * @param id 出库单ID
     * @return 出库单详情VO
     */
    OutboundOrderVo getOrderById(Long id);

    /**
     * 新增出库单
     * 生成出库单号CK+日期+4位流水，保存出库单及明细
     *
     * @param dto 出库单创建参数
     * @return 创建后的出库单VO
     */
    OutboundOrderVo createOrder(OutboundOrderDto dto);

    /**
     * 更新出库单(仅草稿状态可更新)
     *
     * @param id  出库单ID
     * @param dto 出库单更新参数
     * @return 更新后的出库单VO
     */
    OutboundOrderVo updateOrder(Long id, OutboundOrderDto dto);

    /**
     * 提交出库单
     * 草稿→待审核，触发审批或直接出库，发布StockSyncEvent
     *
     * @param id 出库单ID
     */
    void submitOrder(Long id);

    /**
     * 删除出库单(仅草稿状态可删除)
     *
     * @param id 出库单ID
     */
    void deleteOrder(Long id);
}
