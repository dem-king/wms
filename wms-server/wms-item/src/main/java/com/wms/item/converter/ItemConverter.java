package com.wms.item.converter;

import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemImage;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.domain.vo.ItemVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物品转换器
 * 负责WmsItem实体与ItemVo之间的转换，类目名称从预查询的Map中填充以避免N+1
 */
@Component
public class ItemConverter {

    /**
     * WmsItem实体转ItemVo
     *
     * @param item 物品实体
     * @param categoryNameMap 主类目ID到名称的映射
     * @param subCategoryNameMap 细分类目ID到名称的映射
     * @return 物品VO
     */
    public ItemVo toVo(WmsItem item, Map<Long, String> categoryNameMap, Map<Long, String> subCategoryNameMap) {
        ItemVo vo = new ItemVo();
        vo.setId(item.getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setPinyin(item.getPinyin());
        vo.setModel(item.getModel());
        vo.setSpec(item.getSpec());
        vo.setSpecModel(resolveSpecModel(item));
        vo.setUnit(item.getUnit());
        vo.setBrand(item.getBrand());
        vo.setCategoryId(item.getCategoryId());
        vo.setSubCategoryId(item.getSubCategoryId());
        vo.setSupplierId(item.getSupplierId());
        vo.setStatus(item.getStatus());
        vo.setIsConsumable(item.getIsConsumable());
        vo.setIsReturnable(item.getIsReturnable());
        vo.setPurchasePrice(item.getPurchasePrice());
        vo.setStockQty(item.getStockQty());
        vo.setStockLowerLimit(item.getStockLowerLimit());
        vo.setStockUpperLimit(item.getStockUpperLimit());
        vo.setReplenishThreshold(item.getReplenishThreshold());
        vo.setIdleDays(item.getIdleDays());
        vo.setRemark(item.getRemark());
        vo.setCreateTime(item.getCreateTime());
        if (item.getCategoryId() != null) {
            vo.setCategoryName(categoryNameMap.get(item.getCategoryId()));
        }
        if (item.getSubCategoryId() != null) {
            vo.setSubCategoryName(subCategoryNameMap.get(item.getSubCategoryId()));
        }
        return vo;
    }

    /**
     * 单条转换物品实体
     *
     * @param item 物品实体
     * @param categoryName 主类目名称
     * @param subCategoryName 细分类目名称
     * @return 物品VO
     */
    public ItemVo toVo(WmsItem item, String categoryName, String subCategoryName) {
        Map<Long, String> categoryNameMap = new HashMap<>();
        Map<Long, String> subCategoryNameMap = new HashMap<>();
        if (item.getCategoryId() != null && categoryName != null) {
            categoryNameMap.put(item.getCategoryId(), categoryName);
        }
        if (item.getSubCategoryId() != null && subCategoryName != null) {
            subCategoryNameMap.put(item.getSubCategoryId(), subCategoryName);
        }
        return toVo(item, categoryNameMap, subCategoryNameMap);
    }

    /**
     * 批量转换物品实体列表
     *
     * @param items 物品实体列表
     * @param categoryNameMap 主类目ID到名称的映射
     * @param subCategoryNameMap 细分类目ID到名称的映射
     * @return 物品VO列表
     */
    public List<ItemVo> toVoList(List<WmsItem> items, Map<Long, String> categoryNameMap,
                                  Map<Long, String> subCategoryNameMap) {
        return items.stream()
                .map(item -> toVo(item, categoryNameMap, subCategoryNameMap))
                .collect(Collectors.toList());
    }

    /**
     * WmsItemImage实体转ItemImageVo
     *
     * @param image 物品图片实体
     * @return 图片VO
     */
    public ItemImageVo toImageVo(WmsItemImage image) {
        ItemImageVo vo = new ItemImageVo();
        vo.setId(image.getId());
        vo.setItemId(image.getItemId());
        vo.setImageUrl(image.getImageUrl());
        vo.setBucket(image.getBucket());
        vo.setObjectName(image.getObjectName());
        vo.setImageName(image.getImageName());
        vo.setSortOrder(image.getSortOrder());
        return vo;
    }

    private String resolveSpecModel(WmsItem item) {
        if (item.getModel() != null && !item.getModel().isBlank()) {
            return item.getModel();
        }
        return item.getSpec();
    }
}
