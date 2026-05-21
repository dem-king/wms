package com.wms.business.service.impl;

import com.wms.business.domain.dto.OutboundScanDto;
import com.wms.business.domain.vo.OutboundScanResultVo;
import com.wms.common.enums.LabelStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.service.ElectronicLabelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 出库扫码服务单元测试
 * 验证扫码结果复用、重复扫描校验和出库状态校验
 */
@DisplayName("OutboundScanServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class OutboundScanServiceImplTest {

    @Mock
    private ElectronicLabelService electronicLabelService;

    @InjectMocks
    private OutboundScanServiceImpl outboundScanService;

    @Test
    @DisplayName("标签状态允许出库时应返回出库建议明细")
    void shouldReturnOutboundScanResultWhenLabelCanOutbound() {
        OutboundScanDto dto = new OutboundScanDto();
        dto.setCode("LBL-OUT-001");
        ElectronicLabelVo labelVo = buildLabelVo(301L, "LBL-OUT-001", LabelStatusEnum.IN_STOCK.getCode(), 2001L);
        when(electronicLabelService.scan("LBL-OUT-001")).thenReturn(labelVo);

        OutboundScanResultVo result = outboundScanService.scan(dto);

        assertEquals(301L, result.getLabelId());
        assertEquals("LBL-OUT-001", result.getLabelNo());
        assertEquals(2001L, result.getItemId());
        assertEquals(2001L, result.getDetail().getItemId());
        assertEquals(1, result.getDetail().getQuantity());
        verify(electronicLabelService).scan("LBL-OUT-001");
    }

    @Test
    @DisplayName("重复扫描同一标签时应抛出业务异常")
    void shouldThrowWhenOutboundLabelAlreadyScanned() {
        OutboundScanDto dto = new OutboundScanDto();
        dto.setCode("LBL-OUT-002");
        dto.setCurrentLabelIds(List.of(302L));
        ElectronicLabelVo labelVo = buildLabelVo(302L, "LBL-OUT-002", LabelStatusEnum.IN_STOCK.getCode(), 2002L);
        when(electronicLabelService.scan("LBL-OUT-002")).thenReturn(labelVo);

        BizException exception = assertThrows(BizException.class, () -> outboundScanService.scan(dto));

        assertEquals("该标签已存在于当前出库单", exception.getMessage());
    }

    @Test
    @DisplayName("标签状态不允许出库时应抛出业务异常")
    void shouldThrowWhenLabelStatusCannotOutbound() {
        OutboundScanDto dto = new OutboundScanDto();
        dto.setCode("LBL-OUT-003");
        ElectronicLabelVo labelVo = buildLabelVo(303L, "LBL-OUT-003", LabelStatusEnum.IN_USE.getCode(), 2003L);
        when(electronicLabelService.scan("LBL-OUT-003")).thenReturn(labelVo);

        BizException exception = assertThrows(BizException.class, () -> outboundScanService.scan(dto));

        assertEquals("该标签当前不可出库", exception.getMessage());
    }

    private ElectronicLabelVo buildLabelVo(Long labelId, String labelNo, Integer labelStatus, Long itemId) {
        ElectronicLabelVo labelVo = new ElectronicLabelVo();
        labelVo.setId(labelId);
        labelVo.setLabelNo(labelNo);
        labelVo.setLabelStatus(labelStatus);
        labelVo.setItemId(itemId);
        labelVo.setItemName("测试物品");
        labelVo.setItemCode("ITEM-002");
        return labelVo;
    }
}
