package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.item.domain.constant.ItemConstants;
import com.wms.item.domain.dto.CategoryDto;
import com.wms.item.domain.dto.SubCategoryDto;
import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.domain.entity.WmsSubCategory;
import com.wms.item.domain.vo.CategoryVo;
import com.wms.item.domain.vo.SubCategoryVo;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.item.mapper.WmsSubCategoryMapper;
import com.wms.item.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 主类目服务实现类
 * 处理主类目和细分类目的CRUD业务逻辑
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final WmsCategoryMapper wmsCategoryMapper;
    private final WmsSubCategoryMapper wmsSubCategoryMapper;

    @Override
    public List<CategoryVo> listAll() {
        List<WmsCategory> categories = wmsCategoryMapper.selectList(
                new LambdaQueryWrapper<WmsCategory>()
                        .orderByAsc(WmsCategory::getSortOrder)
                        .orderByDesc(WmsCategory::getCreateTime)
        );
        return categories.stream().map(this::toCategoryVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVo create(CategoryDto dto) {
        // 校验类目编码唯一性
        checkCategoryCodeUnique(dto.getCategoryCode(), null);
        WmsCategory category = new WmsCategory();
        copyDtoToEntity(dto, category);
        // 新增类目默认排序为0
        if (category.getSortOrder() == null) {
            category.setSortOrder(BizConstants.DEFAULT_SORT_ORDER);
        }
        if (category.getIsConsumable() == null) {
            category.setIsConsumable(ItemConstants.IS_CONSUMABLE_NO);
        }
        wmsCategoryMapper.insert(category);
        return toCategoryVo(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVo update(Long id, CategoryDto dto) {
        WmsCategory existing = wmsCategoryMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("主类目不存在");
        }
        // 校验类目编码唯一性(排除自身)
        checkCategoryCodeUnique(dto.getCategoryCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        wmsCategoryMapper.updateById(existing);
        return toCategoryVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsCategory existing = wmsCategoryMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("主类目不存在");
        }
        // 逻辑删除主类目
        WmsCategory updateEntity = new WmsCategory();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsCategoryMapper.updateById(updateEntity);
        // 逻辑删除其下的细分类目
        List<WmsSubCategory> subCategories = wmsSubCategoryMapper.selectList(
                new LambdaQueryWrapper<WmsSubCategory>()
                        .eq(WmsSubCategory::getCategoryId, id)
        );
        for (WmsSubCategory sub : subCategories) {
            WmsSubCategory updateSub = new WmsSubCategory();
            updateSub.setId(sub.getId());
            updateSub.setDelFlag(DelFlagConstants.DELETED);
            
            wmsSubCategoryMapper.updateById(updateSub);
        }
    }

    @Override
    public List<SubCategoryVo> listSubCategories(Long categoryId) {
        List<WmsSubCategory> subCategories = wmsSubCategoryMapper.selectList(
                new LambdaQueryWrapper<WmsSubCategory>()
                        .eq(WmsSubCategory::getCategoryId, categoryId)
                        .orderByAsc(WmsSubCategory::getSortOrder)
                        .orderByDesc(WmsSubCategory::getCreateTime)
        );
        return subCategories.stream().map(this::toSubCategoryVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubCategoryVo createSubCategory(Long categoryId, SubCategoryDto dto) {
        // 校验所属主类目存在
        WmsCategory category = wmsCategoryMapper.selectById(categoryId);
        if (category == null || category.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("所属主类目不存在");
        }
        // 校验细分类目编码唯一性(同一主类目下)
        checkSubCategoryCodeUnique(categoryId, dto.getSubCategoryCode(), null);
        WmsSubCategory subCategory = new WmsSubCategory();
        subCategory.setCategoryId(categoryId);
        subCategory.setSubCategoryName(dto.getSubCategoryName());
        subCategory.setSubCategoryCode(dto.getSubCategoryCode());
        subCategory.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : BizConstants.DEFAULT_SORT_ORDER);
        wmsSubCategoryMapper.insert(subCategory);
        return toSubCategoryVo(subCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubCategoryVo updateSubCategory(Long subId, SubCategoryDto dto) {
        WmsSubCategory existing = wmsSubCategoryMapper.selectById(subId);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("细分类目不存在");
        }
        // 校验细分类目编码唯一性(同一主类目下，排除自身)
        checkSubCategoryCodeUnique(existing.getCategoryId(), dto.getSubCategoryCode(), subId);
        existing.setSubCategoryName(dto.getSubCategoryName());
        existing.setSubCategoryCode(dto.getSubCategoryCode());
        existing.setSortOrder(dto.getSortOrder());
        wmsSubCategoryMapper.updateById(existing);
        return toSubCategoryVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubCategory(Long subId) {
        WmsSubCategory existing = wmsSubCategoryMapper.selectById(subId);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("细分类目不存在");
        }
        WmsSubCategory updateEntity = new WmsSubCategory();
        updateEntity.setId(subId);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsSubCategoryMapper.updateById(updateEntity);
    }

    /**
     * 校验主类目编码唯一性
     *
     * @param categoryCode 类目编码
     * @param excludeId    排除的ID(更新时排除自身)
     */
    private void checkCategoryCodeUnique(String categoryCode, Long excludeId) {
        LambdaQueryWrapper<WmsCategory> wrapper = new LambdaQueryWrapper<WmsCategory>()
                .eq(WmsCategory::getCategoryCode, categoryCode);
        if (excludeId != null) {
            wrapper.ne(WmsCategory::getId, excludeId);
        }
        Long count = wmsCategoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("类目编码已存在: " + categoryCode);
        }
    }

    /**
     * 校验细分类目编码唯一性(同一主类目下)
     *
     * @param categoryId       主类目ID
     * @param subCategoryCode  细分类目编码
     * @param excludeId        排除的ID(更新时排除自身)
     */
    private void checkSubCategoryCodeUnique(Long categoryId, String subCategoryCode, Long excludeId) {
        LambdaQueryWrapper<WmsSubCategory> wrapper = new LambdaQueryWrapper<WmsSubCategory>()
                .eq(WmsSubCategory::getCategoryId, categoryId)
                .eq(WmsSubCategory::getSubCategoryCode, subCategoryCode);
        if (excludeId != null) {
            wrapper.ne(WmsSubCategory::getId, excludeId);
        }
        Long count = wmsSubCategoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BizException("同一主类目下细分类目编码已存在: " + subCategoryCode);
        }
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(CategoryDto dto, WmsCategory entity) {
        entity.setCategoryName(dto.getCategoryName());
        entity.setCategoryCode(dto.getCategoryCode());
        entity.setCategoryColor(dto.getCategoryColor());
        entity.setIcon(dto.getIcon());
        entity.setSortOrder(dto.getSortOrder());
        entity.setIsConsumable(dto.getIsConsumable());
    }

    /**
     * WmsCategory实体转CategoryVo(含子分类列表)
     */
    private CategoryVo toCategoryVo(WmsCategory category) {
        CategoryVo vo = new CategoryVo();
        vo.setId(category.getId());
        vo.setCategoryName(category.getCategoryName());
        vo.setCategoryCode(category.getCategoryCode());
        vo.setCategoryColor(category.getCategoryColor());
        vo.setIcon(category.getIcon());
        vo.setSortOrder(category.getSortOrder());
        vo.setIsConsumable(category.getIsConsumable());
        vo.setCreateTime(category.getCreateTime());
        // 查询子分类列表
        vo.setSubCategories(listSubCategories(category.getId()));
        return vo;
    }

    /**
     * WmsSubCategory实体转SubCategoryVo
     */
    private SubCategoryVo toSubCategoryVo(WmsSubCategory sub) {
        SubCategoryVo vo = new SubCategoryVo();
        vo.setId(sub.getId());
        vo.setCategoryId(sub.getCategoryId());
        vo.setSubCategoryName(sub.getSubCategoryName());
        vo.setSubCategoryCode(sub.getSubCategoryCode());
        vo.setSortOrder(sub.getSortOrder());
        vo.setCreateTime(sub.getCreateTime());
        return vo;
    }
}
