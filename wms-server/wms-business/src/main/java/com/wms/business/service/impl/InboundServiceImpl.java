package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.converter.InboundOrderConverter;
import com.wms.business.domain.constant.OrderConstants;
import com.wms.business.domain.dto.InboundOrderDto;
import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.vo.InboundOrderVo;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.service.InboundService;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.system.domain.entity.SysSupplier;
import com.wms.system.mapper.SysSupplierMapper;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 入库单服务实现类
 * 处理入库单的创建、审核、完成等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {

    private final WmsInboundOrderMapper wmsInboundOrderMapper;
    private final WmsInboundDetailMapper wmsInboundDetailMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SysSupplierMapper sysSupplierMapper;
    private final WmsItemMapper wmsItemMapper;
    private final WmsBinMapper wmsBinMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SequenceGenerator sequenceGenerator;
    private final InboundOrderConverter inboundOrderConverter;

    /**
     * 分页查询入库单
     *
     * @param pageParam 分页参数
     * @param warehouseId 库房ID
     * @param orderType 入库类型
     * @param status 单据状态
     * @param orderNo 单号(模糊搜索)
     * @return 入库单分页结果
     */
    @Override
    public PageResult<InboundOrderVo> pageOrders(PageParam pageParam, Long warehouseId,
                                                  Integer orderType, Integer status, String orderNo) {
        LambdaQueryWrapper<WmsInboundOrder> wrapper = new LambdaQueryWrapper<WmsInboundOrder>();
        if (warehouseId != null) {
            wrapper.eq(WmsInboundOrder::getWarehouseId, warehouseId);
        }
        if (orderType != null) {
            wrapper.eq(WmsInboundOrder::getOrderType, orderType);
        }
        if (status != null) {
            wrapper.eq(WmsInboundOrder::getStatus, status);
        }
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsInboundOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsInboundOrder::getCreateTime);

        Page<WmsInboundOrder> page = wmsInboundOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<InboundOrderVo> result = new PageResult<>();
        List<WmsInboundOrder> orders = page.getRecords();
        Set<Long> warehouseIds = orders.stream()
                .map(WmsInboundOrder::getWarehouseId).filter(id -> id != null).collect(Collectors.toSet());
        Set<Long> supplierIds = orders.stream()
                .map(WmsInboundOrder::getSupplierId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty() ? Map.of()
                : wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouse::getId, Function.identity()));
        Map<Long, SysSupplier> supplierMap = supplierIds.isEmpty() ? Map.of()
                : sysSupplierMapper.selectBatchIds(supplierIds).stream()
                        .collect(Collectors.toMap(SysSupplier::getId, Function.identity()));
        result.setRecords(orders.stream()
                .map(order -> inboundOrderConverter.toVo(order, warehouseMap, supplierMap))
                .collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询入库单详情
     *
     * @param id 入库单ID
     * @return 入库单VO(含明细列表)
     */
    @Override
    public InboundOrderVo getOrderById(Long id) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("入库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("入库单已删除");
        }
        InboundOrderVo vo = inboundOrderConverter.toVo(order, Map.of(), Map.of());
        vo.setDetails(inboundOrderConverter.toDetailVoList(
                wmsInboundDetailMapper.selectList(
                        new LambdaQueryWrapper<WmsInboundDetail>()
                                .eq(WmsInboundDetail::getOrderId, id))));
        return vo;
    }

    /**
     * 创建入库单
     * 校验库房和供应商，生成入库单号，保存入库单及明细
     *
     * @param dto 入库单创建参数，包含库房ID、供应商ID、明细列表
     * @return 创建后的入库单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundOrderVo createOrder(InboundOrderDto dto) {
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }
        if (dto.getSupplierId() != null) {
            SysSupplier supplier = sysSupplierMapper.selectById(dto.getSupplierId());
            if (supplier == null) {
                throw new BizException("供应商不存在");
            }
            if (supplier.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("供应商已删除");
            }
        }

        WmsInboundOrder order = new WmsInboundOrder();
        order.setOrderNo(generateOrderNo());
        order.setWarehouseId(dto.getWarehouseId());
        order.setSupplierId(dto.getSupplierId());
        order.setOrderType(dto.getOrderType());
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        order.setRemark(dto.getRemark());

        BigDecimal totalAmount = BigDecimal.ZERO;
        wmsInboundOrderMapper.insert(order);
        List<WmsInboundDetail> detailList = new ArrayList<>();
        for (InboundOrderDto.InboundDetailDto detailDto : dto.getDetails()) {
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + detailDto.getItemId());
            }
            WmsInboundDetail detail = new WmsInboundDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            BigDecimal amount = BigDecimal.ZERO;
            if (detailDto.getUnitPrice() != null && detailDto.getQuantity() != null) {
                amount = detailDto.getUnitPrice().multiply(BigDecimal.valueOf(detailDto.getQuantity()));
            }
            detail.setAmount(amount);
            detail.setBinId(detailDto.getBinId());
            detailList.add(detail);
            totalAmount = totalAmount.add(amount);
        }
        if (!detailList.isEmpty()) {
            Db.saveBatch(detailList);
        }
        order.setTotalAmount(totalAmount);
        wmsInboundOrderMapper.updateById(order);

        InboundOrderVo vo = inboundOrderConverter.toVo(order, Map.of(), Map.of());
        vo.setDetails(inboundOrderConverter.toDetailVoList(
                wmsInboundDetailMapper.selectList(
                        new LambdaQueryWrapper<WmsInboundDetail>()
                                .eq(WmsInboundDetail::getOrderId, order.getId()))));
        return vo;
    }

    /**
     * 更新入库单
     * 仅草稿状态可更新，逻辑删除原有明细后重新保存
     *
     * @param id 入库单ID
     * @param dto 入库单更新参数
     * @return 更新后的入库单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundOrderVo updateOrder(Long id, InboundOrderDto dto) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("入库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("入库单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的入库单可以更新");
        }

        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }

        order.setWarehouseId(dto.getWarehouseId());
        order.setSupplierId(dto.getSupplierId());
        order.setOrderType(dto.getOrderType());
        order.setRemark(dto.getRemark());
        wmsInboundOrderMapper.updateById(order);

        List<WmsInboundDetail> oldDetails = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>().eq(WmsInboundDetail::getOrderId, id));
        List<WmsInboundDetail> updateDetails = new ArrayList<>();
        for (WmsInboundDetail oldDetail : oldDetails) {
            WmsInboundDetail updateDetail = new WmsInboundDetail();
            updateDetail.setId(oldDetail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetails.add(updateDetail);
        }
        if (!updateDetails.isEmpty()) {
            Db.updateBatchById(updateDetails);
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<WmsInboundDetail> newDetailList = new ArrayList<>();
        for (InboundOrderDto.InboundDetailDto detailDto : dto.getDetails()) {
            WmsInboundDetail detail = new WmsInboundDetail();
            detail.setOrderId(id);
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            BigDecimal amount = BigDecimal.ZERO;
            if (detailDto.getUnitPrice() != null && detailDto.getQuantity() != null) {
                amount = detailDto.getUnitPrice().multiply(BigDecimal.valueOf(detailDto.getQuantity()));
            }
            detail.setAmount(amount);
            detail.setBinId(detailDto.getBinId());
            newDetailList.add(detail);
            totalAmount = totalAmount.add(amount);
        }
        if (!newDetailList.isEmpty()) {
            Db.saveBatch(newDetailList);
        }
        order.setTotalAmount(totalAmount);
        wmsInboundOrderMapper.updateById(order);

        InboundOrderVo vo = inboundOrderConverter.toVo(order, Map.of(), Map.of());
        vo.setDetails(inboundOrderConverter.toDetailVoList(
                wmsInboundDetailMapper.selectList(
                        new LambdaQueryWrapper<WmsInboundDetail>()
                                .eq(WmsInboundDetail::getOrderId, id))));
        return vo;
    }

    /**
     * 提交入库单
     * 仅草稿状态可提交，提交后标记为已完成并发布库存同步事件
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("入库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("入库单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的入库单可以提交");
        }

        order.setStatus(OrderStatusEnum.PENDING.getCode());
        wmsInboundOrderMapper.updateById(order);

        order.setStatus(OrderStatusEnum.APPROVED.getCode());
        wmsInboundOrderMapper.updateById(order);

        List<WmsInboundDetail> details = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>()
                        .eq(WmsInboundDetail::getOrderId, id));
        for (WmsInboundDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), detail.getBinId(),
                    detail.getQuantity(), BizConstants.STOCK_SYNC_IN));
        }
    }

    /**
     * 删除入库单
     * 仅草稿状态可删除，逻辑删除入库单及明细
     *
     * @param id 入库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        WmsInboundOrder order = wmsInboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("入库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("入库单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的入库单可以删除");
        }
        WmsInboundOrder updateEntity = new WmsInboundOrder();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        wmsInboundOrderMapper.updateById(updateEntity);

        List<WmsInboundDetail> details = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>()
                        .eq(WmsInboundDetail::getOrderId, id));
        List<WmsInboundDetail> updateDetailList = new ArrayList<>();
        for (WmsInboundDetail detail : details) {
            WmsInboundDetail updateDetail = new WmsInboundDetail();
            updateDetail.setId(detail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetailList.add(updateDetail);
        }
        if (!updateDetailList.isEmpty()) {
            Db.updateBatchById(updateDetailList);
        }
    }

    /**
     * 生成入库单号: RK + 年月日 + 4位流水号
     * 使用Redis INCR原子操作保证并发安全
     */
    private String generateOrderNo() {
        return sequenceGenerator.next(OrderConstants.INBOUND_NO_PREFIX);
    }
}