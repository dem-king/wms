package com.wms.report.converter;

import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.report.domain.entity.*;
import com.wms.report.domain.vo.CommonReportVo;
import com.wms.report.domain.vo.TransferReportVo;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报表转换器
 * 负责预聚合Entity到报表VO的转换，分类名称从预查询Map中填充以避免N+1
 */
@Component
@RequiredArgsConstructor
public class ReportConverter {

    private final WmsCategoryMapper wmsCategoryMapper;

    private final WmsWarehouseMapper wmsWarehouseMapper;

    /**
     * 批量查询分类名称构建Map
     *
     * @param categoryIds 分类ID集合
     * @return 分类ID到分类名称的映射
     */
    public Map<Long, String> buildCategoryNameMap(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return wmsCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(WmsCategory::getId, WmsCategory::getCategoryName, (a, b) -> a));
    }

    /**
     * 入库日聚合Entity转分类汇总项
     *
     * @param entity 入库日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toInboundCategorySummary(ReportInboundDaily entity,
                                                                        Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 出库日聚合Entity转分类汇总项
     *
     * @param entity 出库日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toOutboundCategorySummary(ReportOutboundDaily entity,
                                                                         Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 库存日快照Entity转分类汇总项
     *
     * @param entity 库存日快照实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toStockCategorySummary(ReportStockDaily entity,
                                                                      Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 归还日聚合Entity转分类汇总项
     *
     * @param entity 归还日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toReturnCategorySummary(ReportReturnDaily entity,
                                                                       Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        return item;
    }

    /**
     * 入库/出库/库存Entity列表转分布项列表(含占比计算)
     *
     * @param entities 聚合实体列表
     * @param categoryNameMap 分类名称映射
     * @param totalQuantity 总数量(用于计算占比)
     * @return 分布项列表
     */
    public List<CommonReportVo.DistributionItem> toDistributionList(List<?> entities,
                                                                     Map<Long, String> categoryNameMap,
                                                                     int totalQuantity) {
        List<CommonReportVo.DistributionItem> result = new ArrayList<>();
        for (Object obj : entities) {
            CommonReportVo.DistributionItem item = new CommonReportVo.DistributionItem();
            if (obj instanceof ReportInboundDaily e) {
                fillDistributionItem(item, e.getCategoryId(), e.getTotalQuantity(), e.getTotalAmount(), categoryNameMap);
            } else if (obj instanceof ReportOutboundDaily e) {
                fillDistributionItem(item, e.getCategoryId(), e.getTotalQuantity(), e.getTotalAmount(), categoryNameMap);
            } else if (obj instanceof ReportStockDaily e) {
                fillDistributionItem(item, e.getCategoryId(), e.getTotalQuantity(), e.getTotalAmount(), categoryNameMap);
            } else if (obj instanceof ReportReturnDaily e) {
                fillDistributionItem(item, e.getCategoryId(), e.getTotalQuantity(), null, categoryNameMap);
            } else if (obj instanceof ReportScrapDaily e) {
                fillDistributionItem(item, e.getCategoryId(), e.getTotalQuantity(), e.getTotalAmount(), categoryNameMap);
            } else if (obj instanceof ReportTransferDaily e) {
                fillDistributionItem(item, e.getCategoryId(), e.getTotalQuantity(), e.getTotalAmount(), categoryNameMap);
            }
            if (totalQuantity > 0 && item.getQuantity() != null) {
                item.setPercentage(BigDecimal.valueOf(item.getQuantity())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP));
            } else {
                item.setPercentage(BigDecimal.ZERO);
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 填充分布项公共字段
     */
    private void fillDistributionItem(CommonReportVo.DistributionItem item, Long categoryId,
                                       Integer quantity, BigDecimal amount, Map<Long, String> categoryNameMap) {
        item.setCategoryId(categoryId);
        item.setCategoryName(categoryNameMap.getOrDefault(categoryId, ""));
        item.setQuantity(quantity);
        item.setAmount(amount);
    }

    /**
     * 批量查询库房名称构建Map
     *
     * @param warehouseIds 库房ID集合
     * @return 库房ID到库房名称的映射
     */
    public Map<Long, String> buildWarehouseNameMap(Set<Long> warehouseIds) {
        if (warehouseIds == null || warehouseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, WmsWarehouse::getWarehouseName, (a, b) -> a));
    }

    /**
     * 报废日聚合Entity转分类汇总项
     *
     * @param entity 报废日聚合实体
     * @param categoryNameMap 分类名称映射
     * @return 分类汇总项
     */
    public CommonReportVo.CategorySummaryItem toScrapCategorySummary(ReportScrapDaily entity,
                                                                      Map<Long, String> categoryNameMap) {
        CommonReportVo.CategorySummaryItem item = new CommonReportVo.CategorySummaryItem();
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }

    /**
     * 调拨日聚合Entity转调拨分类汇总项
     *
     * @param entity 调拨日聚合实体
     * @param categoryNameMap 分类名称映射
     * @param warehouseNameMap 库房名称映射
     * @return 调拨分类汇总项
     */
    public TransferReportVo.TransferCategorySummaryItem toTransferCategorySummary(ReportTransferDaily entity,
                                                                                    Map<Long, String> categoryNameMap,
                                                                                    Map<Long, String> warehouseNameMap) {
        TransferReportVo.TransferCategorySummaryItem item = new TransferReportVo.TransferCategorySummaryItem();
        item.setFromWarehouseId(entity.getFromWarehouseId());
        item.setFromWarehouseName(warehouseNameMap.getOrDefault(entity.getFromWarehouseId(), ""));
        item.setToWarehouseId(entity.getToWarehouseId());
        item.setToWarehouseName(warehouseNameMap.getOrDefault(entity.getToWarehouseId(), ""));
        item.setCategoryId(entity.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(entity.getCategoryId(), ""));
        item.setQuantity(entity.getTotalQuantity());
        item.setAmount(entity.getTotalAmount());
        return item;
    }
}
