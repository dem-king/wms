package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.converter.ItemConverter;
import com.wms.item.domain.dto.ItemDto;
import com.wms.item.domain.entity.WmsCategory;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemBin;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.item.mapper.WmsElectronicLabelMapper;
import com.wms.item.mapper.WmsItemBinMapper;
import com.wms.item.mapper.WmsItemImageMapper;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsItemTagMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.item.mapper.WmsSubCategoryMapper;
import com.wms.item.mapper.WmsTagMapper;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 物品默认库位维护测试
 */
@DisplayName("ItemServiceImpl 默认库位测试")
@ExtendWith(MockitoExtension.class)
class ItemServiceImplLocationTest {

    @Mock
    private WmsItemMapper wmsItemMapper;
    @Mock
    private WmsItemTagMapper wmsItemTagMapper;
    @Mock
    private WmsItemImageMapper wmsItemImageMapper;
    @Mock
    private WmsCategoryMapper wmsCategoryMapper;
    @Mock
    private WmsSubCategoryMapper wmsSubCategoryMapper;
    @Mock
    private WmsTagMapper wmsTagMapper;
    @Mock
    private WmsItemBinMapper wmsItemBinMapper;
    @Mock
    private WmsBinMapper wmsBinMapper;
    @Mock
    private WmsCabinetMapper wmsCabinetMapper;
    @Mock
    private WmsAreaMapper wmsAreaMapper;
    @Mock
    private WmsWarehouseMapper wmsWarehouseMapper;

    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(wmsItemMapper, wmsItemTagMapper, wmsItemImageMapper,
                wmsCategoryMapper, wmsSubCategoryMapper, wmsTagMapper, null,
                new ItemConverter(), wmsItemBinMapper, wmsBinMapper, wmsCabinetMapper,
                wmsAreaMapper, wmsWarehouseMapper);
    }

    @Test
    @DisplayName("新增物品时应保存多个默认库位")
    void shouldSaveDefaultBinsWhenCreateItem() {
        ItemDto dto = createItemDto(List.of(101L, 102L));
        when(wmsCategoryMapper.selectById(1L)).thenReturn(createCategory());
        when(wmsBinMapper.selectBatchIds(any()))
                .thenReturn(List.of(createBin(101L), createBin(102L)));
        when(wmsCabinetMapper.selectBatchIds(any())).thenReturn(List.of(createCabinet()));
        when(wmsAreaMapper.selectBatchIds(any())).thenReturn(List.of(createArea()));
        when(wmsWarehouseMapper.selectBatchIds(any())).thenReturn(List.of(createWarehouse()));
        when(wmsItemBinMapper.selectAllByItemId(10L)).thenReturn(List.of());
        when(wmsItemBinMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(createItemBin(10L, 101L), createItemBin(10L, 102L)));
        doAnswer(invocation -> {
            WmsItem item = invocation.getArgument(0);
            item.setId(10L);
            return 1;
        }).when(wmsItemMapper).insert(any(WmsItem.class));

        ItemVo result = itemService.create(dto);

        assertEquals(List.of(101L, 102L), result.getBinIds());
        ArgumentCaptor<WmsItemBin> captor = ArgumentCaptor.forClass(WmsItemBin.class);
        verify(wmsItemBinMapper, org.mockito.Mockito.times(2)).insert(captor.capture());
        assertEquals(List.of(101L, 102L), captor.getAllValues().stream().map(WmsItemBin::getBinId).toList());
    }

    @Test
    @DisplayName("分页查询物品时应批量加载默认库位")
    void shouldBatchLoadDefaultBinsWhenPageItems() {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setSize(10);
        WmsItem item = createItem(10L);
        when(wmsItemMapper.selectPage(any(), any())).thenReturn(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<WmsItem>(1, 10, 1)
                .setRecords(List.of(item)));
        when(wmsCategoryMapper.selectBatchIds(any())).thenReturn(List.of(createCategory()));
        when(wmsItemBinMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(createItemBin(10L, 101L)));
        when(wmsBinMapper.selectBatchIds(any())).thenReturn(List.of(createBin(101L)));
        when(wmsCabinetMapper.selectBatchIds(any())).thenReturn(List.of(createCabinet()));
        when(wmsAreaMapper.selectBatchIds(any())).thenReturn(List.of(createArea()));
        when(wmsWarehouseMapper.selectBatchIds(any())).thenReturn(List.of(createWarehouse()));

        PageResult<ItemVo> result = itemService.page(pageParam, null, null, null, null, null);

        assertEquals(List.of(101L), result.getRecords().get(0).getBinIds());
        assertEquals("一号仓/一区/A柜/BIN-101", result.getRecords().get(0).getLocations().get(0).getLocationText());
        verify(wmsItemBinMapper).selectList(any(LambdaQueryWrapper.class));
        verify(wmsBinMapper).selectBatchIds(any());
    }

    @Test
    @DisplayName("保存不存在的库位时应抛出业务异常")
    void shouldRejectMissingBinWhenSaveDefaultBins() {
        ItemDto dto = createItemDto(List.of(404L));
        when(wmsCategoryMapper.selectById(1L)).thenReturn(createCategory());
        when(wmsBinMapper.selectBatchIds(List.of(404L))).thenReturn(List.of());

        assertThrows(BizException.class, () -> itemService.create(dto));
        verify(wmsItemMapper, never()).insert(any(WmsItem.class));
    }

    @Test
    @DisplayName("保存默认库位时应拒绝禁用库房")
    void shouldRejectDisabledWarehouseWhenSaveDefaultBins() {
        ItemDto dto = createItemDto(List.of(101L));
        WmsWarehouse disabledWarehouse = createWarehouse();
        disabledWarehouse.setStatus(BizConstants.STATUS_DISABLED);
        when(wmsCategoryMapper.selectById(1L)).thenReturn(createCategory());
        when(wmsBinMapper.selectBatchIds(List.of(101L))).thenReturn(List.of(createBin(101L)));
        when(wmsCabinetMapper.selectBatchIds(any())).thenReturn(List.of(createCabinet()));
        when(wmsAreaMapper.selectBatchIds(any())).thenReturn(List.of(createArea()));
        when(wmsWarehouseMapper.selectBatchIds(any())).thenReturn(List.of(disabledWarehouse));

        assertThrows(BizException.class, () -> itemService.create(dto));
        verify(wmsItemMapper, never()).insert(any(WmsItem.class));
    }

    @Test
    @DisplayName("编辑物品时应恢复历史库位并新增库位关联")
    void shouldRestoreAndInsertDefaultBinsWhenUpdateItem() {
        ItemDto dto = createItemDto(List.of(102L, 103L));
        WmsItem existing = createItem(10L);
        when(wmsItemMapper.selectById(10L)).thenReturn(existing);
        when(wmsCategoryMapper.selectById(1L)).thenReturn(createCategory());
        when(wmsBinMapper.selectBatchIds(any()))
                .thenReturn(List.of(createBin(102L), createBin(103L)));
        when(wmsCabinetMapper.selectBatchIds(any())).thenReturn(List.of(createCabinet()));
        when(wmsAreaMapper.selectBatchIds(any())).thenReturn(List.of(createArea()));
        when(wmsWarehouseMapper.selectBatchIds(any())).thenReturn(List.of(createWarehouse()));
        when(wmsItemBinMapper.selectAllByItemId(10L))
                .thenReturn(List.of(createItemBin(10L, 102L, 900L, DelFlagConstants.DELETED, 5)));
        when(wmsItemBinMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(createItemBin(10L, 102L), createItemBin(10L, 103L)));

        ItemVo result = itemService.update(10L, dto);

        verify(wmsItemBinMapper).restoreById(900L, 1, DelFlagConstants.NORMAL);
        ArgumentCaptor<WmsItemBin> captor = ArgumentCaptor.forClass(WmsItemBin.class);
        verify(wmsItemBinMapper).insert(captor.capture());
        assertEquals(103L, captor.getValue().getBinId());
        assertEquals(List.of(102L, 103L), result.getBinIds());
    }

    private ItemDto createItemDto(List<Long> binIds) {
        ItemDto dto = new ItemDto();
        dto.setItemName("轴承");
        dto.setCategoryId(1L);
        dto.setBinIds(binIds);
        return dto;
    }

    private WmsItem createItem(Long id) {
        WmsItem item = new WmsItem();
        item.setId(id);
        item.setItemName("轴承");
        item.setItemCode("WP001");
        item.setCategoryId(1L);
        item.setDelFlag(DelFlagConstants.NORMAL);
        return item;
    }

    private WmsCategory createCategory() {
        WmsCategory category = new WmsCategory();
        category.setId(1L);
        category.setCategoryName("备件");
        category.setDelFlag(DelFlagConstants.NORMAL);
        return category;
    }

    private WmsBin createBin(Long id) {
        WmsBin bin = new WmsBin();
        bin.setId(id);
        bin.setBinCode("BIN-" + id);
        bin.setCabinetId(201L);
        bin.setWarehouseId(401L);
        bin.setBinStatus(WarehouseConstants.BIN_STATUS_NORMAL);
        return bin;
    }

    private WmsItemBin createItemBin(Long itemId, Long binId) {
        WmsItemBin itemBin = new WmsItemBin();
        itemBin.setItemId(itemId);
        itemBin.setBinId(binId);
        return itemBin;
    }

    private WmsItemBin createItemBin(Long itemId, Long binId, Long id, Integer delFlag, Integer sortOrder) {
        WmsItemBin itemBin = createItemBin(itemId, binId);
        itemBin.setId(id);
        itemBin.setDelFlag(delFlag);
        itemBin.setSortOrder(sortOrder);
        return itemBin;
    }

    private WmsCabinet createCabinet() {
        WmsCabinet cabinet = new WmsCabinet();
        cabinet.setId(201L);
        cabinet.setAreaId(301L);
        cabinet.setWarehouseId(401L);
        cabinet.setCabinetName("A柜");
        cabinet.setStatus(BizConstants.STATUS_ENABLED);
        return cabinet;
    }

    private WmsArea createArea() {
        WmsArea area = new WmsArea();
        area.setId(301L);
        area.setAreaName("一区");
        area.setWarehouseId(401L);
        area.setStatus(BizConstants.STATUS_ENABLED);
        return area;
    }

    private WmsWarehouse createWarehouse() {
        WmsWarehouse warehouse = new WmsWarehouse();
        warehouse.setId(401L);
        warehouse.setWarehouseName("一号仓");
        warehouse.setStatus(BizConstants.STATUS_ENABLED);
        return warehouse;
    }
}
