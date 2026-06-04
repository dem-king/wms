package com.wms.item.service.impl;

import com.wms.common.enums.LabelStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.item.converter.ElectronicLabelConverter;
import com.wms.item.domain.constant.LabelConstants;
import com.wms.item.domain.dto.LabelGenerateDto;
import com.wms.item.domain.entity.WmsElectronicLabel;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemBin;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.item.mapper.WmsElectronicLabelMapper;
import com.wms.item.mapper.WmsItemBinMapper;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.item.mapper.WmsSubCategoryMapper;
import com.wms.warehouse.domain.entity.WmsBin;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 电子标签服务测试。
 */
@DisplayName("ElectronicLabelServiceImpl")
@ExtendWith(MockitoExtension.class)
class ElectronicLabelServiceImplTest {

    @Mock
    private WmsElectronicLabelMapper labelMapper;
    @Mock
    private WmsItemMapper wmsItemMapper;
    @Mock
    private WmsStockMapper wmsStockMapper;
    @Mock
    private WmsCategoryMapper wmsCategoryMapper;
    @Mock
    private WmsSubCategoryMapper wmsSubCategoryMapper;
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
    @Mock
    private SequenceGenerator sequenceGenerator;

    private ElectronicLabelServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ElectronicLabelServiceImpl(labelMapper, wmsItemMapper, wmsStockMapper,
                wmsCategoryMapper, wmsSubCategoryMapper, wmsItemBinMapper, wmsBinMapper,
                wmsCabinetMapper, wmsAreaMapper, wmsWarehouseMapper, new ElectronicLabelConverter(),
                sequenceGenerator);
    }

    @Test
    @DisplayName("generate should create one RFID label for a bin with custom prefix")
    void shouldCreateRfidLabelForBinWithCustomPrefix() {
        LabelGenerateDto dto = generateDto(11L, 101L, "RF");
        WmsItem item = item(11L);
        WmsBin bin = bin(101L);
        when(wmsItemMapper.selectById(11L)).thenReturn(item);
        when(wmsBinMapper.selectById(101L)).thenReturn(bin);
        when(wmsItemBinMapper.selectList(any())).thenReturn(List.of());
        when(wmsStockMapper.selectList(any())).thenReturn(List.of());
        when(labelMapper.selectOne(any())).thenReturn(null);
        when(sequenceGenerator.next("RF")).thenReturn("RF0001");
        when(wmsItemMapper.selectBatchIds(any())).thenReturn(List.of(item));
        when(wmsBinMapper.selectBatchIds(any())).thenReturn(List.of(bin));

        List<ElectronicLabelVo> result = service.generate(dto);

        ArgumentCaptor<WmsElectronicLabel> captor = ArgumentCaptor.forClass(WmsElectronicLabel.class);
        verify(labelMapper).insert(captor.capture());
        WmsElectronicLabel saved = captor.getValue();
        assertEquals("RF0001", saved.getLabelNo());
        assertEquals(101L, saved.getBinId());
        assertEquals(11L, saved.getItemId());
        assertEquals(LabelConstants.LABEL_TYPE_RFID, saved.getLabelType());
        assertNotNull(saved.getRfidCode());
        assertEquals("RF0001", result.get(0).getLabelNo());
        assertEquals("BIN-101", result.get(0).getBinCode());
    }

    @Test
    @DisplayName("generate should reuse an existing bin label")
    void shouldReuseExistingBinLabel() {
        LabelGenerateDto dto = generateDto(11L, 101L, "RF");
        WmsElectronicLabel existing = label(301L, 11L, 101L, LabelStatusEnum.IN_STOCK.getCode());
        when(wmsItemMapper.selectById(11L)).thenReturn(item(11L));
        when(wmsBinMapper.selectById(101L)).thenReturn(bin(101L));
        when(wmsItemBinMapper.selectList(any())).thenReturn(List.of());
        when(wmsStockMapper.selectList(any())).thenReturn(List.of());
        when(labelMapper.selectOne(any())).thenReturn(existing);
        when(wmsItemMapper.selectBatchIds(any())).thenReturn(List.of(item(11L)));
        when(wmsBinMapper.selectBatchIds(any())).thenReturn(List.of(bin(101L)));

        List<ElectronicLabelVo> result = service.generate(dto);

        verify(labelMapper, never()).insert(any(WmsElectronicLabel.class));
        assertEquals(301L, result.get(0).getId());
        assertEquals(101L, result.get(0).getBinId());
    }

    @Test
    @DisplayName("ensureRfidLabelsForItemBins should reject bins assigned to another item")
    void shouldRejectBinAssignedToAnotherItem() {
        WmsItemBin itemBin = new WmsItemBin();
        itemBin.setItemId(22L);
        itemBin.setBinId(101L);
        when(wmsItemBinMapper.selectList(any())).thenReturn(List.of(itemBin));

        assertThrows(BizException.class, () -> service.ensureRfidLabelsForItemBins(11L, List.of(101L)));
        verify(labelMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("markBorrowed should update returned bin label to in use")
    void shouldMarkReturnedLabelBorrowed() {
        WmsElectronicLabel label = label(301L, 11L, 101L, LabelStatusEnum.RETURNED.getCode());
        label.setReturnerName("old");
        label.setReturnTime(LocalDateTime.now().minusDays(1));
        LocalDateTime expectedReturn = LocalDateTime.now().plusDays(7);
        when(labelMapper.selectById(301L)).thenReturn(label);

        service.markBorrowed(301L, null, "Alice", expectedReturn);

        ArgumentCaptor<WmsElectronicLabel> captor = ArgumentCaptor.forClass(WmsElectronicLabel.class);
        verify(labelMapper).updateById(captor.capture());
        WmsElectronicLabel updated = captor.getValue();
        assertEquals(LabelStatusEnum.IN_USE.getCode(), updated.getLabelStatus());
        assertEquals("Alice", updated.getBorrowerName());
        assertEquals(expectedReturn, updated.getExpectedReturn());
        assertNotNull(updated.getBorrowTime());
        assertNull(updated.getReturnerName());
        assertNull(updated.getReturnTime());
    }

    @Test
    @DisplayName("markReturned should find label by bin id and write returner")
    void shouldMarkReturnedByBinId() {
        WmsElectronicLabel label = label(301L, 11L, 101L, LabelStatusEnum.IN_USE.getCode());
        when(labelMapper.selectOne(any())).thenReturn(label);

        service.markReturned(null, 101L, "Bob");

        ArgumentCaptor<WmsElectronicLabel> captor = ArgumentCaptor.forClass(WmsElectronicLabel.class);
        verify(labelMapper).updateById(captor.capture());
        WmsElectronicLabel updated = captor.getValue();
        assertEquals(LabelStatusEnum.RETURNED.getCode(), updated.getLabelStatus());
        assertEquals("Bob", updated.getReturnerName());
        assertNotNull(updated.getReturnTime());
    }

    private LabelGenerateDto generateDto(Long itemId, Long binId, String prefix) {
        LabelGenerateDto dto = new LabelGenerateDto();
        dto.setItemId(itemId);
        dto.setBinId(binId);
        dto.setLabelPrefix(prefix);
        dto.setCount(LabelConstants.BIND_TYPE_SINGLE);
        dto.setLabelType(LabelConstants.LABEL_TYPE_RFID);
        dto.setBindType(LabelConstants.BIND_TYPE_SINGLE);
        return dto;
    }

    private WmsElectronicLabel label(Long id, Long itemId, Long binId, Integer status) {
        WmsElectronicLabel label = new WmsElectronicLabel();
        label.setId(id);
        label.setLabelNo("BQ001");
        label.setLabelType(LabelConstants.LABEL_TYPE_RFID);
        label.setItemId(itemId);
        label.setBinId(binId);
        label.setBindType(LabelConstants.BIND_TYPE_SINGLE);
        label.setLabelStatus(status);
        label.setPrintStatus(LabelConstants.PRINT_STATUS_NOT);
        label.setRfidCode("RFID001");
        return label;
    }

    private WmsItem item(Long id) {
        WmsItem item = new WmsItem();
        item.setId(id);
        item.setItemName("Bearing");
        item.setItemCode("WP001");
        return item;
    }

    private WmsBin bin(Long id) {
        WmsBin bin = new WmsBin();
        bin.setId(id);
        bin.setBinCode("BIN-" + id);
        return bin;
    }
}
