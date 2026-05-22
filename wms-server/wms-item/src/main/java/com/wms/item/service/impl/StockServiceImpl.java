package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.dto.StockThresholdDto;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.domain.vo.StockVo;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.item.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 库存服务实现类
 * 处理库存分页查询、物品库存详情、库存预警、阈值设置等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final WmsStockMapper wmsStockMapper;
    private final WmsItemMapper wmsItemMapper;

    @Override
    public PageResult<StockVo> page(PageParam pageParam, Long warehouseId,
                                    Long categoryId, Boolean alertOnly) {
        LambdaQueryWrapper<WmsStock> wrapper = new LambdaQueryWrapper<WmsStock>();
        // 按库房筛选
        if (warehouseId != null) {
            wrapper.eq(WmsStock::getWarehouseId, warehouseId);
        }
        // 按主类目筛选: 先查该类目下的物品ID
        if (categoryId != null) {
            List<WmsItem> items = wmsItemMapper.selectList(
                    new LambdaQueryWrapper<WmsItem>()
                            .eq(WmsItem::getCategoryId, categoryId)
            );
            List<Long> itemIds = items.stream().map(WmsItem::getId).collect(Collectors.toList());
            if (itemIds.isEmpty()) {
                PageResult<StockVo> result = new PageResult<>();
                result.setRecords(List.of());
                result.setTotal(0L);
                result.setPage(pageParam.getPage());
                result.setSize(pageParam.getSize());
                return result;
            }
            wrapper.in(WmsStock::getItemId, itemIds);
        }
        wrapper.orderByDesc(WmsStock::getUpdateTime);

        Page<WmsStock> page = wmsStockMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        // 批量查询所有涉及的物品信息，避免N+1查询
        List<WmsStock> stocks = page.getRecords();
        Map<Long, WmsItem> itemMap = stocks.stream()
                .map(WmsStock::getItemId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> wmsItemMapper.selectById(id),
                        (existing, replacement) -> existing));

        PageResult<StockVo> result = new PageResult<>();
        List<StockVo> voList = stocks.stream().map(stock -> toStockVo(stock, itemMap)).collect(Collectors.toList());
        // 如果仅显示预警库存，过滤quantity < stockLowerLimit的记录
        if (alertOnly != null && alertOnly) {
            voList = voList.stream().filter(vo -> Boolean.TRUE.equals(vo.getAlert())).collect(Collectors.toList());
        }
        result.setRecords(voList);
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    @Override
    public List<StockVo> getByItemId(Long itemId) {
        List<WmsStock> stocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .eq(WmsStock::getItemId, itemId)
                        .orderByDesc(WmsStock::getUpdateTime)
        );
        return stocks.stream().map(this::toStockVo).collect(Collectors.toList());
    }

    @Override
    public List<StockVo> getAlertList() {
        // 在SQL层面直接筛选存在对应物品且库存数量低于安全库存的记录，避免全表查询
        List<WmsStock> stocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .apply("quantity < (SELECT stock_lower_limit FROM wms_item WHERE wms_item.id = wms_stock.item_id AND wms_item.del_flag = 0)")
        );
        // 批量查询涉及的物品信息
        Map<Long, WmsItem> itemMap = stocks.stream()
                .map(WmsStock::getItemId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> wmsItemMapper.selectById(id),
                        (existing, replacement) -> existing));
        return stocks.stream()
                .map(stock -> toStockVo(stock, itemMap))
                .collect(Collectors.toList());
    }

    /**
     * 更新物品库存预警阈值
     * 校验物品存在性，更新安全库存/最大库存/补货阈值，返回更新后的库存信息
     *
     * @param itemId 物品ID
     * @param dto    阈值设置参数
     * @return 更新后的库存VO
     */
    @Override
    public StockVo updateThreshold(Long itemId, StockThresholdDto dto) {
        WmsItem item = wmsItemMapper.selectById(itemId);
        if (item == null) {
            throw new BizException("物品不存在: itemId=" + itemId);
        }
        // 按需更新阈值字段
        if (dto.getStockLowerLimit() != null) {
            item.setStockLowerLimit(dto.getStockLowerLimit());
        }
        if (dto.getStockUpperLimit() != null) {
            item.setStockUpperLimit(dto.getStockUpperLimit());
        }
        if (dto.getReplenishThreshold() != null) {
            item.setReplenishThreshold(dto.getReplenishThreshold());
        }
        // 校验上限不小于下限
        if (item.getStockLowerLimit() != null && item.getStockUpperLimit() != null
                && item.getStockUpperLimit() < item.getStockLowerLimit()) {
            throw new BizException("最大库存不能小于安全库存");
        }
        wmsItemMapper.updateById(item);

        // 查询该物品的第一条库存记录，构建返回VO
        WmsStock stock = wmsStockMapper.selectOne(
                new LambdaQueryWrapper<WmsStock>()
                        .eq(WmsStock::getItemId, itemId)
                        .orderByDesc(WmsStock::getUpdateTime)
                        .last("LIMIT 1")
        );
        if (stock == null) {
            StockVo vo = new StockVo();
            vo.setItemId(itemId);
            vo.setItemCode(item.getItemCode());
            vo.setItemName(item.getItemName());
            vo.setStockLowerLimit(item.getStockLowerLimit());
            vo.setStockUpperLimit(item.getStockUpperLimit());
            return vo;
        }
        return toStockVo(stock);
    }

    /**
     * WmsStock实体转StockVo(单条查询时使用，内部查DB填充物品信息)
     *
     * @param stock 库存实体
     * @return 库存VO
     */
    private StockVo toStockVo(WmsStock stock) {
        Map<Long, WmsItem> itemMap = stock.getItemId() != null
                ? Map.of(stock.getItemId(), wmsItemMapper.selectById(stock.getItemId()))
                : Map.of();
        return toStockVo(stock, itemMap);
    }

    /**
     * WmsStock实体转StockVo(使用预查询的物品Map，避免N+1查询)
     *
     * @param stock 库存实体
     * @param itemMap 物品ID到实体的映射
     * @return 库存VO
     */
    private StockVo toStockVo(WmsStock stock, Map<Long, WmsItem> itemMap) {
        StockVo vo = new StockVo();
        vo.setId(stock.getId());
        vo.setItemId(stock.getItemId());
        vo.setBinId(stock.getBinId());
        vo.setWarehouseId(stock.getWarehouseId());
        vo.setAreaId(stock.getAreaId());
        vo.setCabinetId(stock.getCabinetId());
        vo.setQuantity(stock.getQuantity());
        vo.setLockedQuantity(stock.getLockedQuantity());
        vo.setAmount(stock.getAmount());
        vo.setLastInboundTime(stock.getLastInboundTime());
        vo.setLastOutboundTime(stock.getLastOutboundTime());
        // 从Map中填充物品信息和库存阈值
        if (stock.getItemId() != null) {
            WmsItem item = itemMap.get(stock.getItemId());
            if (item != null) {
                vo.setItemCode(item.getItemCode());
                vo.setItemName(item.getItemName());
                vo.setStockLowerLimit(item.getStockLowerLimit());
                vo.setStockUpperLimit(item.getStockUpperLimit());
                // 判断是否预警: 库存数量 < 安全库存
                vo.setAlert(item.getStockLowerLimit() != null
                        && stock.getQuantity() != null
                        && stock.getQuantity() < item.getStockLowerLimit());
            }
        }
        return vo;
    }
}
