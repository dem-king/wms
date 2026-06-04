package com.wms.item.converter;

import com.wms.item.domain.entity.WmsElectronicLabel;
import com.wms.item.domain.vo.ElectronicLabelVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 电子标签转换器
 * 负责WmsElectronicLabel实体与ElectronicLabelVo之间的转换
 */
@Component
public class ElectronicLabelConverter {

    /**
     * 实体转VO(不含物品关联信息)
     *
     * @param entity 电子标签实体
     * @return 电子标签VO
     */
    public ElectronicLabelVo toVo(WmsElectronicLabel entity) {
        if (entity == null) {
            return null;
        }
        ElectronicLabelVo vo = new ElectronicLabelVo();
        vo.setId(entity.getId());
        vo.setLabelNo(entity.getLabelNo());
        vo.setLabelType(entity.getLabelType());
        vo.setItemId(entity.getItemId());
        vo.setBinId(entity.getBinId());
        vo.setBatchNo(entity.getBatchNo());
        vo.setRfidCode(entity.getRfidCode());
        vo.setQrContent(entity.getQrContent());
        vo.setBarcodeContent(entity.getBarcodeContent());
        vo.setLabelStatus(entity.getLabelStatus());
        vo.setBindType(entity.getBindType());
        vo.setPrintStatus(entity.getPrintStatus());
        vo.setBorrowTime(entity.getBorrowTime());
        vo.setBorrowerName(entity.getBorrowerName());
        vo.setExpectedReturn(entity.getExpectedReturn());
        vo.setReturnerName(entity.getReturnerName());
        vo.setReturnTime(entity.getReturnTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 实体列表转VO列表
     *
     * @param entities 电子标签实体列表
     * @return 电子标签VO列表
     */
    public List<ElectronicLabelVo> toVoList(List<WmsElectronicLabel> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream().map(this::toVo).collect(Collectors.toList());
    }
}
