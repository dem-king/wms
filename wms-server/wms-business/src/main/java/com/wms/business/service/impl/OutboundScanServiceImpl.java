package com.wms.business.service.impl;

import com.wms.business.domain.constant.OrderScanConstants;
import com.wms.business.domain.dto.OutboundScanDto;
import com.wms.business.domain.vo.OutboundScanResultVo;
import com.wms.business.service.OutboundScanService;
import com.wms.common.enums.LabelStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.service.ElectronicLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 出库扫码服务实现类
 * 复用标签扫码结果，完成重复扫描校验、标签状态校验并生成出库建议明细
 */
@Service
@RequiredArgsConstructor
public class OutboundScanServiceImpl implements OutboundScanService {

    private final ElectronicLabelService electronicLabelService;

    /**
     * 解析出库扫码结果
     * 校验标签已绑定物品、当前单据未重复扫描且标签状态允许出库
     *
     * @param dto 出库扫码请求
     * @return 出库扫码结果
     */
    @Override
    public OutboundScanResultVo scan(OutboundScanDto dto) {
        ElectronicLabelVo labelVo = electronicLabelService.scan(dto.getCode());
        validateRepeatedScan(dto.getCurrentLabelIds(), labelVo.getId(), "该标签已存在于当前出库单");
        validateBoundItem(labelVo);
        validateLabelStatus(labelVo);
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
     * 因为出库明细必须依赖物品ID生成，未绑定标签无法直接回填
     *
     * @param labelVo 标签扫码结果
     */
    private void validateBoundItem(ElectronicLabelVo labelVo) {
        if (labelVo.getItemId() == null) {
            throw new BizException("标签未绑定物品，无法生成出库明细");
        }
    }

    /**
     * 校验标签状态是否允许出库
     * 最小可用规则仅允许在库状态的标签进入当前出库单，避免重复出库
     *
     * @param labelVo 标签扫码结果
     */
    private void validateLabelStatus(ElectronicLabelVo labelVo) {
        if (labelVo.getLabelStatus() == null || labelVo.getLabelStatus() != LabelStatusEnum.IN_STOCK.getCode()) {
            throw new BizException("该标签当前不可出库");
        }
    }

    /**
     * 构建出库扫码结果
     *
     * @param labelVo 标签扫码结果
     * @return 出库扫码结果
     */
    private OutboundScanResultVo buildResult(ElectronicLabelVo labelVo) {
        OutboundScanResultVo resultVo = new OutboundScanResultVo();
        resultVo.setLabelId(labelVo.getId());
        resultVo.setLabelNo(labelVo.getLabelNo());
        resultVo.setLabelStatus(labelVo.getLabelStatus());
        resultVo.setItemId(labelVo.getItemId());
        resultVo.setItemName(labelVo.getItemName());
        resultVo.setItemCode(labelVo.getItemCode());

        OutboundScanResultVo.OutboundDetailScanVo detailVo = new OutboundScanResultVo.OutboundDetailScanVo();
        detailVo.setItemId(labelVo.getItemId());
        detailVo.setQuantity(OrderScanConstants.DEFAULT_SCAN_QUANTITY);
        resultVo.setDetail(detailVo);
        return resultVo;
    }
}
