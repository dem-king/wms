package com.wms.business.service.impl;

import com.wms.business.domain.dto.InboundScanDto;
import com.wms.business.domain.vo.InboundScanResultVo;
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
 * 入库扫码服务单元测试
 * 验证扫码结果复用、重复扫描校验和明细组装行为
 */
@DisplayName("InboundScanServiceImpl 测试")
@ExtendWith(MockitoExtension.class)
class InboundScanServiceImplTest {

    @Mock
    private ElectronicLabelService electronicLabelService;

    @InjectMocks
    private InboundScanServiceImpl inboundScanService;

    @Test
    @DisplayName("扫码成功时应返回入库建议明细")
    void shouldReturnInboundScanResultWhenScanSucceeds() {
        InboundScanDto dto = new InboundScanDto();
        dto.setCode("LBL-IN-001");
        ElectronicLabelVo labelVo = buildLabelVo(101L, "LBL-IN-001", LabelStatusEnum.RETURNED.getCode(), 1001L);
        when(electronicLabelService.scan("LBL-IN-001")).thenReturn(labelVo);

        InboundScanResultVo result = inboundScanService.scan(dto);

        assertEquals(101L, result.getLabelId());
        assertEquals("LBL-IN-001", result.getLabelNo());
        assertEquals(1001L, result.getItemId());
        assertEquals(1001L, result.getDetail().getItemId());
        assertEquals(1, result.getDetail().getQuantity());
        verify(electronicLabelService).scan("LBL-IN-001");
    }

    @Test
    @DisplayName("重复扫描同一标签时应抛出业务异常")
    void shouldThrowWhenInboundLabelAlreadyScanned() {
        InboundScanDto dto = new InboundScanDto();
        dto.setCode("LBL-IN-002");
        dto.setCurrentLabelIds(List.of(202L));
        ElectronicLabelVo labelVo = buildLabelVo(202L, "LBL-IN-002", LabelStatusEnum.IN_STOCK.getCode(), 1002L);
        when(electronicLabelService.scan("LBL-IN-002")).thenReturn(labelVo);

        BizException exception = assertThrows(BizException.class, () -> inboundScanService.scan(dto));

        assertEquals("该标签已存在于当前入库单", exception.getMessage());
    }

    private ElectronicLabelVo buildLabelVo(Long labelId, String labelNo, Integer labelStatus, Long itemId) {
        ElectronicLabelVo labelVo = new ElectronicLabelVo();
        labelVo.setId(labelId);
        labelVo.setLabelNo(labelNo);
        labelVo.setLabelStatus(labelStatus);
        labelVo.setItemId(itemId);
        labelVo.setItemName("测试物品");
        labelVo.setItemCode("ITEM-001");
        return labelVo;
    }
}
