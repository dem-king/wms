package com.wms.warehouse.converter;

import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.dto.LayoutElementUpdateItemDto;
import com.wms.warehouse.domain.entity.WmsLayoutElement;
import com.wms.warehouse.domain.vo.LayoutElementVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 库房布局元素转换器
 * 负责WmsLayoutElement与LayoutElementVo之间的转换，以及Dto转Entity
 */
@Component
public class LayoutElementConverter {

    /**
     * Entity转Vo
     *
     * @param entity 布局元素实体
     * @return 布局元素VO
     */
    public LayoutElementVo toVo(WmsLayoutElement entity) {
        LayoutElementVo vo = new LayoutElementVo();
        vo.setId(entity.getId());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setAreaId(entity.getAreaId());
        vo.setElementCode(entity.getElementCode());
        vo.setElementName(entity.getElementName());
        vo.setElementType(entity.getElementType());
        vo.setShapeType(entity.getShapeType());
        vo.setPositionX(entity.getPositionX());
        vo.setPositionY(entity.getPositionY());
        vo.setLayoutWidth(entity.getLayoutWidth());
        vo.setLayoutHeight(entity.getLayoutHeight());
        vo.setRotation(entity.getRotation());
        vo.setPointData(entity.getPointData());
        vo.setStyleData(entity.getStyleData());
        vo.setLabelText(entity.getLabelText());
        vo.setSortOrder(entity.getSortOrder());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * Entity列表转Vo列表
     *
     * @param entities 布局元素实体列表
     * @return 布局元素VO列表
     */
    public List<LayoutElementVo> toVoList(List<WmsLayoutElement> entities) {
        return entities.stream().map(this::toVo).collect(Collectors.toList());
    }

    /**
     * Dto转Entity（新增场景）
     *
     * @param dto 布局元素创建参数
     * @return 布局元素实体
     */
    public WmsLayoutElement toEntity(LayoutElementDto dto) {
        WmsLayoutElement entity = new WmsLayoutElement();
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setAreaId(dto.getAreaId());
        entity.setElementName(dto.getElementName());
        entity.setElementType(dto.getElementType());
        entity.setShapeType(dto.getShapeType());
        entity.setPositionX(dto.getPositionX());
        entity.setPositionY(dto.getPositionY());
        entity.setLayoutWidth(dto.getLayoutWidth());
        entity.setLayoutHeight(dto.getLayoutHeight());
        entity.setRotation(dto.getRotation());
        entity.setPointData(dto.getPointData());
        entity.setStyleData(dto.getStyleData());
        entity.setLabelText(dto.getLabelText());
        entity.setSortOrder(dto.getSortOrder());
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * UpdateItemDto属性拷贝到Entity（更新场景）
     *
     * @param dto    布局元素更新项参数
     * @param entity 待更新的实体
     */
    public void copyToEntity(LayoutElementUpdateItemDto dto, WmsLayoutElement entity) {
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setAreaId(dto.getAreaId());
        entity.setElementName(dto.getElementName());
        entity.setElementType(dto.getElementType());
        entity.setShapeType(dto.getShapeType());
        entity.setPositionX(dto.getPositionX());
        entity.setPositionY(dto.getPositionY());
        entity.setLayoutWidth(dto.getLayoutWidth());
        entity.setLayoutHeight(dto.getLayoutHeight());
        entity.setRotation(dto.getRotation());
        entity.setPointData(dto.getPointData());
        entity.setStyleData(dto.getStyleData());
        entity.setLabelText(dto.getLabelText());
        entity.setSortOrder(dto.getSortOrder());
        entity.setRemark(dto.getRemark());
    }
}