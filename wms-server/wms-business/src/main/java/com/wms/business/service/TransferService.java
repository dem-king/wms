package com.wms.business.service;

import com.wms.business.domain.dto.TransferOrderDto;
import com.wms.business.domain.vo.TransferOrderVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 调拨单服务接口
 * 提供调拨单的创建、提交、分页查询、详情查看等功能
 */
public interface TransferService {

    /**
     * 调拨单分页列表
     * 支持按状态、单号筛选
     *
     * @param pageParam 分页参数
     * @param status    状态(可选)
     * @param orderNo   调拨单号(可选，模糊匹配)
     * @return 分页结果
     */
    PageResult<TransferOrderVo> pageOrders(PageParam pageParam, Integer status, String orderNo);

    /**
     * 根据ID获取调拨单详情(含明细)
     *
     * @param id 调拨单ID
     * @return 调拨单详情VO
     */
    TransferOrderVo getOrderById(Long id);

    /**
     * 新增调拨单
     * 生成调拨单号DB+日期+4位流水，保存调拨单及明细
     *
     * @param dto 调拨单创建参数
     * @return 创建后的调拨单VO
     */
    TransferOrderVo createOrder(TransferOrderDto dto);

    /**
     * 提交调拨单
     * 草稿→待审核，触发审批或直接执行调拨
     *
     * @param id 调拨单ID
     */
    void submitOrder(Long id);
}
