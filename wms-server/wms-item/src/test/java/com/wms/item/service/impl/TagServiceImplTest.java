package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.item.converter.ItemConverter;
import com.wms.item.converter.TagConverter;
import com.wms.item.domain.entity.WmsTag;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsItemTagMapper;
import com.wms.item.mapper.WmsTagMapper;
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

@DisplayName("TagServiceImpl 分页测试")
@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private WmsTagMapper wmsTagMapper;

    @Mock
    private WmsItemTagMapper wmsItemTagMapper;

    @Mock
    private WmsItemMapper wmsItemMapper;

    @Spy
    private TagConverter tagConverter;

    @Mock
    private ItemConverter itemConverter;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    @DisplayName("分页查询标签时应返回分页结果和转换后的记录")
    void shouldReturnPagedTags() {
        WmsTag first = buildTag(1L, "刀具");
        WmsTag second = buildTag(2L, "轴承");

        Page<WmsTag> page = new Page<>(2, 10);
        page.setRecords(List.of(first, second));
        page.setTotal(12);

        when(wmsTagMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        var result = tagService.page(2, 10);

        assertEquals(12L, result.getTotal());
        assertEquals(2, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(List.of("刀具", "轴承"), result.getRecords().stream().map(TagVo::getTagName).toList());
    }

    private WmsTag buildTag(Long id, String tagName) {
        WmsTag tag = new WmsTag();
        tag.setId(id);
        tag.setTagName(tagName);
        return tag;
    }
}
