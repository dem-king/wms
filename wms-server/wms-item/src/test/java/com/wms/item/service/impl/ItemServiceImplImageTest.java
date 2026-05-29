package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.storage.StorageConstants;
import com.wms.common.storage.StorageStrategy;
import com.wms.item.converter.ItemConverter;
import com.wms.item.domain.dto.ItemImageDto;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemImage;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.mapper.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 物品图片服务测试。
 */
@DisplayName("物品图片服务测试")
@ExtendWith(MockitoExtension.class)
class ItemServiceImplImageTest {

    @Mock
    private WmsItemMapper wmsItemMapper;

    @Mock
    private WmsItemTagMapper wmsItemTagMapper;

    @Mock
    private WmsItemImageMapper wmsItemImageMapper;

    @Mock
    private WmsStockMapper wmsStockMapper;

    @Mock
    private WmsCategoryMapper wmsCategoryMapper;

    @Mock
    private WmsSubCategoryMapper wmsSubCategoryMapper;

    @Mock
    private WmsTagMapper wmsTagMapper;

    @Mock
    private StorageStrategy storageStrategy;

    @Spy
    private ItemConverter itemConverter;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("关联统一上传结果时应保存图片元数据")
    void shouldAttachUploadedImageMetadata() {
        WmsItem item = new WmsItem();
        item.setId(1L);
        item.setDelFlag(0);
        ItemImageDto dto = new ItemImageDto();
        dto.setImageUrl("/api/storage/items/1/demo.png");
        dto.setObjectName("1/demo.png");
        dto.setImageName("demo.png");

        when(wmsItemMapper.selectById(1L)).thenReturn(item);
        when(wmsItemImageMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);

        ItemImageVo vo = itemService.attachImage(1L, dto);

        ArgumentCaptor<WmsItemImage> captor = ArgumentCaptor.forClass(WmsItemImage.class);
        verify(wmsItemImageMapper).insert(captor.capture());
        WmsItemImage saved = captor.getValue();
        assertEquals(1L, saved.getItemId());
        assertEquals(StorageConstants.BUCKET_ITEMS, saved.getBucket());
        assertEquals("1/demo.png", saved.getObjectName());
        assertEquals("/api/storage/items/1/demo.png", saved.getImageUrl());
        assertEquals("demo.png", vo.getImageName());
        assertEquals(3, vo.getSortOrder());
    }
}
