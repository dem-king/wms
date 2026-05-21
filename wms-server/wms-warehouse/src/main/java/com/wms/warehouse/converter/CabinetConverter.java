package com.wms.warehouse.converter;

import com.wms.common.constant.BizConstants;
import com.wms.warehouse.domain.dto.CabinetDto;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.CabinetVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 存放柜转换器
 * 负责存放柜 DTO、Entity 与 VO 之间的转换，避免在 Service 中编写重复转换逻辑
 */
@Component
public class CabinetConverter {

    /**
     * 创建存放柜实体
     *
     * @param dto 存放柜新增/编辑参数
     * @return 存放柜实体
     */
    public WmsCabinet toEntity(CabinetDto dto) {
        WmsCabinet entity = new WmsCabinet();
        copyToEntity(dto, entity);
        return entity;
    }

    /**
     * 将 DTO 属性复制到实体
     *
     * @param dto 存放柜新增/编辑参数
     * @param entity 存放柜实体
     */
    public void copyToEntity(CabinetDto dto, WmsCabinet entity) {
        entity.setAreaId(dto.getAreaId());
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setCabinetName(dto.getCabinetName());
        entity.setCabinetCode(dto.getCabinetCode());
        entity.setPositionX(dto.getPositionX());
        entity.setPositionY(dto.getPositionY());
        entity.setCabinetType(dto.getCabinetType());
        entity.setRows(dto.getRows());
        entity.setCols(dto.getCols());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
    }

    /**
     * 转换为存放柜 VO
     *
     * @param entity 存放柜实体
     * @param areaName 区域名称
     * @param itemCount 物品数量
     * @return 存放柜 VO
     */
    public CabinetVo toVo(WmsCabinet entity, String areaName, Integer itemCount) {
        CabinetVo vo = new CabinetVo();
        vo.setId(entity.getId());
        vo.setAreaId(entity.getAreaId());
        vo.setAreaName(areaName);
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setCabinetName(entity.getCabinetName());
        vo.setCabinetCode(entity.getCabinetCode());
        vo.setPositionX(entity.getPositionX());
        vo.setPositionY(entity.getPositionY());
        vo.setCabinetType(entity.getCabinetType());
        vo.setRows(entity.getRows());
        vo.setCols(entity.getCols());
        vo.setSortOrder(entity.getSortOrder() == null ? BizConstants.DEFAULT_SORT_ORDER : entity.getSortOrder());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setItemCount(itemCount);
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 批量转换为存放柜 VO 列表
     *
     * @param entities 存放柜实体列表
     * @return 存放柜 VO 列表
     */
    public List<CabinetVo> toVoList(List<WmsCabinet> entities) {
        return entities.stream()
                .map(entity -> toVo(entity, null, null))
                .toList();
    }
}
