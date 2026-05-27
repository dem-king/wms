package com.wms.item.service;

import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.item.domain.dto.CategoryDto;
import com.wms.item.domain.dto.SubCategoryDto;
import com.wms.item.domain.vo.CategoryVo;
import com.wms.item.domain.vo.SubCategoryVo;

import java.util.List;

/**
 * 主类目服务接口
 * 提供主类目和细分类目的CRUD功能
 */
public interface CategoryService {

    /**
     * 主类目分页列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    PageResult<CategoryVo> page(PageParam pageParam);

    /**
     * 查询所有主类目列表(含细分类目)
     *
     * @return 主类目VO列表
     */
    List<CategoryVo> listAll();

    /**
     * 新增主类目
     * 校验类目编码唯一性后保存
     *
     * @param dto 主类目新增参数
     * @return 新增后的主类目VO
     */
    CategoryVo create(CategoryDto dto);

    /**
     * 更新主类目
     * 校验类目存在性和编码唯一性后更新
     *
     * @param id  主类目ID
     * @param dto 主类目更新参数
     * @return 更新后的主类目VO
     */
    CategoryVo update(Long id, CategoryDto dto);

    /**
     * 删除主类目(逻辑删除)
     * 同时逻辑删除其下的细分类目
     *
     * @param id 主类目ID
     */
    void delete(Long id);

    /**
     * 查询指定主类目下的细分类目列表
     *
     * @param categoryId 主类目ID
     * @return 细分类目VO列表
     */
    List<SubCategoryVo> listSubCategories(Long categoryId);

    /**
     * 分页查询指定主类目下的细分类目列表
     *
     * @param categoryId 主类目ID
     * @param pageParam 分页参数
     * @return 分页结果
     */
    PageResult<SubCategoryVo> pageSubCategories(Long categoryId, PageParam pageParam);

    /**
     * 新增细分类目
     * 校验所属主类目存在性和编码唯一性后保存
     *
     * @param categoryId 所属主类目ID
     * @param dto        细分类目新增参数
     * @return 新增后的细分类目VO
     */
    SubCategoryVo createSubCategory(Long categoryId, SubCategoryDto dto);

    /**
     * 更新细分类目
     *
     * @param subId 细分类目ID
     * @param dto   细分类目更新参数
     * @return 更新后的细分类目VO
     */
    SubCategoryVo updateSubCategory(Long subId, SubCategoryDto dto);

    /**
     * 删除细分类目(逻辑删除)
     *
     * @param subId 细分类目ID
     */
    void deleteSubCategory(Long subId);
}
