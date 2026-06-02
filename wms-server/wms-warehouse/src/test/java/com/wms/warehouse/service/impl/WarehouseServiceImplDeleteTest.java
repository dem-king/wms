package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.storage.StorageStrategy;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.converter.WarehouseConverter;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * WarehouseServiceImpl 删除逻辑回归测试。
 * 验证逻辑删除字段通过显式 SET 更新，避免 @TableLogic 字段被 updateById 过滤。
 */
@DisplayName("WarehouseServiceImpl 删除逻辑测试")
@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplDeleteTest {

    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;

    @Mock
    private SequenceGenerator sequenceGenerator;

    @Mock
    private StorageStrategy storageStrategy;

    private WarehouseServiceImpl warehouseService;

    @BeforeEach
    void setUp() {
        warehouseService = new WarehouseServiceImpl(
                wmsWarehouseMapper,
                sequenceGenerator,
                new WarehouseConverter(),
                storageStrategy
        );
    }

    @Test
    @DisplayName("删除库房时应显式更新 del_flag 而不是调用 updateById")
    void shouldSetDelFlagWithUpdateWrapperWhenDeletingWarehouse() {
        WmsWarehouse existing = new WmsWarehouse();
        existing.setId(1001L);
        existing.setDelFlag(DelFlagConstants.NORMAL);
        when(wmsWarehouseMapper.selectById(1001L)).thenReturn(existing);

        warehouseService.delete(1001L);

        ArgumentCaptor<WmsWarehouse> entityCaptor = ArgumentCaptor.forClass(WmsWarehouse.class);
        ArgumentCaptor<UpdateWrapper<WmsWarehouse>> wrapperCaptor = ArgumentCaptor.forClass(UpdateWrapper.class);
        verify(wmsWarehouseMapper).update(entityCaptor.capture(), wrapperCaptor.capture());
        verify(wmsWarehouseMapper, never()).updateById(any(WmsWarehouse.class));
        assertEquals(1001L, entityCaptor.getValue().getId());
        assertTrue(wrapperCaptor.getValue().getSqlSet().contains("del_flag"));
        assertEquals(DelFlagConstants.DELETED, wrapperCaptor.getValue().getParamNameValuePairs().get("MPGENVAL1"));
    }
}
