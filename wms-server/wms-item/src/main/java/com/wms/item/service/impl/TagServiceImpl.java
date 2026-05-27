package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.item.domain.constant.TagConstants;
import com.wms.item.domain.dto.TagDto;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemTag;
import com.wms.item.domain.entity.WmsTag;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.converter.ItemConverter;
import com.wms.item.converter.TagConverter;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsItemTagMapper;
import com.wms.item.mapper.WmsTagMapper;
import com.wms.item.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final TagConverter tagConverter;
    private final ItemConverter itemConverter;

    /**
     * 标签分页列表
     *
     * @param pageParam 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<TagVo> page(PageParam pageParam) {
        return page(pageParam.getPage(), pageParam.getSize());
    }

    /**
     * 标签分页列表
     *
     * @param page 当前页
     * @param size 每页数量
     * @return 分页结果
     */
    public PageResult<TagVo> page(int page, int size) {
        Page<WmsTag> pageData = wmsTagMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<WmsTag>().orderByDesc(WmsTag::getCreateTime)
        );

        PageResult<TagVo> result = new PageResult<>();
        result.setRecords(tagConverter.toVoList(pageData.getRecords()));
        result.setTotal(pageData.getTotal());
        result.setPage(page);
        result.setSize(size);
        return result;
    }

    /**
     * 查询所有标签列表
     * 
     * @return 标签VO列表
     */
    @Override
    public List<TagVo> listAll() {
        List<WmsTag> tags = wmsTagMapper.selectList(
                new LambdaQueryWrapper<WmsTag>()
                        .orderByDesc(WmsTag::getCreateTime)
        );
        return tags.stream().map(tagConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 新增标签
     * 默认为全局范围
     * 
     * @param dto 标签新增参数
     * @return 新增后的标签VO
     */
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
        return tagConverter.toVo(tag);
    }

    /**
     * 更新标签
     * 
     * @param id 标签ID
     * @param dto 标签更新参数
     * @return 更新后的标签VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVo update(Long id, TagDto dto) {
        WmsTag existing = wmsTagMapper.selectById(id);
        if (existing == null) {
            throw new BizException("标签不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("标签已删除");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        wmsTagMapper.updateById(existing);
        return tagConverter.toVo(existing);
    }

    /**
     * 删除标签(逻辑删除)
     * 同时逻辑删除物品与标签的关联
     * 
     * @param id 标签ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsTag existing = wmsTagMapper.selectById(id);
        if (existing == null) {
            throw new BizException("标签不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("标签已删除");
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
        List<WmsItemTag> updateItemTagList = new ArrayList<>();
        for (WmsItemTag itemTag : itemTags) {
            WmsItemTag updateItemTag = new WmsItemTag();
            updateItemTag.setId(itemTag.getId());
            updateItemTag.setDelFlag(DelFlagConstants.DELETED);
            
            updateItemTagList.add(updateItemTag);
        }
        if (!updateItemTagList.isEmpty()) {
            Db.updateBatchById(updateItemTagList);
        }
    }

    /**
     * 按标签查询关联的物品列表
     * 
     * @param tagId 标签ID
     * @return 物品VO列表
     */
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
        return items.stream().map(item -> itemConverter.toVo(item, Map.of(), Map.of())).collect(Collectors.toList());
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

}
