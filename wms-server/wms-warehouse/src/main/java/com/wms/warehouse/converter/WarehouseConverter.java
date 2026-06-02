package com.wms.warehouse.converter;

import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.WarehouseVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 库房转换器
 * WmsWarehouse实体与WarehouseVo之间的转换逻辑
 */
@Component
public class WarehouseConverter {

    /**
     * WmsWarehouse实体转WarehouseVo
     *
     * @param warehouse 库房实体
     * @return 库房VO
     */
    public WarehouseVo toVo(WmsWarehouse warehouse) {
        WarehouseVo vo = new WarehouseVo();
        vo.setId(warehouse.getId());
        vo.setWarehouseName(warehouse.getWarehouseName());
        vo.setWarehouseCode(warehouse.getWarehouseCode());
        vo.setAddress(warehouse.getAddress());
        vo.setManager(warehouse.getManager());
        vo.setPhone(warehouse.getPhone());
        vo.setArea(warehouse.getArea());
        vo.setStatus(warehouse.getStatus());
        vo.setRemark(warehouse.getRemark());
        vo.setCreateTime(warehouse.getCreateTime());
        vo.setUpdateTime(warehouse.getUpdateTime());
        vo.setLayoutWidth(warehouse.getLayoutWidth());
        vo.setLayoutHeight(warehouse.getLayoutHeight());
        vo.setLayoutScale(warehouse.getLayoutScale());
        vo.setLayoutBackgroundVersion(warehouse.getLayoutBackgroundVersion());
        return vo;
    }

    /**
     * 批量转换库房实体列表
     *
     * @param warehouses 库房实体列表
     * @return 库房VO列表
     */
    public List<WarehouseVo> toVoList(List<WmsWarehouse> warehouses) {
        return warehouses.stream().map(this::toVo).collect(Collectors.toList());
    }
}
