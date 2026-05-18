package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.business.domain.dto.OutboundOrderDto;
import com.wms.business.domain.entity.WmsOutboundDetail;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.vo.OutboundOrderVo;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.service.OutboundService;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出库单服务实现类
 * 处理出库单的创建、审核、完成等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class OutboundServiceImpl implements OutboundService {

    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsOutboundDetailMapper wmsOutboundDetailMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final WmsItemMapper wmsItemMapper;
    private final WmsBinMapper wmsBinMapper;
    private final ApplicationEventPublisher eventPublisher;

    /** 出库单号前缀 */
    private static final String ORDER_NO_PREFIX = "CK";

    /** 草稿状态 */
    private static final int STATUS_DRAFT = 0;

    /** 待审核状态 */
    private static final int STATUS_PENDING = 1;

    /** 已完成状态 */
    private static final int STATUS_COMPLETED = 3;

    /**
     * 分页查询出库单
     *
     * @param pageParam 分页参数
     * @param warehouseId 库房ID
     * @param orderType 出库类型
     * @param status 单据状态
     * @param orderNo 单号(模糊搜索)
     * @return 出库单分页结果
     */
    @Override
    public PageResult<OutboundOrderVo> pageOrders(PageParam pageParam, Long warehouseId,
                                                   Integer orderType, Integer status, String orderNo) {
        LambdaQueryWrapper<WmsOutboundOrder> wrapper = new LambdaQueryWrapper<WmsOutboundOrder>();
        // 按库房筛选
        if (warehouseId != null) {
            wrapper.eq(WmsOutboundOrder::getWarehouseId, warehouseId);
        }
        // 按出库类型筛选
        if (orderType != null) {
            wrapper.eq(WmsOutboundOrder::getOrderType, orderType);
        }
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsOutboundOrder::getStatus, status);
        }
        // 按单号模糊搜索
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsOutboundOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsOutboundOrder::getCreateTime);

        Page<WmsOutboundOrder> page = wmsOutboundOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<OutboundOrderVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toOrderVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询出库单详情
     *
     * @param id 出库单ID
     * @return 出库单VO(含明细列表)
     */
    @Override
    public OutboundOrderVo getOrderById(Long id) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("出库单不存在");
        }
        OutboundOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 创建出库单
     * 校验库房，生成出库单号，保存出库单及明细
     *
     * @param dto 出库单创建参数，包含库房ID、明细列表
     * @return 创建后的出库单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundOrderVo createOrder(OutboundOrderDto dto) {
        // 校验库房是否存在且为启用状态
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == 1) {
            throw new BizException("库房不存在或已禁用");
        }

        WmsOutboundOrder order = new WmsOutboundOrder();
        // 生成出库单号：格式为CK + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setWarehouseId(dto.getWarehouseId());
        order.setOrderType(dto.getOrderType());
        order.setStatus(STATUS_DRAFT);
        order.setReceiver(dto.getReceiver());
        order.setPurpose(dto.getPurpose());
        order.setExpectedReturnDate(dto.getExpectedReturnDate());
        order.setRemark(dto.getRemark());

        wmsOutboundOrderMapper.insert(order);
        // 保存出库明细
        for (OutboundOrderDto.OutboundDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null || item.getDelFlag() == 1) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            WmsOutboundDetail detail = new WmsOutboundDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setBinId(detailDto.getBinId());
            wmsOutboundDetailMapper.insert(detail);
        }

        OutboundOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(order.getId()));
        return vo;
    }

    /**
     * 更新出库单
     * 仅草稿状态可更新，逻辑删除原有明细后重新保存
     *
     * @param id 出库单ID
     * @param dto 出库单更新参数
     * @return 更新后的出库单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundOrderVo updateOrder(Long id, OutboundOrderDto dto) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("出库单不存在");
        }
        // 仅草稿状态可更新
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的出库单可以更新");
        }

        // 校验库房
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == 1) {
            throw new BizException("库房不存在或已禁用");
        }

        order.setWarehouseId(dto.getWarehouseId());
        order.setOrderType(dto.getOrderType());
        order.setReceiver(dto.getReceiver());
        order.setPurpose(dto.getPurpose());
        order.setExpectedReturnDate(dto.getExpectedReturnDate());
        order.setRemark(dto.getRemark());
        wmsOutboundOrderMapper.updateById(order);

        // 逻辑删除原有明细后重新保存
        List<WmsOutboundDetail> oldDetails = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>().eq(WmsOutboundDetail::getOrderId, id));
        for (WmsOutboundDetail oldDetail : oldDetails) {
            WmsOutboundDetail updateDetail = new WmsOutboundDetail();
            updateDetail.setId(oldDetail.getId());
            updateDetail.setDelFlag(1);
            updateDetail.setLastOperType("d");
            wmsOutboundDetailMapper.updateById(updateDetail);
        }
        for (OutboundOrderDto.OutboundDetailDto detailDto : dto.getDetails()) {
            WmsOutboundDetail detail = new WmsOutboundDetail();
            detail.setOrderId(id);
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setBinId(detailDto.getBinId());
            wmsOutboundDetailMapper.insert(detail);
        }

        OutboundOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 提交出库单
     * 仅草稿状态可提交，提交后标记为已完成并发布库存扣减事件
     *
     * @param id 出库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("出库单不存在");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的出库单可以提交");
        }

        // 状态变为待审核
        order.setStatus(STATUS_PENDING);
        wmsOutboundOrderMapper.updateById(order);

        // 直接标记为已完成并发布库存同步事件(简化流程)
        order.setStatus(STATUS_COMPLETED);
        wmsOutboundOrderMapper.updateById(order);

        // 查询出库明细，逐条发布库存同步事件(出库为负数)
        List<WmsOutboundDetail> details = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>()
                        .eq(WmsOutboundDetail::getOrderId, id));
        for (WmsOutboundDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), detail.getBinId(),
                    -detail.getQuantity(), "OUT"));
        }
    }

    /**
     * 删除出库单
     * 仅草稿状态可删除，逻辑删除出库单及明细
     *
     * @param id 出库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("出库单不存在");
        }
        // 仅草稿状态可删除
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的出库单可以删除");
        }
        // 逻辑删除出库单
        WmsOutboundOrder updateEntity = new WmsOutboundOrder();
        updateEntity.setId(id);
        updateEntity.setDelFlag(1);
        updateEntity.setLastOperType("d");
        wmsOutboundOrderMapper.updateById(updateEntity);
        // 逻辑删除出库明细
        List<WmsOutboundDetail> details = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>()
                        .eq(WmsOutboundDetail::getOrderId, id));
        for (WmsOutboundDetail detail : details) {
            WmsOutboundDetail updateDetail = new WmsOutboundDetail();
            updateDetail.setId(detail.getId());
            updateDetail.setDelFlag(1);
            updateDetail.setLastOperType("d");
            wmsOutboundDetailMapper.updateById(updateDetail);
        }
    }

    /**
     * 生成出库单号: CK + 年月日 + 4位流水号
     * 示例: CK202605140001
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<WmsOutboundOrder> wrapper = new LambdaQueryWrapper<WmsOutboundOrder>()
                .likeRight(WmsOutboundOrder::getOrderNo, ORDER_NO_PREFIX + datePart)
                .orderByDesc(WmsOutboundOrder::getOrderNo)
                .last("LIMIT 1");
        WmsOutboundOrder lastOrder = wmsOutboundOrderMapper.selectOne(wrapper);
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
     * 获取出库单明细列表
     *
     * @param orderId 出库单ID
     * @return 出库明细VO列表
     */
    private List<OutboundOrderVo.OutboundDetailVo> getOrderDetails(Long orderId) {
        List<WmsOutboundDetail> details = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>()
                        .eq(WmsOutboundDetail::getOrderId, orderId));
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }

    /**
     * WmsOutboundOrder实体转OutboundOrderVo(填充库房名称)
     */
    private OutboundOrderVo toOrderVo(WmsOutboundOrder order) {
        OutboundOrderVo vo = new OutboundOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setWarehouseId(order.getWarehouseId());
        vo.setOrderType(order.getOrderType());
        vo.setStatus(order.getStatus());
        vo.setReceiver(order.getReceiver());
        vo.setPurpose(order.getPurpose());
        vo.setExpectedReturnDate(order.getExpectedReturnDate());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        // 填充库房名称
        if (order.getWarehouseId() != null) {
            WmsWarehouse warehouse = wmsWarehouseMapper.selectById(order.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        return vo;
    }

    /**
     * WmsOutboundDetail实体转OutboundDetailVo(填充物品名称、库位编码)
     */
    private OutboundOrderVo.OutboundDetailVo toDetailVo(WmsOutboundDetail detail) {
        OutboundOrderVo.OutboundDetailVo vo = new OutboundOrderVo.OutboundDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        vo.setBinId(detail.getBinId());
        // 填充物品信息
        if (detail.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(detail.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        // 填充库位编码
        if (detail.getBinId() != null) {
            WmsBin bin = wmsBinMapper.selectById(detail.getBinId());
            if (bin != null) {
                vo.setBinCode(bin.getBinCode());
            }
        }
        return vo;
    }
}
