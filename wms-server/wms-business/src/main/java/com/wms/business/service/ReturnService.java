package com.wms.business.service;

import com.wms.business.domain.dto.ReturnOrderDto;
import com.wms.business.domain.vo.ReturnOrderVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 归还单服务接口
 * 提供归还单的创建、分页查询、详情查看等功能
 */
public interface ReturnService {

    /**
     * 归还单分页列表
     * 支持按状态、单号筛选
     *
     * @param pageParam 分页参数
     * @param status    状态(可选)
     * @param orderNo   归还单号(可选，模糊匹配)
     * @return 分页结果
     */
    PageResult<ReturnOrderVo> pageOrders(PageParam pageParam, Integer status, String orderNo);

    /**
     * 根据ID获取归还单详情(含明细)
     *
     * @param id 归还单ID
     * @return 归还单详情VO
     */
    ReturnOrderVo getOrderById(Long id);

    /**
     * 新增归还单
     * 生成归还单号GH+日期+4位流水，保存归还单及明细
     *
     * @param dto 归还单创建参数
     * @return 创建后的归还单VO
     */
    ReturnOrderVo createOrder(ReturnOrderDto dto);

    /**
     * 提交归还单
     * 状态由草稿变为待审批，并发布库存同步事件
     *
     * @param id 归还单ID
     * @return 提交后的归还单VO
     */
    ReturnOrderVo submitOrder(Long id);
}
