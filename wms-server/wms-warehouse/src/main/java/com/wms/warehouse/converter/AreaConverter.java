package com.wms.warehouse.converter;

import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.AreaVo;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存放区域转换器
 * WmsArea实体与AreaVo之间的转换逻辑，库房名称从预查询的Map中填充
 */
@Component
@RequiredArgsConstructor
public class AreaConverter {

    private final WmsWarehouseMapper wmsWarehouseMapper;

    /**
     * WmsArea实体转AreaVo(填充库房名称)
     *
     * @param area 区域实体
     * @param warehouseMap 库房ID到实体的映射，避免N+1查询
     * @return 区域VO
     */
    public AreaVo toVo(WmsArea area, Map<Long, WmsWarehouse> warehouseMap) {
        AreaVo vo = new AreaVo();
        vo.setId(area.getId());
        vo.setWarehouseId(area.getWarehouseId());
        vo.setAreaName(area.getAreaName());
        vo.setAreaCode(area.getAreaCode());
        vo.setAreaType(area.getAreaType());
        vo.setSortOrder(area.getSortOrder());
        vo.setStatus(area.getStatus());
        vo.setRemark(area.getRemark());
        vo.setCreateTime(area.getCreateTime());
        // 从Map中填充库房名称
        if (area.getWarehouseId() != null) {
            WmsWarehouse warehouse = warehouseMap.get(area.getWarehouseId());
            if (warehouse == null) {
                warehouse = wmsWarehouseMapper.selectById(area.getWarehouseId());
            }
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        return vo;
    }

    /**
     * 批量转换区域实体列表
     *
     * @param areas 区域实体列表
     * @param warehouseMap 库房ID到实体的映射
     * @return 区域VO列表
     */
    public List<AreaVo> toVoList(List<WmsArea> areas, Map<Long, WmsWarehouse> warehouseMap) {
        return areas.stream().map(area -> toVo(area, warehouseMap)).collect(Collectors.toList());
    }
}
