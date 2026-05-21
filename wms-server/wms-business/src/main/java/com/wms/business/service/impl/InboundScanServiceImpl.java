package com.wms.business.service.impl;

import com.wms.business.domain.constant.OrderScanConstants;
import com.wms.business.domain.dto.InboundScanDto;
import com.wms.business.domain.vo.InboundScanResultVo;
import com.wms.business.service.InboundScanService;
import com.wms.common.exception.BizException;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.service.ElectronicLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入库扫码服务实现类
 * 复用标签扫码结果，完成重复扫描校验并生成入库建议明细
 */
@Service
@RequiredArgsConstructor
public class InboundScanServiceImpl implements InboundScanService {

    private final ElectronicLabelService electronicLabelService;

    /**
     * 解析入库扫码结果
     * 校验标签已绑定物品且未在当前单据中重复扫描
     *
     * @param dto 入库扫码请求
     * @return 入库扫码结果
     */
    @Override
    public InboundScanResultVo scan(InboundScanDto dto) {
        ElectronicLabelVo labelVo = electronicLabelService.scan(dto.getCode());
        validateRepeatedScan(dto.getCurrentLabelIds(), labelVo.getId(), "该标签已存在于当前入库单");
        validateBoundItem(labelVo);
        return buildResult(labelVo);
    }

    /**
     * 校验当前单据内是否重复扫描同一标签
     *
     * @param currentLabelIds 当前单据中已扫描的标签ID列表
     * @param labelId         当前识别到的标签ID
     * @param message         异常提示
     */
    private void validateRepeatedScan(List<Long> currentLabelIds, Long labelId, String message) {
        if (currentLabelIds != null && labelId != null && currentLabelIds.contains(labelId)) {
            throw new BizException(message);
        }
    }

    /**
     * 校验标签已绑定物品
     * 因为单据明细必须依赖物品ID生成，未绑定标签无法直接回填
     *
     * @param labelVo 标签扫码结果
     */
    private void validateBoundItem(ElectronicLabelVo labelVo) {
        if (labelVo.getItemId() == null) {
            throw new BizException("标签未绑定物品，无法生成入库明细");
        }
    }

    /**
     * 构建入库扫码结果
     *
     * @param labelVo 标签扫码结果
     * @return 入库扫码结果
     */
    private InboundScanResultVo buildResult(ElectronicLabelVo labelVo) {
        InboundScanResultVo resultVo = new InboundScanResultVo();
        resultVo.setLabelId(labelVo.getId());
        resultVo.setLabelNo(labelVo.getLabelNo());
        resultVo.setLabelStatus(labelVo.getLabelStatus());
        resultVo.setItemId(labelVo.getItemId());
        resultVo.setItemName(labelVo.getItemName());
        resultVo.setItemCode(labelVo.getItemCode());

        InboundScanResultVo.InboundDetailScanVo detailVo = new InboundScanResultVo.InboundDetailScanVo();
        detailVo.setItemId(labelVo.getItemId());
        detailVo.setQuantity(OrderScanConstants.DEFAULT_SCAN_QUANTITY);
        resultVo.setDetail(detailVo);
        return resultVo;
    }
}
