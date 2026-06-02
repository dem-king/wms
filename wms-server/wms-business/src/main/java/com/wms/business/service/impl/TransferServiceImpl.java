package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.converter.TransferOrderConverter;
import com.wms.business.domain.dto.TransferOrderDto;
import com.wms.business.domain.entity.WmsTransferDetail;
import com.wms.business.domain.entity.WmsTransferOrder;
import com.wms.business.domain.vo.TransferOrderVo;
import com.wms.business.mapper.WmsTransferDetailMapper;
import com.wms.business.mapper.WmsTransferOrderMapper;
import com.wms.business.service.TransferService;
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
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 调拨单服务实现类
 * 处理调拨单的创建、提交、更新、删除等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final WmsTransferOrderMapper wmsTransferOrderMapper;
    private final WmsTransferDetailMapper wmsTransferDetailMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ItemService itemService;
    private final BinWarehouseValidator binWarehouseValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final SequenceGenerator sequenceGenerator;
    private final TransferOrderConverter transferOrderConverter;

    /**
     * 分页查询调拨单
     * 批量查询关联库房构建Map，避免N+1查询
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

        List<WmsTransferOrder> records = page.getRecords();
        // 收集所有库房ID，批量查询构建Map避免N+1
        Set<Long> warehouseIds = new HashSet<>();
        for (WmsTransferOrder order : records) {
            if (order.getFromWarehouseId() != null) {
                warehouseIds.add(order.getFromWarehouseId());
            }
            if (order.getToWarehouseId() != null) {
                warehouseIds.add(order.getToWarehouseId());
            }
        }
        Map<Long, WmsWarehouse> warehouseMap = Collections.emptyMap();
        if (!warehouseIds.isEmpty()) {
            warehouseMap = wmsWarehouseMapper.selectBatchIds(warehouseIds)
                    .stream().collect(Collectors.toMap(WmsWarehouse::getId, w -> w));
        }

        PageResult<TransferOrderVo> result = new PageResult<>();
        Map<Long, WmsWarehouse> finalWarehouseMap = warehouseMap;
        result.setRecords(records.stream().map(order -> transferOrderConverter.toVo(order, finalWarehouseMap)).collect(Collectors.toList()));
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
        if (order == null) {
            throw new BizException("调拨单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调拨单已删除");
        }
        TransferOrderVo vo = transferOrderConverter.toVo(order, Map.of());
        // 查询明细并转换为VO列表
        List<WmsTransferDetail> details = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, id));
        vo.setDetails(transferOrderConverter.toDetailVoList(details));
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
        if (fromWarehouse == null) {
            throw new BizException("调出库房不存在");
        }
        if (fromWarehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调出库房已删除");
        }
        // 校验调入库房存在
        WmsWarehouse toWarehouse = wmsWarehouseMapper.selectById(dto.getToWarehouseId());
        if (toWarehouse == null) {
            throw new BizException("调入库房不存在");
        }
        if (toWarehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调入库房已删除");
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
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        order.setRemark(dto.getRemark());

        wmsTransferOrderMapper.insert(order);
        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(TransferOrderDto.TransferDetailDto::getFromBinId)
                .collect(Collectors.toList()), dto.getFromWarehouseId(), "调出库位不属于调出库房");
        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(TransferOrderDto.TransferDetailDto::getToBinId)
                .collect(Collectors.toList()), dto.getToWarehouseId(), "调入库位不属于调入库房");
        // 保存调拨明细
        List<WmsTransferDetail> detailList = saveDetails(order.getId(), dto.getDetails());
        appendDefaultBins(detailList);

        TransferOrderVo vo = transferOrderConverter.toVo(order, Map.of());
        // 查询明细并转换为VO列表
        List<WmsTransferDetail> details = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, order.getId()));
        vo.setDetails(transferOrderConverter.toDetailVoList(details));
        return vo;
    }

    /**
     * 提交调拨单
     * 仅草稿状态可提交，提交后状态变为待审核并发起审批请求，库存同步延迟到审批通过后
     *
     * @param id 调拨单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("调拨单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调拨单已删除");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的调拨单可以提交");
        }

        // 状态变为待审核
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        wmsTransferOrderMapper.updateById(order);

        // 发起审批请求，库存同步延迟到审批通过后
        eventPublisher.publishEvent(new ApprovalRequestEvent(id, BizTypeEnum.TRANSFER.getCode()));
    }

    /**
     * 更新调拨单(仅草稿状态)
     * 校验草稿状态→校验调出/调入库房→更新主表→逻辑删除旧明细→保存新明细
     *
     * @param id 调拨单ID
     * @param dto 调拨单更新参数
     * @return 更新后的调拨单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferOrderVo updateOrder(Long id, TransferOrderDto dto) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("调拨单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调拨单已删除");
        }
        // 仅草稿状态可更新
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的调拨单可以更新");
        }
        // 校验调出库房存在
        WmsWarehouse fromWarehouse = wmsWarehouseMapper.selectById(dto.getFromWarehouseId());
        if (fromWarehouse == null) {
            throw new BizException("调出库房不存在");
        }
        if (fromWarehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调出库房已删除");
        }
        // 校验调入库房存在
        WmsWarehouse toWarehouse = wmsWarehouseMapper.selectById(dto.getToWarehouseId());
        if (toWarehouse == null) {
            throw new BizException("调入库房不存在");
        }
        if (toWarehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调入库房已删除");
        }
        // 调出库房和调入库房不能相同
        if (dto.getFromWarehouseId().equals(dto.getToWarehouseId())) {
            throw new BizException("调出库房和调入库房不能相同");
        }

        // 更新主表
        order.setFromWarehouseId(dto.getFromWarehouseId());
        order.setToWarehouseId(dto.getToWarehouseId());
        order.setRemark(dto.getRemark());
        wmsTransferOrderMapper.updateById(order);

        // 逻辑删除原有明细
        List<WmsTransferDetail> oldDetails = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, id));
        List<WmsTransferDetail> updateDetails = new ArrayList<>();
        for (WmsTransferDetail oldDetail : oldDetails) {
            oldDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetails.add(oldDetail);
        }
        if (!updateDetails.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(wmsTransferDetailMapper, WmsTransferDetail.class, updateDetails);
        }

        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(TransferOrderDto.TransferDetailDto::getFromBinId)
                .collect(Collectors.toList()), dto.getFromWarehouseId(), "调出库位不属于调出库房");
        binWarehouseValidator.validateBelongToWarehouse(dto.getDetails().stream()
                .map(TransferOrderDto.TransferDetailDto::getToBinId)
                .collect(Collectors.toList()), dto.getToWarehouseId(), "调入库位不属于调入库房");
        // 保存新明细
        List<WmsTransferDetail> detailList = saveDetails(id, dto.getDetails());
        appendDefaultBins(detailList);

        TransferOrderVo vo = transferOrderConverter.toVo(order, Map.of());
        // 查询新明细并转换为VO列表
        List<WmsTransferDetail> newDetails = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, id));
        vo.setDetails(transferOrderConverter.toDetailVoList(newDetails));
        return vo;
    }

    /**
     * 删除调拨单(仅草稿状态)
     * 逻辑删除调拨单主表及明细
     *
     * @param id 调拨单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        WmsTransferOrder order = wmsTransferOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("调拨单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("调拨单已删除");
        }
        // 仅草稿状态可删除
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的调拨单可以删除");
        }

        // delFlag 是 @TableLogic 字段，必须显式 SET 才能真正写入删除标记
        LogicDeleteHelper.markDeleted(wmsTransferOrderMapper, WmsTransferOrder.class, id);

        // 逻辑删除明细
        List<WmsTransferDetail> details = wmsTransferDetailMapper.selectList(
                new LambdaQueryWrapper<WmsTransferDetail>()
                        .eq(WmsTransferDetail::getOrderId, id));
        List<WmsTransferDetail> updateDetailList = new ArrayList<>();
        for (WmsTransferDetail detail : details) {
            detail.setDelFlag(DelFlagConstants.DELETED);
            updateDetailList.add(detail);
        }
        if (!updateDetailList.isEmpty()) {
            LogicDeleteHelper.markDeletedEntities(wmsTransferDetailMapper, WmsTransferDetail.class, updateDetailList);
        }
    }

    /**
     * 生成调拨单号: DB + 年月日 + 4位流水号
     * 使用Redis INCR原子操作保证并发安全
     * 示例: DB202605180001
     */
    private String generateOrderNo() {
        return sequenceGenerator.next(OrderConstants.TRANSFER_NO_PREFIX);
    }

    /**
     * 批量保存调拨明细
     * 校验物品存在后逐条保存
     *
     * @param orderId 调拨单ID
     * @param detailDtos 明细DTO列表
     */
    private List<WmsTransferDetail> saveDetails(Long orderId, List<TransferOrderDto.TransferDetailDto> detailDtos) {
        List<WmsTransferDetail> detailList = new ArrayList<>();
        for (TransferOrderDto.TransferDetailDto detailDto : detailDtos) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + detailDto.getItemId());
            }
            WmsTransferDetail detail = new WmsTransferDetail();
            detail.setOrderId(orderId);
            detail.setItemId(detailDto.getItemId());
            detail.setFromBinId(detailDto.getFromBinId());
            detail.setToBinId(detailDto.getToBinId());
            detail.setQuantity(detailDto.getQuantity());
            detailList.add(detail);
        }
        if (!detailList.isEmpty()) {
            Db.saveBatch(detailList);
        }
        return detailList;
    }

    /**
     * 调拨保存成功后把调入库位追加到物品默认库位。
     *
     * @param details 调拨明细列表
     */
    private void appendDefaultBins(List<WmsTransferDetail> details) {
        details.stream()
                .filter(detail -> detail.getItemId() != null && detail.getToBinId() != null)
                .collect(Collectors.groupingBy(WmsTransferDetail::getItemId,
                        Collectors.mapping(WmsTransferDetail::getToBinId, Collectors.toList())))
                .forEach(itemService::appendDefaultBins);
    }
}
