package com.wms.item.converter;

import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.domain.entity.WmsSubCategory;
import com.wms.item.domain.vo.CategoryVo;
import com.wms.item.domain.vo.SubCategoryVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类目转换器
 * WmsCategory/WmsSubCategory实体与Vo之间的转换逻辑
 */
@Component
public class CategoryConverter {

    /**
     * WmsCategory实体转CategoryVo
     *
     * @param category 主类目实体
     * @return 主类目VO
     */
    public CategoryVo toVo(WmsCategory category) {
        CategoryVo vo = new CategoryVo();
        vo.setId(category.getId());
        vo.setCategoryName(category.getCategoryName());
        vo.setCategoryCode(category.getCategoryCode());
        vo.setCategoryColor(category.getCategoryColor());
        vo.setIcon(category.getIcon());
        vo.setSortOrder(category.getSortOrder());
        vo.setIsConsumable(category.getIsConsumable());
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }

    /**
     * 批量转换主类目实体列表
     *
     * @param categories 主类目实体列表
     * @return 主类目VO列表
     */
    public List<CategoryVo> toVoList(List<WmsCategory> categories) {
        return categories.stream().map(this::toVo).collect(Collectors.toList());
    }

    /**
     * WmsSubCategory实体转SubCategoryVo
     *
     * @param sub 细分类目实体
     * @return 细分类目VO
     */
    public SubCategoryVo toSubCategoryVo(WmsSubCategory sub) {
        SubCategoryVo vo = new SubCategoryVo();
        vo.setId(sub.getId());
        vo.setCategoryId(sub.getCategoryId());
        vo.setSubCategoryName(sub.getSubCategoryName());
        vo.setSubCategoryCode(sub.getSubCategoryCode());
        vo.setSortOrder(sub.getSortOrder());
        vo.setCreateTime(sub.getCreateTime());
        return vo;
    }

    /**
     * 批量转换细分类目实体列表
     *
     * @param subCategories 细分类目实体列表
     * @return 细分类目VO列表
     */
    public List<SubCategoryVo> toSubCategoryVoList(List<WmsSubCategory> subCategories) {
        return subCategories.stream().map(this::toSubCategoryVo).collect(Collectors.toList());
    }
}
