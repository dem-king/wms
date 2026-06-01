package com.wms.item.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.item.domain.dto.CategoryDto;
import com.wms.item.domain.dto.SubCategoryDto;
import com.wms.item.domain.vo.CategoryVo;
import com.wms.item.domain.vo.SubCategoryVo;
import com.wms.item.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 主类目管理控制器
 * 提供主类目和细分类目的CRUD接口
 */
@Tag(name = "主类目管理")
@RestController
@RequestMapping("/item/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 主类目分页列表
     */
    @Operation(summary = "主类目列表")
    @GetMapping
    @PreAuthorize("hasAuthority('item:category:list')")
    @DataScope
    public R<PageResult<CategoryVo>> page(PageParam pageParam) {
        return R.ok(categoryService.page(pageParam));
    }

    /**
     * 主类目列表(含细分类目)
     */
    @Operation(summary = "主类目列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('item:category:list')")
    @DataScope
    public R<List<CategoryVo>> listAll() {
        return R.ok(categoryService.listAll());
    }

    /**
     * 新增主类目
     */
    @Operation(summary = "新增主类目")
    @PostMapping
    @PreAuthorize("hasAuthority('item:category:add')")
    @OperLog(module = "item", type = "新增", desc = "新增主类目")
    public R<CategoryVo> create(@Valid @RequestBody CategoryDto dto) {
        return R.ok(categoryService.create(dto));
    }

    /**
     * 更新主类目
     */
    @Operation(summary = "更新主类目")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('item:category:edit')")
    @OperLog(module = "item", type = "更新", desc = "更新主类目")
    public R<CategoryVo> update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return R.ok(categoryService.update(id, dto));
    }

    /**
     * 删除主类目
     */
    @Operation(summary = "删除主类目")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('item:category:delete')")
    @OperLog(module = "item", type = "删除", desc = "删除主类目")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return R.ok();
    }

    /**
     * 细分类目分页列表
     */
    @Operation(summary = "细分类目列表")
    @GetMapping("/{categoryId}/sub")
    @PreAuthorize("hasAuthority('item:category:list')")
    @DataScope
    public R<PageResult<SubCategoryVo>> pageSubCategories(@PathVariable Long categoryId, PageParam pageParam) {
        return R.ok(categoryService.pageSubCategories(categoryId, pageParam));
    }

    /**
     * 细分类目列表
     */
    @Operation(summary = "细分类目列表")
    @GetMapping("/{categoryId}/sub/list")
    @PreAuthorize("hasAuthority('item:category:list')")
    @DataScope
    public R<List<SubCategoryVo>> listSubCategories(@PathVariable Long categoryId) {
        return R.ok(categoryService.listSubCategories(categoryId));
    }

    /**
     * 新增细分类目
     */
    @Operation(summary = "新增细分类目")
    @PostMapping("/{categoryId}/sub")
    @PreAuthorize("hasAuthority('item:category:add')")
    @OperLog(module = "item", type = "新增", desc = "新增细分类目")
    public R<SubCategoryVo> createSubCategory(@PathVariable Long categoryId,
                                              @Valid @RequestBody SubCategoryDto dto) {
        return R.ok(categoryService.createSubCategory(categoryId, dto));
    }

    /**
     * 更新细分类目
     */
    @Operation(summary = "更新细分类目")
    @PutMapping("/sub/{subId}")
    @PreAuthorize("hasAuthority('item:category:edit')")
    @OperLog(module = "item", type = "更新", desc = "更新细分类目")
    public R<SubCategoryVo> updateSubCategory(@PathVariable Long subId,
                                              @Valid @RequestBody SubCategoryDto dto) {
        return R.ok(categoryService.updateSubCategory(subId, dto));
    }

    /**
     * 删除细分类目
     */
    @Operation(summary = "删除细分类目")
    @DeleteMapping("/sub/{subId}")
    @PreAuthorize("hasAuthority('item:category:delete')")
    @OperLog(module = "item", type = "删除", desc = "删除细分类目")
    public R<Void> deleteSubCategory(@PathVariable Long subId) {
        categoryService.deleteSubCategory(subId);
        return R.ok();
    }
}
