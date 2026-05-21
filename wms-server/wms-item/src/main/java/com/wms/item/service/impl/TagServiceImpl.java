package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.item.domain.constant.TagConstants;
import com.wms.item.domain.dto.TagDto;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemTag;
import com.wms.item.domain.entity.WmsTag;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsItemTagMapper;
import com.wms.item.mapper.WmsTagMapper;
import com.wms.item.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 * 处理标签CRUD和按标签筛选物品的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final WmsTagMapper wmsTagMapper;
    private final WmsItemTagMapper wmsItemTagMapper;
    private final WmsItemMapper wmsItemMapper;

    @Override
    public List<TagVo> listAll() {
        List<WmsTag> tags = wmsTagMapper.selectList(
                new LambdaQueryWrapper<WmsTag>()
                        .orderByDesc(WmsTag::getCreateTime)
        );
        return tags.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVo create(TagDto dto) {
        WmsTag tag = new WmsTag();
        copyDtoToEntity(dto, tag);
        // 默认全局范围
        if (tag.getScopeType() == null) {
            tag.setScopeType(TagConstants.SCOPE_TYPE_GLOBAL);
        }
        wmsTagMapper.insert(tag);
        return toVo(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVo update(Long id, TagDto dto) {
        WmsTag existing = wmsTagMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("标签不存在");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        wmsTagMapper.updateById(existing);
        return toVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsTag existing = wmsTagMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("标签不存在");
        }
        // 逻辑删除标签
        WmsTag updateEntity = new WmsTag();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsTagMapper.updateById(updateEntity);
        // 逻辑删除物品与该标签的关联
        List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .eq(WmsItemTag::getTagId, id)
        );
        for (WmsItemTag itemTag : itemTags) {
            WmsItemTag updateItemTag = new WmsItemTag();
            updateItemTag.setId(itemTag.getId());
            updateItemTag.setDelFlag(DelFlagConstants.DELETED);
            
            wmsItemTagMapper.updateById(updateItemTag);
        }
    }

    @Override
    public List<ItemVo> getItemsByTag(Long tagId) {
        // 查询该标签关联的物品ID列表
        List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .eq(WmsItemTag::getTagId, tagId)
        );
        List<Long> itemIds = itemTags.stream().map(WmsItemTag::getItemId).collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            return List.of();
        }
        // 查询物品列表
        List<WmsItem> items = wmsItemMapper.selectList(
                new LambdaQueryWrapper<WmsItem>()
                        .in(WmsItem::getId, itemIds)
                        .orderByDesc(WmsItem::getCreateTime)
        );
        return items.stream().map(this::toItemVo).collect(Collectors.toList());
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(TagDto dto, WmsTag entity) {
        entity.setTagName(dto.getTagName());
        entity.setTagColor(dto.getTagColor());
        entity.setTagDesc(dto.getTagDesc());
        entity.setScopeType(dto.getScopeType());
        entity.setScopeId(dto.getScopeId());
    }

    /**
     * WmsTag实体转TagVo
     */
    private TagVo toVo(WmsTag tag) {
        TagVo vo = new TagVo();
        vo.setId(tag.getId());
        vo.setTagName(tag.getTagName());
        vo.setTagColor(tag.getTagColor());
        vo.setTagDesc(tag.getTagDesc());
        vo.setScopeType(tag.getScopeType());
        vo.setScopeId(tag.getScopeId());
        vo.setCreateTime(tag.getCreateTime());
        return vo;
    }

    /**
     * WmsItem实体转ItemVo(基本信息)
     */
    private ItemVo toItemVo(WmsItem item) {
        ItemVo vo = new ItemVo();
        vo.setId(item.getId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setPinyin(item.getPinyin());
        vo.setModel(item.getModel());
        vo.setSpec(item.getSpec());
        vo.setUnit(item.getUnit());
        vo.setBrand(item.getBrand());
        vo.setCategoryId(item.getCategoryId());
        vo.setSubCategoryId(item.getSubCategoryId());
        vo.setSupplierId(item.getSupplierId());
        vo.setStatus(item.getStatus());
        vo.setIsConsumable(item.getIsConsumable());
        vo.setIsReturnable(item.getIsReturnable());
        vo.setPurchasePrice(item.getPurchasePrice());
        vo.setStockQty(item.getStockQty());
        vo.setStockLowerLimit(item.getStockLowerLimit());
        vo.setStockUpperLimit(item.getStockUpperLimit());
        vo.setReplenishThreshold(item.getReplenishThreshold());
        vo.setIdleDays(item.getIdleDays());
        vo.setRemark(item.getRemark());
        vo.setCreateTime(item.getCreateTime());
        return vo;
    }
}
