package com.wms.business.service;

import com.wms.business.domain.dto.InboundOrderDto;
import com.wms.business.domain.vo.InboundOrderVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 入库单服务接口
 * 提供入库单的创建、更新、提交、删除、分页查询等功能
 */
public interface InboundService {

    /**
     * 入库单分页列表
     * 支持按库房、入库类型、状态、单号筛选
     *
     * @param pageParam   分页参数
     * @param warehouseId 库房ID(可选)
     * @param orderType   入库类型(可选)
     * @param status      状态(可选)
     * @param orderNo     入库单号(可选，模糊匹配)
     * @return 分页结果
     */
    PageResult<InboundOrderVo> pageOrders(PageParam pageParam, Long warehouseId,
                                          Integer orderType, Integer status, String orderNo);

    /**
     * 根据ID获取入库单详情(含明细)
     *
     * @param id 入库单ID
     * @return 入库单详情VO
     */
    InboundOrderVo getOrderById(Long id);

    /**
     * 新增入库单
     * 生成入库单号RK+日期+4位流水，保存入库单及明细
     *
     * @param dto 入库单创建参数
     * @return 创建后的入库单VO
     */
    InboundOrderVo createOrder(InboundOrderDto dto);

    /**
     * 更新入库单(仅草稿状态可更新)
     *
     * @param id  入库单ID
     * @param dto 入库单更新参数
     * @return 更新后的入库单VO
     */
    InboundOrderVo updateOrder(Long id, InboundOrderDto dto);

    /**
     * 提交入库单
     * 草稿→待审核，触发审批或直接入库，发布StockSyncEvent
     *
     * @param id 入库单ID
     */
    void submitOrder(Long id);

    /**
     * 删除入库单(仅草稿状态可删除)
     *
     * @param id 入库单ID
     */
    void deleteOrder(Long id);
}
