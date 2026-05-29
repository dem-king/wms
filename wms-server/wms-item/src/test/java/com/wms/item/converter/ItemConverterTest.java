package com.wms.item.converter;

import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.vo.ItemVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Item converter tests.
 */
@DisplayName("ItemConverter")
class ItemConverterTest {

    @Test
    @DisplayName("should expose specModel alias for item list columns")
    void shouldExposeSpecModelAlias() {
        WmsItem item = new WmsItem();
        item.setId(1L);
        item.setModel("6205-ZZ");

        ItemVo vo = new ItemConverter().toVo(item, Map.of(), Map.of());

        assertEquals("6205-ZZ", vo.getSpecModel());
    }

}
