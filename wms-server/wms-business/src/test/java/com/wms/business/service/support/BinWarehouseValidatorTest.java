package com.wms.business.service.support;

import com.wms.common.constant.DelFlagConstants;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

/**
 * 库位所属库房校验测试。
 */
@DisplayName("BinWarehouseValidator tests")
@ExtendWith(MockitoExtension.class)
class BinWarehouseValidatorTest {

    @Mock
    private WmsBinMapper wmsBinMapper;

    @Mock
    private WmsCabinetMapper wmsCabinetMapper;

    @Mock
    private WmsAreaMapper wmsAreaMapper;

    @InjectMocks
    private BinWarehouseValidator validator;

    @Test
    @DisplayName("should resolve warehouse from area when bin and cabinet warehouse are empty")
    void shouldResolveWarehouseFromAreaWhenBinAndCabinetWarehouseAreEmpty() {
        WmsBin bin = new WmsBin();
        bin.setId(1001L);
        bin.setCabinetId(2001L);
        bin.setWarehouseId(null);
        bin.setDelFlag(DelFlagConstants.NORMAL);
        bin.setBinStatus(WarehouseConstants.BIN_STATUS_NORMAL);

        WmsCabinet cabinet = new WmsCabinet();
        cabinet.setId(2001L);
        cabinet.setAreaId(3001L);
        cabinet.setWarehouseId(null);

        WmsArea area = new WmsArea();
        area.setId(3001L);
        area.setWarehouseId(4001L);

        when(wmsBinMapper.selectBatchIds(anyCollection())).thenReturn(List.of(bin));
        when(wmsCabinetMapper.selectBatchIds(anyCollection())).thenReturn(List.of(cabinet));
        when(wmsAreaMapper.selectBatchIds(anyCollection())).thenReturn(List.of(area));

        assertDoesNotThrow(() -> validator.validateBelongToWarehouse(
                List.of(1001L), 4001L, "库位不属于单据库房"));
    }
}
