package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.business.domain.dto.TransferOrderDto;
import com.wms.business.domain.entity.WmsTransferDetail;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.domain.vo.TransferOrderVo;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.business.service.TransferService;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.domain.entity.WmsWarehouse;
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
 * 调拨单服务实现类
 * 处理调拨单的创建、提交等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final WmsTransferOrderMapper wmsTransferOrderMapper;
    private final WmsTransferDetailMapper wmsTransferDetailMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ApplicationEventPublisher eventPublisher;

    /** 调拨单号前缀 */
    private static final String ORDER_NO_PREFIX = "DB";

    /** 草稿状态 */
    private static final int STATUS_DRAFT = 0;

    /** 待审核状态 */
    private static final int STATUS_PENDING = 1;

    /** 已完成状态 */
    private static final int STATUS_COMPLETED = 5;

    /**
     * 分页查询调拨单
     *
     * @param pageParam 分页参数
     * @param status 单据状态
     * @param orderNo 单号(模糊搜索)
     * @return 调拨单分页结果
     */
    @Override
    public PageResult<TransferOrderVo> pageOrders(PageParam pageParam, Integer status, String orderNo) {
        LambdaQueryWrapper<WmsTransferOrder> wrapper = new LambdaQueryWrapper<WmsTransferOrder>();
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsTransferOrder::getStatus, status);
        }
        // 按单号模糊搜索
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsTransferOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsTransferOrder::getCreateTime);

        Page<WmsTransferOrder> page = wmsTransferOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<TransferOrderVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toOrderVo).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询调拨单详情
     *
     * @param id 调拨单ID
     * @return 调拨单VO(含明细列表)
     */
    @Override
    public TransferOrderVo getOrderById(Long id) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("调拨单不存在");
        }
        TransferOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 创建调拨单
     * 校验调出/调入库房，生成调拨单号，保存调拨单及明细
     *
     * @param dto 调拨单创建参数，包含调出库房ID、调入库房ID、明细列表
     * @return 创建后的调拨单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferOrderVo createOrder(TransferOrderDto dto) {
        // 校验调出库房存在
        WmsWarehouse fromWarehouse = wmsWarehouseMapper.selectById(dto.getFromWarehouseId());
        if (fromWarehouse == null || fromWarehouse.getDelFlag() == 1) {
            throw new BizException("调出库房不存在或已禁用");
        }
        // 校验调入库房存在
        WmsWarehouse toWarehouse = wmsWarehouseMapper.selectById(dto.getToWarehouseId());
        if (toWarehouse == null || toWarehouse.getDelFlag() == 1) {
            throw new BizException("调入库房不存在或已禁用");
        }
        // 调出库房和调入库房不能相同
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            throw new BizException("调出库房和调入库房不能相同");
        }

        WmsTransferOrder order = new WmsTransferOrder();
        // 生成调拨单号：格式为DB + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setFromWarehouseId(dto.getFromWarehouseId());
        order.setToWarehouseId(dto.getToWarehouseId());
        order.setStatus(STATUS_DRAFT);
        order.setRemark(dto.getRemark());

        wmsTransferOrderMapper.insert(order);
        // 保存调拨明细
        for (TransferOrderDto.TransferDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null || item.getDelFlag() == 1) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            WmsTransferDetail detail = new WmsTransferDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            wmsTransferDetailMapper.insert(detail);
        }

        TransferOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(order.getId()));
        return vo;
    }

    /**
     * 提交调拨单
     * 仅草稿状态可提交，提交后标记为已完成并发布库存同步事件(调出库房出库、调入库房入库)
     *
     * @param id 调拨单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(id);
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("调拨单不存在");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的调拨单可以提交");
        }

        // 状态变为待审核
        order.setStatus(STATUS_PENDING);
        wmsTransferOrderMapper.updateById(order);

        // 直接标记为已完成并发布库存同步事件(简化流程)
        order.setStatus(STATUS_COMPLETED);
        wmsTransferOrderMapper.updateById(order);

        // 调拨完成后：调出库房出库(负数)，调入库房入库(正数)
        List<WmsTransferDetail> details = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, id));
        for (WmsTransferDetail detail : details) {
            // 调出库房出库
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getFromWarehouseId(), null,
                    -detail.getQuantity(), "OUT"));
            // 调入库房入库
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getToWarehouseId(), null,
                    detail.getQuantity(), "IN"));
        }
    }

    /**
     * 生成调拨单号: DB + 年月日 + 4位流水号
     * 示例: DB202605140001
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<WmsTransferOrder> wrapper = new LambdaQueryWrapper<WmsTransferOrder>()
                .likeRight(WmsTransferOrder::getOrderNo, ORDER_NO_PREFIX + datePart)
                .orderByDesc(WmsTransferOrder::getOrderNo)
                .last("LIMIT 1");
        WmsTransferOrder lastOrder = wmsTransferOrderMapper.selectOne(wrapper);
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
     * 获取调拨单明细列表
     *
     * @param orderId 调拨单ID
     * @return 调拨明细VO列表
     */
    private List<TransferOrderVo.TransferDetailVo> getOrderDetails(Long orderId) {
        List<WmsTransferDetail> details = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, orderId));
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }

    /**
     * WmsTransferOrder实体转TransferOrderVo(填充库房名称)
     */
    private TransferOrderVo toOrderVo(WmsTransferOrder order) {
        TransferOrderVo vo = new TransferOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setFromWarehouseId(order.getFromWarehouseId());
        vo.setToWarehouseId(order.getToWarehouseId());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setCreateBy(order.getCreateBy());
        // 填充调出库房名称
        if (order.getFromWarehouseId() != null) {
            WmsWarehouse fromWarehouse = wmsWarehouseMapper.selectById(order.getFromWarehouseId());
            if (fromWarehouse != null) {
                vo.setFromWarehouseName(fromWarehouse.getWarehouseName());
            }
        }
        // 填充调入库房名称
        if (order.getToWarehouseId() != null) {
            WmsWarehouse toWarehouse = wmsWarehouseMapper.selectById(order.getToWarehouseId());
            if (toWarehouse != null) {
                vo.setToWarehouseName(toWarehouse.getWarehouseName());
            }
        }
        return vo;
    }

    /**
     * WmsTransferDetail实体转TransferDetailVo(填充物品名称)
     */
    private TransferOrderVo.TransferDetailVo toDetailVo(WmsTransferDetail detail) {
        TransferOrderVo.TransferDetailVo vo = new TransferOrderVo.TransferDetailVo();
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
