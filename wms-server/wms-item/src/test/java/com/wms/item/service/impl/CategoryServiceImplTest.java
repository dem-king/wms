package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.item.converter.CategoryConverter;
import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.domain.entity.WmsSubCategory;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.item.mapper.WmsSubCategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("CategoryServiceImpl 分页测试")
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private WmsCategoryMapper wmsCategoryMapper;

    @Mock
    private WmsSubCategoryMapper wmsSubCategoryMapper;

    @Spy
    private CategoryConverter categoryConverter;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("分页查询主类目时应返回不含子类目的分页记录")
    void shouldReturnPagedCategories() {
        WmsCategory first = buildCategory(1L, "刀具类");
        WmsCategory second = buildCategory(2L, "轴承类");

        Page<WmsCategory> page = new Page<>(1, 10);
        page.setRecords(List.of(first, second));
        page.setTotal(18);

        when(wmsCategoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        var result = categoryService.page(1, 10);

        assertEquals(18L, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(List.of("刀具类", "轴承类"), result.getRecords().stream().map(item -> item.getCategoryName()).toList());
    }

    @Test
    @DisplayName("分页查询细分类目时应按主类目返回分页记录")
    void shouldReturnPagedSubCategories() {
        WmsSubCategory first = buildSubCategory(11L, 1L, "硬质刀片");
        WmsSubCategory second = buildSubCategory(12L, 1L, "刀柄");

        Page<WmsSubCategory> page = new Page<>(2, 5);
        page.setRecords(List.of(first, second));
        page.setTotal(7);

        when(wmsSubCategoryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        var result = categoryService.pageSubCategories(1L, 2, 5);

        assertEquals(7L, result.getTotal());
        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(List.of("硬质刀片", "刀柄"), result.getRecords().stream().map(item -> item.getSubCategoryName()).toList());
    }

    private WmsCategory buildCategory(Long id, String name) {
        WmsCategory category = new WmsCategory();
        category.setId(id);
        category.setCategoryName(name);
        category.setCategoryCode("CAT-" + id);
        return category;
    }

    private WmsSubCategory buildSubCategory(Long id, Long categoryId, String name) {
        WmsSubCategory subCategory = new WmsSubCategory();
        subCategory.setId(id);
        subCategory.setCategoryId(categoryId);
        subCategory.setSubCategoryName(name);
        subCategory.setSubCategoryCode("SUB-" + id);
        return subCategory;
    }
}
