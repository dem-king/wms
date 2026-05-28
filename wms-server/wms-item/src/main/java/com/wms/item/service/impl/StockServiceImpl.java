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
import com.wms.item.converter.StockConverter;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.item.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
    private final StockConverter stockConverter;

    /**
     * 分页查询库存
     * 支持按库房、类目、预警筛选
     * 
     * @param pageParam 分页参数
     * @param warehouseId 库房ID(可选)
     * @param categoryId 主类目ID(可选)
     * @param alertOnly 是否仅显示预警(可选)
     * @return 库存分页结果
     */
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
        if (Boolean.TRUE.equals(alertOnly)) {
            wrapper.apply("quantity < (SELECT stock_lower_limit FROM wms_item WHERE wms_item.id = wms_stock.item_id)");
        }
        wrapper.orderByDesc(WmsStock::getUpdateTime);

        Page<WmsStock> page = wmsStockMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        // 批量查询所有涉及的物品信息，避免N+1查询
        List<WmsStock> stocks = page.getRecords();
        Map<Long, WmsItem> itemMap = loadItemMap(stocks);

        PageResult<StockVo> result = new PageResult<>();
        List<StockVo> voList = stockConverter.toVoList(stocks, itemMap);
        result.setRecords(voList);
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 按物品ID查询库存列表
     * 
     * @param itemId 物品ID
     * @return 库存VO列表
     */
    @Override
    public List<StockVo> getByItemId(Long itemId) {
        List<WmsStock> stocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .eq(WmsStock::getItemId, itemId)
                        .orderByDesc(WmsStock::getUpdateTime)
        );
        return stockConverter.toVoList(stocks, loadItemMap(stocks));
    }

    /**
     * 查询库存预警列表
     * 在SQL层面筛选库存数量低于安全库存的记录
     * 
     * @return 预警库存VO列表
     */
    @Override
    public List<StockVo> getAlertList() {
        // 在SQL层面直接筛选存在对应物品且库存数量低于安全库存的记录，避免全表查询
        List<WmsStock> stocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .apply("quantity < (SELECT stock_lower_limit FROM wms_item WHERE wms_item.id = wms_stock.item_id)")
        );
        return stockConverter.toVoList(stocks, loadItemMap(stocks));
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
        return stockConverter.toVo(stock, loadItemMap(List.of(stock)));
    }

    /**
     * 批量加载库存关联的物品信息，统一复用以避免逐条查库。
     */
    private Map<Long, WmsItem> loadItemMap(List<WmsStock> stocks) {
        Set<Long> itemIds = stocks.stream()
                .map(WmsStock::getItemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        return wmsItemMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(WmsItem::getId, Function.identity()));
    }
}
