package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.business.domain.dto.ScrapOrderDto;
import com.wms.business.domain.entity.WmsScrapDetail;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.vo.ScrapOrderVo;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsScrapDetailMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.service.ScrapService;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.business.domain.constant.OrderConstants;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

/**
 * 报废单服务实现类
 * 处理报废单的创建、提交等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsScrapDetailMapper wmsScrapDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SequenceGenerator sequenceGenerator;



    /**
     * 分页查询报废单
     *
     * @param pageParam 分页参数
     * @param status 单据状态
     * @param orderNo 单号(模糊搜索)
     * @return 报废单分页结果
     */
    @Override
    public PageResult<ScrapOrderVo> pageOrders(PageParam pageParam, Integer status, String orderNo) {
        LambdaQueryWrapper<WmsScrapOrder> wrapper = new LambdaQueryWrapper<WmsScrapOrder>();
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsScrapOrder::getStatus, status);
        }
        // 按单号模糊搜索
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsScrapOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsScrapOrder::getCreateTime);

        Page<WmsScrapOrder> page = wmsScrapOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<ScrapOrderVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toOrderVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询报废单详情
     *
     * @param id 报废单ID
     * @return 报废单VO(含明细列表)
     */
    @Override
    public ScrapOrderVo getOrderById(Long id) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("报废单不存在");
        }
        ScrapOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 创建报废单
     * 生成报废单号，保存报废单及明细
     *
     * @param dto 报废单创建参数，包含报废原因、明细列表
     * @return 创建后的报废单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScrapOrderVo createOrder(ScrapOrderDto dto) {
        WmsScrapOrder order = new WmsScrapOrder();
        // 生成报废单号：格式为BF + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        order.setScrapReason(dto.getScrapReason());

        wmsScrapOrderMapper.insert(order);
        // 保存报废明细
        for (ScrapOrderDto.ScrapDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null || item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            WmsScrapDetail detail = new WmsScrapDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            wmsScrapDetailMapper.insert(detail);
        }

        ScrapOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(order.getId()));
        return vo;
    }

    /**
     * 提交报废单
     * 仅草稿状态可提交，提交后标记为已完成并发布库存扣减事件
     *
     * @param id 报废单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("报废单不存在");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的报废单可以提交");
        }
        // 状态变为待审核
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        wmsScrapOrderMapper.updateById(order);

        // 报废单标记为已完成并发布库存扣减事件
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        wmsScrapOrderMapper.updateById(order);

        // 报废完成后扣减库存(出库)
        List<WmsScrapDetail> details = wmsScrapDetailMapper.selectList(
                new LambdaQueryWrapper<WmsScrapDetail>()
                        .eq(WmsScrapDetail::getOrderId, id));
        for (WmsScrapDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), null,
                    -detail.getQuantity(), BizConstants.STOCK_SYNC_OUT));
        }
    }

    /**
     * 生成报废单号: BF + 年月日 + 4位流水号
     * 使用Redis INCR原子操作保证并发安全
     * 示例: BF202605180001
     */
    private String generateOrderNo() {
        return sequenceGenerator.next(OrderConstants.SCRAP_NO_PREFIX);
    }

    /**
     * 获取报废单明细列表
     *
     * @param orderId 报废单ID
     * @return 报废明细VO列表
     */
    private List<ScrapOrderVo.ScrapDetailVo> getOrderDetails(Long orderId) {
        List<WmsScrapDetail> details = wmsScrapDetailMapper.selectList(
                new LambdaQueryWrapper<WmsScrapDetail>()
                        .eq(WmsScrapDetail::getOrderId, orderId));
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }

    /**
     * WmsScrapOrder实体转ScrapOrderVo
     */
    private ScrapOrderVo toOrderVo(WmsScrapOrder order) {
        ScrapOrderVo vo = new ScrapOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setScrapReason(order.getScrapReason());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        return vo;
    }

    /**
     * WmsScrapDetail实体转ScrapDetailVo(填充物品名称)
     */
    private ScrapOrderVo.ScrapDetailVo toDetailVo(WmsScrapDetail detail) {
        ScrapOrderVo.ScrapDetailVo vo = new ScrapOrderVo.ScrapDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        // 填充物品信息
        if (detail.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        return vo;
    }
}
