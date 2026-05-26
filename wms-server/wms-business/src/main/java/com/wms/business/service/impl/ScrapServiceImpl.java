package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.business.converter.ScrapOrderConverter;
import com.wms.business.domain.dto.ScrapOrderDto;
import com.wms.business.domain.entity.WmsScrapDetail;
import com.wms.business.domain.entity.WmsScrapOrder;
import com.wms.business.domain.vo.ScrapOrderVo;
import com.wms.business.mapper.WmsScrapDetailMapper;
import com.wms.business.mapper.WmsScrapOrderMapper;
import com.wms.business.service.ScrapService;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
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
import com.wms.warehouse.domain.entity.WmsWarehouse;
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
 * 报废单服务实现类
 * 处理报废单的创建、更新、删除、提交等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final WmsScrapOrderMapper wmsScrapOrderMapper;
    private final WmsScrapDetailMapper wmsScrapDetailMapper;
    private final WmsItemMapper wmsItemMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SequenceGenerator sequenceGenerator;
    private final ScrapOrderConverter scrapOrderConverter;

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
        if (status != null) {
            wrapper.eq(WmsScrapOrder::getStatus, status);
        }
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsScrapOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsScrapOrder::getCreateTime);

        Page<WmsScrapOrder> page = wmsScrapOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<ScrapOrderVo> result = new PageResult<>();
        List<WmsScrapOrder> orders = page.getRecords();
        // 批量查询库房避免N+1
        Set<Long> warehouseIds = orders.stream()
                .map(WmsScrapOrder::getWarehouseId).filter(id -> id != null).collect(Collectors.toSet());
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty() ? Map.of()
                : wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouse::getId, Function.identity()));
        result.setRecords(orders.stream()
                .map(order -> scrapOrderConverter.toVo(order, warehouseMap))
                .collect(Collectors.toList()));
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
        if (order == null) {
            throw new BizException("报废单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("报废单已删除");
        }
        ScrapOrderVo vo = scrapOrderConverter.toVo(order, Map.of());
        vo.setDetails(scrapOrderConverter.toDetailVoList(
                wmsScrapDetailMapper.selectList(
                        new LambdaQueryWrapper<WmsScrapDetail>()
                                .eq(WmsScrapDetail::getOrderId, id))));
        return vo;
    }

    /**
     * 创建报废单
     * 校验库房存在且启用，生成报废单号，保存报废单及明细
     *
     * @param dto 报废单创建参数，包含库房ID、报废原因、明细列表
     * @return 创建后的报废单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScrapOrderVo createOrder(ScrapOrderDto dto) {
        // 校验库房存在且启用
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }
        if (warehouse.getStatus() != BizConstants.STATUS_ENABLED) {
            throw new BizException("库房已禁用");
        }

        WmsScrapOrder order = new WmsScrapOrder();
        // 生成报废单号：格式为BF + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setWarehouseId(dto.getWarehouseId());
        order.setStatus(OrderStatusEnum.DRAFT.getCode());
        order.setScrapReason(dto.getScrapReason());

        wmsScrapOrderMapper.insert(order);
        // 保存报废明细
        List<WmsScrapDetail> detailList = new ArrayList<>();
        for (ScrapOrderDto.ScrapDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + detailDto.getItemId());
            }
            WmsScrapDetail detail = new WmsScrapDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detailList.add(detail);
        }
        if (!detailList.isEmpty()) {
            Db.saveBatch(detailList);
        }

        // 报废单创建时不直接发布库存同步事件，需走审批流程后在submitOrder中发布

        ScrapOrderVo vo = scrapOrderConverter.toVo(order, Map.of());
        vo.setDetails(scrapOrderConverter.toDetailVoList(
                wmsScrapDetailMapper.selectList(
                        new LambdaQueryWrapper<WmsScrapDetail>()
                                .eq(WmsScrapDetail::getOrderId, order.getId()))));
        return vo;
    }

    /**
     * 更新报废单
     * 仅草稿状态可更新，逻辑删除原有明细后重新保存
     *
     * @param id 报废单ID
     * @param dto 报废单更新参数
     * @return 更新后的报废单VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScrapOrderVo updateOrder(Long id, ScrapOrderDto dto) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("报废单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("报废单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的报废单可以更新");
        }

        // 校验库房存在且启用
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在");
        }
        if (warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房已删除");
        }
        if (warehouse.getStatus() != BizConstants.STATUS_ENABLED) {
            throw new BizException("库房已禁用");
        }

        order.setWarehouseId(dto.getWarehouseId());
        order.setScrapReason(dto.getScrapReason());
        wmsScrapOrderMapper.updateById(order);

        // 逻辑删除原有明细
        List<WmsScrapDetail> oldDetails = wmsScrapDetailMapper.selectList(
                new LambdaQueryWrapper<WmsScrapDetail>().eq(WmsScrapDetail::getOrderId, id));
        List<WmsScrapDetail> updateDetails = new ArrayList<>();
        for (WmsScrapDetail oldDetail : oldDetails) {
            WmsScrapDetail updateDetail = new WmsScrapDetail();
            updateDetail.setId(oldDetail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetails.add(updateDetail);
        }
        if (!updateDetails.isEmpty()) {
            Db.updateBatchById(updateDetails);
        }

        // 保存新明细
        List<WmsScrapDetail> newDetailList = new ArrayList<>();
        for (ScrapOrderDto.ScrapDetailDto detailDto : dto.getDetails()) {
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            if (item.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("物品已删除: " + detailDto.getItemId());
            }
            WmsScrapDetail detail = new WmsScrapDetail();
            detail.setOrderId(id);
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            newDetailList.add(detail);
        }
        if (!newDetailList.isEmpty()) {
            Db.saveBatch(newDetailList);
        }

        ScrapOrderVo vo = scrapOrderConverter.toVo(order, Map.of());
        vo.setDetails(scrapOrderConverter.toDetailVoList(
                wmsScrapDetailMapper.selectList(
                        new LambdaQueryWrapper<WmsScrapDetail>()
                                .eq(WmsScrapDetail::getOrderId, id))));
        return vo;
    }

    /**
     * 删除报废单
     * 仅草稿状态可删除，逻辑删除报废单及明细
     *
     * @param id 报废单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("报废单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("报废单已删除");
        }
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的报废单可以删除");
        }
        // 逻辑删除主表
        WmsScrapOrder updateEntity = new WmsScrapOrder();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        wmsScrapOrderMapper.updateById(updateEntity);

        // 逻辑删除明细
        List<WmsScrapDetail> details = wmsScrapDetailMapper.selectList(
                new LambdaQueryWrapper<WmsScrapDetail>()
                        .eq(WmsScrapDetail::getOrderId, id));
        List<WmsScrapDetail> updateDetailList = new ArrayList<>();
        for (WmsScrapDetail detail : details) {
            WmsScrapDetail updateDetail = new WmsScrapDetail();
            updateDetail.setId(detail.getId());
            updateDetail.setDelFlag(DelFlagConstants.DELETED);
            updateDetailList.add(updateDetail);
        }
        if (!updateDetailList.isEmpty()) {
            Db.updateBatchById(updateDetailList);
        }
    }

    /**
     * 提交报废单
     * 仅草稿状态可提交，提交后状态变为待审核并发起审批请求，库存同步延迟到审批通过后
     *
     * @param id 报废单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long id) {
        WmsScrapOrder order = wmsScrapOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException("报废单不存在");
        }
        if (order.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("报废单已删除");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != OrderStatusEnum.DRAFT.getCode()) {
            throw new BizException("仅草稿状态的报废单可以提交");
        }
        // 状态变为待审核
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        wmsScrapOrderMapper.updateById(order);

        // 发起审批请求，库存同步延迟到审批通过后
        eventPublisher.publishEvent(new ApprovalRequestEvent(id, BizTypeEnum.SCRAP.getCode()));
    }

    /**
     * 生成报废单号: BF + 年月日 + 4位流水号
     * 使用Redis INCR原子操作保证并发安全
     */
    private String generateOrderNo() {
        return sequenceGenerator.next(OrderConstants.SCRAP_NO_PREFIX);
    }
}
