package com.wms.item.service.impl;

import com.wms.item.converter.ItemConverter;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemImage;
import com.wms.item.domain.vo.ItemImageVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Item 模块架构测试
 * 验证单条 ItemVo 转换职责收敛到 Converter，而非保留在 ServiceImpl 中。
 */
@DisplayName("Item 模块架构测试")
class ItemArchitectureTest {

    @Test
    @DisplayName("ItemServiceImpl 不应保留私有 toItemVo 方法")
    void shouldNotKeepPrivateToItemVoInsideServiceImpl() {
        boolean exists = Arrays.stream(ItemServiceImpl.class.getDeclaredMethods())
                .map(Method::getName)
                .anyMatch("toItemVo"::equals);

        assertFalse(exists);
    }

    @Test
    @DisplayName("ItemConverter 应提供单条 ItemVo 转换入口")
    void shouldProvideSingleItemConverterEntry() {
        boolean exists = Arrays.stream(ItemConverter.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("toVo")
                        && Arrays.equals(method.getParameterTypes(),
                        new Class<?>[]{WmsItem.class, String.class, String.class}));

        assertTrue(exists);
    }

    @Test
    @DisplayName("ItemConverter 转换图片时应保留 imageName")
    void shouldKeepImageNameWhenConvertImageVo() {
        ItemConverter converter = new ItemConverter();
        WmsItemImage image = new WmsItemImage();
        image.setId(1L);
        image.setItemId(2L);
        image.setImageUrl("/storage/items/demo.png");
        image.setImageName("demo.png");
        image.setSortOrder(1);

        ItemImageVo vo = converter.toImageVo(image);

        assertEquals("demo.png", vo.getImageName());
    }
}
