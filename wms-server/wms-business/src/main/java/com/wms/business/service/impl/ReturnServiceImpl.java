package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.converter.ReturnOrderConverter;
import com.wms.business.domain.dto.ReturnOrderDto;
import com.wms.business.domain.entity.WmsOutboundOrder;
import com.wms.business.domain.entity.WmsReturnDetail;
import com.wms.business.domain.entity.WmsReturnOrder;
import com.wms.business.domain.vo.ReturnOrderVo;
import com.wms.business.mapper.WmsOutboundOrderMapper;
import com.wms.business.mapper.WmsReturnDetailMapper;
import com.wms.business.mapper.WmsReturnOrderMapper;
import com.wms.business.service.ReturnService;
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
import com.wms.item.service.ItemService;
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
 * 归还单服务实现类
 * 处理归还单的创建、更新、删除、提交、异常登记等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final WmsReturnOrderMapper wmsReturnOrderMapper;
    private final WmsReturnDetailMapper wmsReturnDetailMapper;
    private final WmsOutboundOrderMapper wmsOutboundOrderMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ItemService itemService;
    private final BinWarehouseValidator binWarehouseValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final SequenceGenerator sequenceGenerator;
    private final ReturnOrderConverter returnOrderConverter;

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
        if (status != null) {
            wrapper.eq(WmsReturnOrder::getStatus, status);
        }
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsReturnOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsReturnOrder::getCreateTime);

        Page<WmsReturnOrder> page = wmsReturnOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<ReturnOrderVo> result = new PageResult<>();
        List<WmsReturnOrder> orders = page.getRecords();
        // 批量查询出库单避免N+1
        Set<Long> outboundOrderIds = orders.stream()
                .map(WmsReturnOrder::getOutboundOrderId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, WmsOutboundOrder> outboundOrderMap = outboundOrderIds.isEmpty() ? Map.of()
                : wmsOutboundOrderMapper.selectBatchIds(outboundOrderIds).stream()
                        .collect(Collectors.toMap(WmsOutboundOrder::getId, Function.identity()));
        result.setRecords(orders.stream()
                .map(order -> returnOrderConverter.toVo(order, outboundOrderMap))
                .collect(Collectors.toList()));
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
        if (order == null) {
            throw new BizException("归还单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("归还单已删除");
        }
        ReturnOrderVo vo = returnOrderConverter.toVo(order, Map.of());
        List<WmsReturnDetail> details = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, id));
        vo.setDetails(returnOrderConverter.toDetailVoList(details, buildItemMap(details)));
        return vo;
    }

    /**
     * 创建归还单
     * 校验关联出库单，生成归还单号，保存归还单及明细
     * 支持异常归还登记：明细中conditionStatus可标记损坏/丢失/数量不符
     *
     * @param dto 归还单创建参数，包含出库单ID、明细列表
     * @return 创建后的归还单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrderVo createOrder(ReturnOrderDto dto) {
        // 校验关联出库单存在且未删除
        WmsOutboundOrder outboundOrder = wmsOutboundOrderMapper.selectById(dto.getOutboundOrderId());
        if (outboundOrder == null) {
            throw new BizException("关联出库单不存在");
        }
        if (outboundOrder.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("关联出库单已删除");
        }

        WmsReturnOrder order = new WmsReturnOrder();
        // 生成归还单号：格式为GH + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setOutboundOrderId(dto.getOutboundOrderId());
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        order.setReceiver(dto.getReceiver());
        order.setRemark(dto.getRemark());

        wmsReturnOrderMapper.insert(order);
        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(ReturnOrderDto.ReturnDetailDto::getBinId)
                .collect(Collectors.toList()), outboundOrder.getWarehouseId(), "归还库位不属于关联出库单库房");
        // 保存归还明细(含异常登记信息)
        List<WmsReturnDetail> detailList = buildDetails(order.getId(), dto.getDetails());
        if (!detailList.isEmpty()) {
            Db.saveBatch(detailList);
            appendDefaultBins(detailList);
        }

        // 归还单创建时不直接发布库存同步事件，需走审批流程后在审批通过时发布

        ReturnOrderVo vo = returnOrderConverter.toVo(order, Map.of());
        List<WmsReturnDetail> savedDetails = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, order.getId()));
        vo.setDetails(returnOrderConverter.toDetailVoList(savedDetails, buildItemMap(savedDetails)));
        return vo;
    }

    /**
     * 更新归还单
     * 仅草稿状态可更新，逻辑删除原有明细后重新保存
     *
     * @param id 归还单ID
     * @param dto 归还单更新参数
     * @return 更新后的归还单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnOrderVo updateOrder(Long id, ReturnOrderDto dto) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("归还单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("归还单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的归还单可以更新");
        }

        // 校验关联出库单存在
        WmsOutboundOrder outboundOrder = wmsOutboundOrderMapper.selectById(dto.getOutboundOrderId());
        if (outboundOrder == null) {
            throw new BizException("关联出库单不存在");
        }
        if (outboundOrder.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("关联出库单已删除");
        }

        order.setOutboundOrderId(dto.getOutboundOrderId());
        order.setReceiver(dto.getReceiver());
        order.setRemark(dto.getRemark());
        wmsReturnOrderMapper.updateById(order);

        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(ReturnOrderDto.ReturnDetailDto::getBinId)
                .collect(Collectors.toList()), outboundOrder.getWarehouseId(), "归还库位不属于关联出库单库房");

        // 逻辑删除原有明细
        List<WmsReturnDetail> oldDetails = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>().eq(WmsReturnDetail::getOrderId, id));
        List<WmsReturnDetail> updateDetails = new ArrayList<>();
        for (WmsReturnDetail oldDetail : oldDetails) {
            WmsReturnDetail updateDetail = new WmsReturnDetail();
            updateDetail.setId(oldDetail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetails.add(updateDetail);
        }
        if (!updateDetails.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(wmsReturnDetailMapper, WmsReturnDetail.class, updateDetails);
        }

        // 保存新明细(含异常登记信息)
        List<WmsReturnDetail> newDetailList = buildDetails(id, dto.getDetails());
        if (!newDetailList.isEmpty()) {
            Db.saveBatch(newDetailList);
            appendDefaultBins(newDetailList);
        }

        ReturnOrderVo vo = returnOrderConverter.toVo(order, Map.of());
        List<WmsReturnDetail> savedDetails = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, id));
        vo.setDetails(returnOrderConverter.toDetailVoList(savedDetails, buildItemMap(savedDetails)));
        return vo;
    }

    /**
     * 删除归还单
     * 仅草稿状态可删除，逻辑删除归还单及明细
     *
     * @param id 归还单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("归还单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("归还单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的归还单可以删除");
        }
        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(wmsReturnOrderMapper, WmsReturnOrder.class, id);

        // 逻辑删除明细
        List<WmsReturnDetail> details = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, id));
        List<WmsReturnDetail> updateDetailList = new ArrayList<>();
        for (WmsReturnDetail detail : details) {
            WmsReturnDetail updateDetail = new WmsReturnDetail();
            updateDetail.setId(detail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetailList.add(updateDetail);
        }
        if (!updateDetailList.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(wmsReturnDetailMapper, WmsReturnDetail.class, updateDetailList);
        }
    }

    /**
     * 提交归还单
     * 仅草稿状态可提交，提交后状态变为待审批并发起审批请求，库存同步延迟到审批通过后
     *
     * @param id 归还单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsReturnOrder order = wmsReturnOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("归还单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("归还单已删除");
        }
        // 只有草稿状态可以提交
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("只有草稿状态的归还单可以提交");
        }
        // 校验明细中异常归还必须有异常说明
        List<WmsReturnDetail> details = wmsReturnDetailMapper.selectList(
                new LambdaQueryWrapper<WmsReturnDetail>()
                        .eq(WmsReturnDetail::getOrderId, id));
        for (WmsReturnDetail detail : details) {
            if (detail.getConditionStatus() != null
                    && detail.getConditionStatus() != OrderConstants.RETURN_CONDITION_NORMAL
                    && (detail.getAbnormalRemark() == null || detail.getAbnormalRemark().isBlank())) {
                throw new BizException("异常归还明细必须填写异常说明: 物品ID=" + detail.getItemId());
            }
        }
        // 提交归还单，状态改为待审批
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        wmsReturnOrderMapper.updateById(order);

        // 发起审批请求，库存同步延迟到审批通过后
        eventPublisher.publishEvent(new ApprovalRequestEvent(id, BizTypeEnum.RETURN.getCode()));
    }

    /**
     * 构建归还明细列表
     * 校验物品存在，设置默认状态和异常登记字段
     *
     * @param orderId 归还单ID
     * @param detailDtos 明细DTO列表
     * @return 归还明细实体列表
     */
    private List<WmsReturnDetail> buildDetails(Long orderId, List<ReturnOrderDto.ReturnDetailDto> detailDtos) {
        List<WmsReturnDetail> detailList = new ArrayList<>();
        for (ReturnOrderDto.ReturnDetailDto detailDto : detailDtos) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + detailDto.getItemId());
            }
            WmsReturnDetail detail = new WmsReturnDetail();
            detail.setOrderId(orderId);
            detail.setItemId(detailDto.getItemId());
            detail.setLabelId(detailDto.getLabelId());
            detail.setBinId(detailDto.getBinId());
            detail.setQuantity(detailDto.getQuantity());
            // 默认物品状态为正常
            detail.setConditionStatus(detailDto.getConditionStatus() != null
                    ? detailDto.getConditionStatus() : OrderConstants.RETURN_CONDITION_NORMAL);
            detail.setAbnormalRemark(detailDto.getAbnormalRemark());
            detail.setActualQuantity(detailDto.getActualQuantity());
            detailList.add(detail);
        }
        return detailList;
    }

    /**
     * 批量查询物品构建Map，避免N+1查询
     *
     * @param details 归还明细列表
     * @return 物品ID到实体的映射
     */
    private Map<Long, WmsItem> buildItemMap(List<WmsReturnDetail> details) {
        Set<Long> itemIds = details.stream()
                .map(WmsReturnDetail::getItemId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        return itemIds.isEmpty() ? Map.of()
                : wmsItemMapper.selectBatchIds(itemIds).stream()
                        .collect(Collectors.toMap(WmsItem::getId, Function.identity()));
    }

    /**
     * 生成归还单号: GH + 年月日 + 4位流水号
     * 使用Redis INCR原子操作保证并发安全
     */
    private String generateOrderNo() {
        return sequenceGenerator.next(OrderConstants.RETURN_NO_PREFIX);
    }

    /**
     * 归还保存成功后把有效入库库位追加到物品默认库位。
     *
     * @param details 归还明细列表
     */
    private void appendDefaultBins(List<WmsReturnDetail> details) {
        details.stream()
                .filter(this::needsInbound)
                .filter(detail -> detail.getItemId() != null && detail.getBinId() != null)
                .collect(Collectors.groupingBy(WmsReturnDetail::getItemId,
                        Collectors.mapping(WmsReturnDetail::getBinId, Collectors.toList())))
                .forEach((itemId, binIds) -> itemService.appendDefaultBins(itemId, binIds));
    }

    /**
     * 判断归还明细是否会形成有效入库。
     *
     * @param detail 归还明细
     * @return 是否需要入库
     */
    private boolean needsInbound(WmsReturnDetail detail) {
        Integer conditionStatus = detail.getConditionStatus();
        if (conditionStatus != null
                && (conditionStatus == OrderConstants.RETURN_CONDITION_DAMAGED
                || conditionStatus == OrderConstants.RETURN_CONDITION_LOST)) {
            return false;
        }
        if (conditionStatus != null && conditionStatus == OrderConstants.RETURN_CONDITION_MISMATCH) {
            return detail.getActualQuantity() != null && detail.getActualQuantity() > 0;
        }
        return detail.getQuantity() != null && detail.getQuantity() > 0;
    }
}
