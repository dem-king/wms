package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.domain.dto.OutboundOrderDto;
import com.wms.business.domain.entity.WmsOutboundDetail;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.vo.OutboundOrderVo;
import com.wms.business.converter.OutboundOrderConverter;
import com.wms.business.mapper.WmsOutboundDetailMapper;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.service.OutboundService;
import com.wms.business.service.support.BinWarehouseValidator;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.enums.BizTypeEnum;
import com.wms.common.enums.OrderStatusEnum;
import com.wms.common.event.ApprovalRequestEvent;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.business.domain.constant.OrderConstants;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
    private final BinWarehouseValidator binWarehouseValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final SequenceGenerator sequenceGenerator;
    private final OutboundOrderConverter outboundOrderConverter;



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
        List<WmsOutboundOrder> orders = page.getRecords();
        // 批量查询库房构建Map，避免N+1查询
        Set<Long> warehouseIds = orders.stream()
                .map(WmsOutboundOrder::getWarehouseId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty() ? Map.of()
                : wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouse::getId, Function.identity()));
        result.setRecords(orders.stream().map(order -> outboundOrderConverter.toOrderVo(order, warehouseMap)).collect(Collectors.toList()));
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
        if (order == null) {
            throw new BizException("出库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("出库单已删除");
        }
        OutboundOrderVo vo = outboundOrderConverter.toOrderVo(order, Map.of());
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
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }

        WmsOutboundOrder order = new WmsOutboundOrder();
        // 生成出库单号：格式为CK + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setWarehouseId(dto.getWarehouseId());
        order.setOrderType(dto.getOrderType());
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        order.setReceiver(dto.getReceiver());
        order.setPurpose(dto.getPurpose());
        order.setExpectedReturnDate(dto.getExpectedReturnDate());
        order.setRemark(dto.getRemark());

        wmsOutboundOrderMapper.insert(order);
        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(OutboundOrderDto.OutboundDetailDto::getBinId)
                .collect(Collectors.toList()), dto.getWarehouseId(), "出库明细库位不属于单据库房");
        // 保存出库明细
        List<WmsOutboundDetail> detailList = new ArrayList<>();
        for (OutboundOrderDto.OutboundDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + detailDto.getItemId());
            }
            WmsOutboundDetail detail = new WmsOutboundDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setBinId(detailDto.getBinId());
            detailList.add(detail);
        }
        if (!detailList.isEmpty()) {
            Db.saveBatch(detailList);
        }

        OutboundOrderVo vo = outboundOrderConverter.toOrderVo(order, Map.of());
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
        if (order == null) {
            throw new BizException("出库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("出库单已删除");
        }
        // 仅草稿状态可更新
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的出库单可以更新");
        }

        // 校验库房
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }

        order.setWarehouseId(dto.getWarehouseId());
        order.setOrderType(dto.getOrderType());
        order.setReceiver(dto.getReceiver());
        order.setPurpose(dto.getPurpose());
        order.setExpectedReturnDate(dto.getExpectedReturnDate());
        order.setRemark(dto.getRemark());
        wmsOutboundOrderMapper.updateById(order);

        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(OutboundOrderDto.OutboundDetailDto::getBinId)
                .collect(Collectors.toList()), dto.getWarehouseId(), "出库明细库位不属于单据库房");

        // 逻辑删除原有明细后重新保存
        List<WmsOutboundDetail> oldDetails = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>().eq(WmsOutboundDetail::getOrderId, id));
        List<WmsOutboundDetail> updateDetails = new ArrayList<>();
        for (WmsOutboundDetail oldDetail : oldDetails) {
            WmsOutboundDetail updateDetail = new WmsOutboundDetail();
            updateDetail.setId(oldDetail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            
            updateDetails.add(updateDetail);
        }
        if (!updateDetails.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(wmsOutboundDetailMapper, WmsOutboundDetail.class, updateDetails);
        }
        List<WmsOutboundDetail> newDetailList = new ArrayList<>();
        for (OutboundOrderDto.OutboundDetailDto detailDto : dto.getDetails()) {
            WmsOutboundDetail detail = new WmsOutboundDetail();
            detail.setOrderId(id);
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setBinId(detailDto.getBinId());
            newDetailList.add(detail);
        }
        if (!newDetailList.isEmpty()) {
            Db.saveBatch(newDetailList);
        }

        OutboundOrderVo vo = outboundOrderConverter.toOrderVo(order, Map.of());
        vo.setDetails(getOrderDetails(id));
        return vo;
    }

    /**
     * 提交出库单
     * 仅草稿状态可提交，提交后进入待审批并发起审批请求
     *
     * @param id 出库单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsOutboundOrder order = wmsOutboundOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("出库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("出库单已删除");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的出库单可以提交");
        }

        // 状态变为待审核
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        wmsOutboundOrderMapper.updateById(order);
        eventPublisher.publishEvent(new ApprovalRequestEvent(id, BizTypeEnum.OUTBOUND.getCode()));
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
        if (order == null) {
            throw new BizException("出库单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("出库单已删除");
        }
        // 仅草稿状态可删除
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的出库单可以删除");
        }
        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(wmsOutboundOrderMapper, WmsOutboundOrder.class, id);
        // 逻辑删除出库明细
        List<WmsOutboundDetail> details = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>()
                        .eq(WmsOutboundDetail::getOrderId, id));
        List<WmsOutboundDetail> updateDetailList = new ArrayList<>();
        for (WmsOutboundDetail detail : details) {
            WmsOutboundDetail updateDetail = new WmsOutboundDetail();
            updateDetail.setId(detail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            
            updateDetailList.add(updateDetail);
        }
        if (!updateDetailList.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(wmsOutboundDetailMapper, WmsOutboundDetail.class, updateDetailList);
        }
    }

    /**
     * 生成出库单号: CK + 年月日 + 4位流水号
     * 使用Redis INCR原子操作保证并发安全
     * 示例: CK202605180001
     */
    private String generateOrderNo() {
        return sequenceGenerator.next(OrderConstants.OUTBOUND_NO_PREFIX);
    }

    /**
     * 获取出库单明细列表
     * 批量查询物品和库位信息，避免N+1查询
     *
     * @param orderId 出库单ID
     * @return 出库明细VO列表
     */
    private List<OutboundOrderVo.OutboundDetailVo> getOrderDetails(Long orderId) {
        List<WmsOutboundDetail> details = wmsOutboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsOutboundDetail>()
                        .eq(WmsOutboundDetail::getOrderId, orderId));
        if (details.isEmpty()) {
            return List.of();
        }
        // 批量查询物品信息构建Map，避免N+1查询
        Set<Long> itemIds = details.stream()
                .map(WmsOutboundDetail::getItemId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, WmsItem> itemMap = itemIds.isEmpty() ? Map.of()
                : wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, Function.identity()));
        // 批量查询库位信息构建Map，避免N+1查询
        Set<Long> binIds = details.stream()
                .map(WmsOutboundDetail::getBinId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, WmsBin> binMap = binIds.isEmpty() ? Map.of()
                : wmsBinMapper.selectBatchIds(binIds).stream()
                        .collect(Collectors.toMap(WmsBin::getId, Function.identity()));
        return details.stream().map(detail -> outboundOrderConverter.toDetailVo(detail, itemMap, binMap)).collect(Collectors.toList());
    }

}
