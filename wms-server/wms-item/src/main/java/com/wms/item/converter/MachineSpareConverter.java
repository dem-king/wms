package com.wms.item.converter;

import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsMachineSpare;
import com.wms.item.domain.vo.MachineSpareVo;
import com.wms.item.mapper.WmsItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 机器-备件关联转换器
 * 负责WmsMachineSpare实体与MachineSpareVo之间的转换
 */
@Component
@RequiredArgsConstructor
public class MachineSpareConverter {

    private final WmsItemMapper wmsItemMapper;

    /**
     * WmsMachineSpare实体转MachineSpareVo(填充备件物品信息)
     *
     * @param entity 机器-备件关联实体
     * @return 机器-备件关联VO
     */
    public MachineSpareVo toVo(WmsMachineSpare entity) {
        MachineSpareVo vo = new MachineSpareVo();
        vo.setId(entity.getId());
        vo.setMachineName(entity.getMachineName());
        vo.setMachineCode(entity.getMachineCode());
        vo.setSpareItemId(entity.getSpareItemId());
        vo.setQuantity(entity.getQuantity());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setCreateBy(entity.getCreateBy());
        if (entity.getSpareItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(entity.getSpareItemId());
            if (item != null) {
                vo.setSpareItemName(item.getItemName());
                vo.setSpareItemCode(item.getItemCode());
            }
        }
        return vo;
    }
}
