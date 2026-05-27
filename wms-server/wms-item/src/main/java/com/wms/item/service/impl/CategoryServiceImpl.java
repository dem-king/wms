package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.constant.ItemConstants;
import com.wms.item.domain.dto.CategoryDto;
import com.wms.item.domain.dto.SubCategoryDto;
import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.domain.entity.WmsSubCategory;
import com.wms.item.domain.vo.CategoryVo;
import com.wms.item.domain.vo.SubCategoryVo;
import com.wms.item.converter.CategoryConverter;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.item.mapper.WmsSubCategoryMapper;
import com.wms.item.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final CategoryConverter categoryConverter;

    /**
     * 主类目分页列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<CategoryVo> page(PageParam pageParam) {
        return page(pageParam.getPage(), pageParam.getSize());
    }

    /**
     * 主类目分页列表
     *
     * @param page 当前页
     * @param size 每页数量
     * @return 分页结果
     */
    public PageResult<CategoryVo> page(int page, int size) {
        Page<WmsCategory> pageData = wmsCategoryMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<WmsCategory>()
                        .orderByAsc(WmsCategory::getSortOrder)
                        .orderByDesc(WmsCategory::getCreateTime)
        );

        PageResult<CategoryVo> result = new PageResult<>();
        result.setRecords(categoryConverter.toVoList(pageData.getRecords()));
        result.setTotal(pageData.getTotal());
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    /**
     * 查询所有主类目列表(含细分类目)
     * 
     * @return 主类目VO列表
     */
    @Override
    public List<CategoryVo> listAll() {
        List<WmsCategory> categories = wmsCategoryMapper.selectList(
                new LambdaQueryWrapper<WmsCategory>()
                        .orderByAsc(WmsCategory::getSortOrder)
                        .orderByDesc(WmsCategory::getCreateTime)
        );
        return categories.stream().map(this::toCategoryVoWithSubs).collect(Collectors.toList());
    }

    /**
     * 新增主类目
     * 校验编码唯一性，默认排序为0
     * 
     * @param dto 主类目新增参数
     * @return 新增后的主类目VO
     */
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
        return toCategoryVoWithSubs(category);
    }

    /**
     * 更新主类目
     * 
     * @param id 主类目ID
     * @param dto 主类目更新参数
     * @return 更新后的主类目VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVo update(Long id, CategoryDto dto) {
        WmsCategory existing = wmsCategoryMapper.selectById(id);
        if (existing == null) {
            throw new BizException("主类目不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("主类目已删除");
        }
        // 校验类目编码唯一性(排除自身)
        checkCategoryCodeUnique(dto.getCategoryCode(), id);
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        wmsCategoryMapper.updateById(existing);
        return toCategoryVoWithSubs(existing);
    }

    /**
     * 删除主类目(逻辑删除)
     * 同时逻辑删除其下的细分类目
     * 
     * @param id 主类目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsCategory existing = wmsCategoryMapper.selectById(id);
        if (existing == null) {
            throw new BizException("主类目不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("主类目已删除");
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
        List<WmsSubCategory> updateSubList = new ArrayList<>();
        for (WmsSubCategory sub : subCategories) {
            WmsSubCategory updateSub = new WmsSubCategory();
            updateSub.setId(sub.getId());
            updateSub.setDelFlag(DelFlagConstants.DELETED);
            
            updateSubList.add(updateSub);
        }
        if (!updateSubList.isEmpty()) {
            Db.updateBatchById(updateSubList);
        }
    }

    /**
     * 查询指定主类目下的细分类目列表
     * 
     * @param categoryId 主类目ID
     * @return 细分类目VO列表
     */
    @Override
    public List<SubCategoryVo> listSubCategories(Long categoryId) {
        List<WmsSubCategory> subCategories = wmsSubCategoryMapper.selectList(
                new LambdaQueryWrapper<WmsSubCategory>()
                        .eq(WmsSubCategory::getCategoryId, categoryId)
                        .orderByAsc(WmsSubCategory::getSortOrder)
                        .orderByDesc(WmsSubCategory::getCreateTime)
        );
        return subCategories.stream().map(categoryConverter::toSubCategoryVo).collect(Collectors.toList());
    }

    /**
     * 分页查询指定主类目下的细分类目列表
     *
     * @param categoryId 主类目ID
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<SubCategoryVo> pageSubCategories(Long categoryId, PageParam pageParam) {
        return pageSubCategories(categoryId, pageParam.getPage(), pageParam.getSize());
    }

    /**
     * 分页查询指定主类目下的细分类目列表
     *
     * @param categoryId 主类目ID
     * @param page 当前页
     * @param size 每页数量
     * @return 分页结果
     */
    public PageResult<SubCategoryVo> pageSubCategories(Long categoryId, int page, int size) {
        Page<WmsSubCategory> pageData = wmsSubCategoryMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<WmsSubCategory>()
                        .eq(WmsSubCategory::getCategoryId, categoryId)
                        .orderByAsc(WmsSubCategory::getSortOrder)
                        .orderByDesc(WmsSubCategory::getCreateTime)
        );

        PageResult<SubCategoryVo> result = new PageResult<>();
        result.setRecords(categoryConverter.toSubCategoryVoList(pageData.getRecords()));
        result.setTotal(pageData.getTotal());
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    /**
     * 新增细分类目
     * 校验所属主类目存在且编码唯一
     * 
     * @param categoryId 主类目ID
     * @param dto 细分类目新增参数
     * @return 新增后的细分类目VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubCategoryVo createSubCategory(Long categoryId, SubCategoryDto dto) {
        // 校验所属主类目存在
        WmsCategory category = wmsCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BizException("所属主类目不存在");
        }
        if (category.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("所属主类目已删除");
        }
        // 校验细分类目编码唯一性(同一主类目下)
        checkSubCategoryCodeUnique(categoryId, dto.getSubCategoryCode(), null);
        WmsSubCategory subCategory = new WmsSubCategory();
        subCategory.setCategoryId(categoryId);
        subCategory.setSubCategoryName(dto.getSubCategoryName());
        subCategory.setSubCategoryCode(dto.getSubCategoryCode());
        subCategory.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : BizConstants.DEFAULT_SORT_ORDER);
        wmsSubCategoryMapper.insert(subCategory);
        return categoryConverter.toSubCategoryVo(subCategory);
    }

    /**
     * 更新细分类目
     * 
     * @param subId 细分类目ID
     * @param dto 细分类目更新参数
     * @return 更新后的细分类目VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubCategoryVo updateSubCategory(Long subId, SubCategoryDto dto) {
        WmsSubCategory existing = wmsSubCategoryMapper.selectById(subId);
        if (existing == null) {
            throw new BizException("细分类目不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("细分类目已删除");
        }
        // 校验细分类目编码唯一性(同一主类目下，排除自身)
        checkSubCategoryCodeUnique(existing.getCategoryId(), dto.getSubCategoryCode(), subId);
        existing.setSubCategoryName(dto.getSubCategoryName());
        existing.setSubCategoryCode(dto.getSubCategoryCode());
        existing.setSortOrder(dto.getSortOrder());
        wmsSubCategoryMapper.updateById(existing);
        return categoryConverter.toSubCategoryVo(existing);
    }

    /**
     * 删除主类目(逻辑删除)
     * 同时逻辑删除其下的细分类目
     * 
     * @param id 主类目ID
     */
    /**
     * 删除细分类目(逻辑删除)
     * 
     * @param subId 细分类目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubCategory(Long subId) {
        WmsSubCategory existing = wmsSubCategoryMapper.selectById(subId);
        if (existing == null) {
            throw new BizException("细分类目不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("细分类目已删除");
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
    private CategoryVo toCategoryVoWithSubs(WmsCategory category) {
        CategoryVo vo = categoryConverter.toVo(category);
        // 查询子分类列表
        vo.setSubCategories(listSubCategories(category.getId()));
        return vo;
    }
}
