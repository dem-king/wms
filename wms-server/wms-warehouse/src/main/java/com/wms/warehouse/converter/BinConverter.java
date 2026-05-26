package com.wms.warehouse.converter;

import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.BinVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库位转换器
 * WmsBin实体与BinVo之间的转换逻辑，存放柜信息从预查询的Map中填充
 */
@Component
public class BinConverter {

    /**
     * WmsBin实体转BinVo(填充存放柜名称和占用状态描述)
     *
     * @param bin 库位实体
     * @param cabinetMap 存放柜ID到实体的映射，避免N+1查询
     * @return 库位VO
     */
    public BinVo toVo(WmsBin bin, Map<Long, WmsCabinet> cabinetMap) {
        BinVo vo = new BinVo();
        vo.setId(bin.getId());
        vo.setCabinetId(bin.getCabinetId());
        vo.setWarehouseId(bin.getWarehouseId());
        vo.setBinCode(bin.getBinCode());
        vo.setRowNum(bin.getRowNum());
        vo.setColNum(bin.getColNum());
        vo.setIsOccupied(bin.getIsOccupied());
        // 占用状态描述
        vo.setOccupiedDesc(bin.getIsOccupied() != null && bin.getIsOccupied() == WarehouseConstants.IS_OCCUPIED_YES ? "占用" : "空闲");
        vo.setCapacity(bin.getCapacity());
        vo.setUsedCapacity(bin.getUsedCapacity());
        vo.setBinStatus(bin.getBinStatus());
        vo.setCreateTime(bin.getCreateTime());
        // 从Map中填充存放柜名称
        if (bin.getCabinetId() != null) {
            WmsCabinet cabinet = cabinetMap.get(bin.getCabinetId());
            if (cabinet != null) {
                vo.setCabinetName(cabinet.getCabinetName());
            }
        }
        return vo;
    }

    /**
     * 批量转换库位实体列表
     *
     * @param bins 库位实体列表
     * @param cabinetMap 存放柜ID到实体的映射
     * @return 库位VO列表
     */
    public List<BinVo> toVoList(List<WmsBin> bins, Map<Long, WmsCabinet> cabinetMap) {
        return bins.stream().map(bin -> toVo(bin, cabinetMap)).collect(Collectors.toList());
    }
}
