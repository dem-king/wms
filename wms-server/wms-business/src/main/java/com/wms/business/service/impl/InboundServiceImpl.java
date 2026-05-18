package com.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.business.domain.dto.InboundOrderDto;
import com.wms.business.domain.entity.WmsInboundDetail;
import com.wms.business.domain.entity.WmsInboundOrder;
import com.wms.business.domain.vo.InboundOrderVo;
import com.wms.business.event.StockSyncEvent;
import com.wms.business.mapper.WmsInboundDetailMapper;
import com.wms.business.mapper.WmsInboundOrderMapper;
import com.wms.business.service.InboundService;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import com.wms.system.domain.entity.SysSupplier;
import com.wms.system.mapper.SysSupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    /** 入库单号前缀 */
    private static final String ORDER_NO_PREFIX = "RK";

    /** 草稿状态 */
    private static final int STATUS_DRAFT = 0;

    /** 待审核状态 */
    private static final int STATUS_PENDING = 1;

    /** 已完成状态 */
    private static final int STATUS_COMPLETED = 3;

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
        // 按库房筛选
        if (warehouseId != null) {
            wrapper.eq(WmsInboundOrder::getWarehouseId, warehouseId);
        }
        // 按入库类型筛选
        if (orderType != null) {
            wrapper.eq(WmsInboundOrder::getOrderType, orderType);
        }
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsInboundOrder::getStatus, status);
        }
        // 按单号模糊搜索
        if (orderNo != null && !orderNo.isBlank()) {
            wrapper.like(WmsInboundOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(WmsInboundOrder::getCreateTime);

        Page<WmsInboundOrder> page = wmsInboundOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        PageResult<InboundOrderVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream().map(this::toOrderVo).collect(Collectors.toList()));
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
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("入库单不存在");
        }
        InboundOrderVo vo = toOrderVo(order);
        // 填充明细列表
        vo.setDetails(getOrderDetails(id));
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
        // 校验库房是否存在且为启用状态
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == 1) {
            throw new BizException("库房不存在或已禁用");
        }
        // 校验供应商存在(如果指定)
        if (dto.getSupplierId() != null) {
            SysSupplier supplier = sysSupplierMapper.selectById(dto.getSupplierId());
            if (supplier == null || supplier.getDelFlag() == 1) {
                throw new BizException("供应商不存在");
            }
        }

        WmsInboundOrder order = new WmsInboundOrder();
        // 生成入库单号：格式为RK + 年月日 + 4位流水号
        order.setOrderNo(generateOrderNo());
        order.setWarehouseId(dto.getWarehouseId());
        order.setSupplierId(dto.getSupplierId());
        order.setOrderType(dto.getOrderType());
        order.setStatus(STATUS_DRAFT);
        order.setRemark(dto.getRemark());

        // 计算总金额并保存明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        wmsInboundOrderMapper.insert(order);
        for (InboundOrderDto.InboundDetailDto detailDto : dto.getDetails()) {
            // 校验物品存在
            WmsItem item = wmsItemMapper.selectById(detailDto.getItemId());
            if (item == null || item.getDelFlag() == 1) {
                throw new BizException("物品不存在: " + detailDto.getItemId());
            }
            WmsInboundDetail detail = new WmsInboundDetail();
            detail.setOrderId(order.getId());
            detail.setItemId(detailDto.getItemId());
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            // 计算明细金额 = 数量 × 单价
            BigDecimal amount = BigDecimal.ZERO;
            if (detailDto.getUnitPrice() != null && detailDto.getQuantity() != null) {
                amount = detailDto.getUnitPrice().multiply(BigDecimal.valueOf(detailDto.getQuantity()));
            }
            detail.setAmount(amount);
            detail.setBinId(detailDto.getBinId());
            wmsInboundDetailMapper.insert(detail);
            totalAmount = totalAmount.add(amount);
        }
        // 更新总金额
        order.setTotalAmount(totalAmount);
        wmsInboundOrderMapper.updateById(order);

        InboundOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(order.getId()));
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
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("入库单不存在");
        }
        // 仅草稿状态可更新
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的入库单可以更新");
        }

        // 校验库房
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == 1) {
            throw new BizException("库房不存在或已禁用");
        }

        order.setWarehouseId(dto.getWarehouseId());
        order.setSupplierId(dto.getSupplierId());
        order.setOrderType(dto.getOrderType());
        order.setRemark(dto.getRemark());
        wmsInboundOrderMapper.updateById(order);

        // 逻辑删除原有明细后重新保存
        List<WmsInboundDetail> oldDetails = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>().eq(WmsInboundDetail::getOrderId, id));
        for (WmsInboundDetail oldDetail : oldDetails) {
            WmsInboundDetail updateDetail = new WmsInboundDetail();
            updateDetail.setId(oldDetail.getId());
            updateDetail.setDelFlag(1);
            updateDetail.setLastOperType("d");
            wmsInboundDetailMapper.updateById(updateDetail);
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
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
            wmsInboundDetailMapper.insert(detail);
            totalAmount = totalAmount.add(amount);
        }
        order.setTotalAmount(totalAmount);
        wmsInboundOrderMapper.updateById(order);

        InboundOrderVo vo = toOrderVo(order);
        vo.setDetails(getOrderDetails(id));
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
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("入库单不存在");
        }
        // 仅草稿状态可提交
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的入库单可以提交");
        }

        // 状态变为待审核
        order.setStatus(STATUS_PENDING);
        wmsInboundOrderMapper.updateById(order);

        // 直接标记为已完成并发布库存同步事件(简化流程，实际应走审批)
        order.setStatus(STATUS_COMPLETED);
        wmsInboundOrderMapper.updateById(order);

        // 查询入库明细，逐条发布库存同步事件
        List<WmsInboundDetail> details = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>()
                        .eq(WmsInboundDetail::getOrderId, id));
        for (WmsInboundDetail detail : details) {
            eventPublisher.publishEvent(new StockSyncEvent(
                    detail.getItemId(), order.getWarehouseId(), detail.getBinId(),
                    detail.getQuantity(), "IN"));
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
        if (order == null || order.getDelFlag() == 1) {
            throw new BizException("入库单不存在");
        }
        // 仅草稿状态可删除
        if (order.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的入库单可以删除");
        }
        // 逻辑删除入库单
        WmsInboundOrder updateEntity = new WmsInboundOrder();
        updateEntity.setId(id);
        updateEntity.setDelFlag(1);
        updateEntity.setLastOperType("d");
        wmsInboundOrderMapper.updateById(updateEntity);
        // 逻辑删除入库明细
        List<WmsInboundDetail> details = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>()
                        .eq(WmsInboundDetail::getOrderId, id));
        for (WmsInboundDetail detail : details) {
            WmsInboundDetail updateDetail = new WmsInboundDetail();
            updateDetail.setId(detail.getId());
            updateDetail.setDelFlag(1);
            updateDetail.setLastOperType("d");
            wmsInboundDetailMapper.updateById(updateDetail);
        }
    }

    /**
     * 生成入库单号: RK + 年月日 + 4位流水号
     * 示例: RK202605140001
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 查询当天最大单号
        LambdaQueryWrapper<WmsInboundOrder> wrapper = new LambdaQueryWrapper<WmsInboundOrder>()
                .likeRight(WmsInboundOrder::getOrderNo, ORDER_NO_PREFIX + datePart)
                .orderByDesc(WmsInboundOrder::getOrderNo)
                .last("LIMIT 1");
        WmsInboundOrder lastOrder = wmsInboundOrderMapper.selectOne(wrapper);
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
     * 获取入库单明细列表
     *
     * @param orderId 入库单ID
     * @return 入库明细VO列表
     */
    private List<InboundOrderVo.InboundDetailVo> getOrderDetails(Long orderId) {
        List<WmsInboundDetail> details = wmsInboundDetailMapper.selectList(
                new LambdaQueryWrapper<WmsInboundDetail>()
                        .eq(WmsInboundDetail::getOrderId, orderId));
        return details.stream().map(this::toDetailVo).collect(Collectors.toList());
    }

    /**
     * WmsInboundOrder实体转InboundOrderVo(填充库房名称、供应商名称)
     */
    private InboundOrderVo toOrderVo(WmsInboundOrder order) {
        InboundOrderVo vo = new InboundOrderVo();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setWarehouseId(order.getWarehouseId());
        vo.setSupplierId(order.getSupplierId());
        vo.setOrderType(order.getOrderType());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
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
        // 填充供应商名称
        if (order.getSupplierId() != null) {
            SysSupplier supplier = sysSupplierMapper.selectById(order.getSupplierId());
            if (supplier != null) {
                vo.setSupplierName(supplier.getSupplierName());
            }
        }
        return vo;
    }

    /**
     * WmsInboundDetail实体转InboundDetailVo(填充物品名称、库位编码)
     */
    private InboundOrderVo.InboundDetailVo toDetailVo(WmsInboundDetail detail) {
        InboundOrderVo.InboundDetailVo vo = new InboundOrderVo.InboundDetailVo();
        vo.setId(detail.getId());
        vo.setItemId(detail.getItemId());
        vo.setQuantity(detail.getQuantity());
        vo.setUnitPrice(detail.getUnitPrice());
        vo.setAmount(detail.getAmount());
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
