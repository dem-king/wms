package com.wms.business.service;

import com.wms.business.domain.dto.ScrapOrderDto;
import com.wms.business.domain.vo.ScrapOrderVo;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;

/**
 * 报废单服务接口
 * 提供报废单的创建、提交、分页查询、详情查看等功能
 */
public interface ScrapService {

    /**
     * 报废单分页列表
     * 支持按状态、单号筛选
     *
     * @param pageParam 分页参数
     * @param status    状态(可选)
     * @param orderNo   报废单号(可选，模糊匹配)
     * @return 分页结果
     */
    PageResult<ScrapOrderVo> pageOrders(PageParam pageParam, Integer status, String orderNo);

    /**
     * 根据ID获取报废单详情(含明细)
     *
     * @param id 报废单ID
     * @return 报废单详情VO
     */
    ScrapOrderVo getOrderById(Long id);

    /**
     * 新增报废单
     * 生成报废单号BF+日期+4位流水，保存报废单及明细
     *
     * @param dto 报废单创建参数
     * @return 创建后的报废单VO
     */
    ScrapOrderVo createOrder(ScrapOrderDto dto);

    /**
     * 提交报废单
     * 草稿→待审核，触发审批或直接执行报废
     *
     * @param id 报废单ID
     */
    void submitOrder(Long id);
}
