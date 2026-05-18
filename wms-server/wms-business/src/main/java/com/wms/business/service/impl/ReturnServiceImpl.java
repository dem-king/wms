package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.business.domain.dto.ReturnOrderDto;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.vo.ReturnOrderVo;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnDetailMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.service.ReturnService;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 归还单服务实现类
 * 处理归还单的创建等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsReturnDetailMapper wmsReturnDetailMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    /** 归还单号前缀 */
    private static final String ORDER_NO_PREFIX = "GH";

    /** 草稿状态 */
    private static final int STATUS_DRAFT = 0;

    /** 待审核状态 */
    private static final int STATUS_PENDING = 1;

    /**
     * 分页查询归还单
     *
     * @param pageParam 分页参数
     * @param status 单据状态
     * @param orderNo 单号(模糊搜索)
     * @return 归还单分页结果
     */
    @Override
    public PageResult<ReturnOrderVo> pageOrders(PageParam pageParam, Integer status, String orderNo) {
        LambdaQueryWrapper<WmsReturnOrder> wrapper = new LambdaQueryWrapper<WmsReturnOrder>();
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsReturnOrder::getStatus, status);
        }
        // 按单号模糊搜索
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsReturnOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsReturnOrder::getCreateTime);

        Page<WmsReturnOrder> page = wmsReturnOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<ReturnOrderVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toOrderVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询归还单详情
     *
     * @param id 归还单ID
     * @return 归还单VO(含明细列表)
     */
    @Override
    public ReturnOrderVo getOrderById(Long id) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("归还单不存在");
        }
        ReturnOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 创建归还单
     * 校验关联出库单，生成归还单号，保存归还单及明细
     *
     * @param dto 归还单创建参数，包含出库单ID、明细列表
     * @return 创建后的归还单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrderVo createOrder(ReturnOrderDto dto) {
        // 校验关联出库单存在
        WmsOutboundOrder outboundOrder = wmsOutboundOrderMapper.selectById(dto.getOutboundOrderId());
        if (outboundOrder == null || outboundOrder.getDelFlag() == 1) {
            throw new BizException("关联出库单不存在");
        }

        WmsReturnOrder order = new WmsReturnOrder();
        // 生成归还单号：格式为GH + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setOutboundOrderId(dto.getOutboundOrderId());
        order.setStatus(STATUS_DRAFT);
        order.setReceiver(dto.getReceiver());
        order.setRemark(dto.getRemark());

        wmsReturnOrderMapper.insert(order);
        // 保存归还明细
        for (ReturnOrderDto.ReturnDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null || item.getDelFlag() == 1) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            WmsReturnDetail detail = new WmsReturnDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            // 默认物品状态为正常
            detail.setConditionStatus(detailDto.getConditionStatus() != null ? detailDto.getConditionStatus() : 1);
            wmsReturnDetailMapper.insert(detail);
        }

        // 归还单创建时不直接发布库存同步事件，需走审批流程后在submitOrder中发布
        // 审批通过后归还视为入库，触发库存同步

        ReturnOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(order.getId()));
        return vo;
    }

    /**
     * 提交归还单
     * 仅草稿状态可提交，提交后发布库存同步事件(归还视为入库)
     *
     * @param id 归还单ID
     * @return 提交后的归还单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrderVo submitOrder(Long id) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("归还单不存在");
        }
        // 只有草稿状态可以提交
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("只有草稿状态的归还单可以提交");
        }
        // 提交归还单，状态改为待审批
        order.setStatus(STATUS_PENDING);
        wmsReturnOrderMapper.updateById(order);

        // 归还提交后发布库存同步事件(归还视为入库)
        WmsOutboundOrder outboundOrder = wmsOutboundOrderMapper.selectById(order.getOutboundOrderId());
        List<WmsReturnDetail> details = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>().eq(WmsReturnDetail::getOrderId, id));
        for (WmsReturnDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), outboundOrder.getWarehouseId(), null,
                    detail.getQuantity(), "IN"));
        }

        ReturnOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 生成归还单号: GH + 年月日 + 4位流水号
     * 示例: GH202605140001
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<WmsReturnOrder> wrapper = new LambdaQueryWrapper<WmsReturnOrder>()
                .likeRight(WmsReturnOrder::getOrderNo, ORDER_NO_PREFIX + datePart)
                .orderByDesc(WmsReturnOrder::getOrderNo)
                .last("LIMIT 1");
        WmsReturnOrder lastOrder = wmsReturnOrderMapper.selectOne(wrapper);
        int seq = 1;
        if (lastOrder != null && lastOrder.getOrderNo() != null) {
            String lastNo = lastOrder.getOrderNo();
            String seqStr = lastNo.substring(lastNo.length() - 4);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return ORDER_NO_PREFIX + datePart + String.format("%04d", seq);
    }

    /**
     * 获取归还单明细列表
     *
     * @param orderId 归还单ID
     * @return 归还明细VO列表
     */
    private List<ReturnOrderVo.ReturnDetailVo> getOrderDetails(Long orderId) {
        List<WmsReturnDetail> details = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, orderId));
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }

    /**
     * WmsReturnOrder实体转ReturnOrderVo(填充出库单号)
     */
    private ReturnOrderVo toOrderVo(WmsReturnOrder order) {
        ReturnOrderVo vo = new ReturnOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOutboundOrderId(order.getOutboundOrderId());
        vo.setStatus(order.getStatus());
        vo.setReceiver(order.getReceiver());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        // 填充关联出库单号
        if (order.getOutboundOrderId() != null) {
            WmsOutboundOrder outboundOrder = wmsOutboundOrderMapper.selectById(order.getOutboundOrderId());
            if (outboundOrder != null) {
                vo.setOutboundOrderNo(outboundOrder.getOrderNo());
            }
        }
        return vo;
    }

    /**
     * WmsReturnDetail实体转ReturnDetailVo(填充物品名称)
     */
    private ReturnOrderVo.ReturnDetailVo toDetailVo(WmsReturnDetail detail) {
        ReturnOrderVo.ReturnDetailVo vo = new ReturnOrderVo.ReturnDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        vo.setConditionStatus(detail.getConditionStatus());
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
