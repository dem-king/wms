package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.util.PinyinUtil;
import com.wms.item.domain.dto.ItemDto;
import com.wms.item.domain.entity.*;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.mapper.*;
import com.wms.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 物品服务实现类
 * 处理物品CRUD、分页查询、图片管理、快速搜索等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final WmsItemMapper wmsItemMapper;
    private final WmsItemTagMapper wmsItemTagMapper;
    private final WmsItemImageMapper wmsItemImageMapper;
    private final WmsCategoryMapper wmsCategoryMapper;
    private final WmsSubCategoryMapper wmsSubCategoryMapper;
    private final WmsTagMapper wmsTagMapper;

    /** 物品编号前缀 */
    private static final String ITEM_CODE_PREFIX = "WP";

    @Override
    public PageResult<ItemVo> page(PageParam pageParam, Long categoryId, Long subCategoryId,
                                   Long tagId, Integer status, String keyword) {
        LambdaQueryWrapper<WmsItem> wrapper = new LambdaQueryWrapper<WmsItem>();
        // 按主类目筛选
        if (categoryId != null) {
            wrapper.eq(WmsItem::getCategoryId, categoryId);
        }
        // 按细分类目筛选
        if (subCategoryId != null) {
            wrapper.eq(WmsItem::getSubCategoryId, subCategoryId);
        }
        // 按状态筛选
        if (status != null) {
            wrapper.eq(WmsItem::getStatus, status);
        }
        // 按关键字模糊搜索(名称/编号/型号/拼音)
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(WmsItem::getItemName, keyword)
                    .or().like(WmsItem::getItemCode, keyword)
                    .or().like(WmsItem::getModel, keyword)
                    .or().like(WmsItem::getPinyin, keyword)
            );
        }
        // 按标签筛选: 先查关联的物品ID
        if (tagId != null) {
            List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                    new LambdaQueryWrapper<WmsItemTag>()
                            .eq(WmsItemTag::getTagId, tagId)
            );
            List<Long> itemIds = itemTags.stream().map(WmsItemTag::getItemId).collect(Collectors.toList());
            if (itemIds.isEmpty()) {
                // 无匹配直接返回空结果
                PageResult<ItemVo> result = new PageResult<>();
                result.setRecords(List.of());
                result.setTotal(0L);
                result.setPage(pageParam.getPage());
                result.setSize(pageParam.getSize());
                return result;
            }
            wrapper.in(WmsItem::getId, itemIds);
        }
        wrapper.orderByDesc(WmsItem::getCreateTime);

        Page<WmsItem> page = wmsItemMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        // 批量查询所有涉及的类目和细分类目，避免N+1查询
        List<WmsItem> items = page.getRecords();
        Map<Long, String> categoryNameMap = items.stream()
                .map(WmsItem::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> {
                    WmsCategory c = wmsCategoryMapper.selectById(id);
                    return c != null ? c.getCategoryName() : null;
                }));
        Map<Long, String> subCategoryNameMap = items.stream()
                .map(WmsItem::getSubCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> {
                    WmsSubCategory sc = wmsSubCategoryMapper.selectById(id);
                    return sc != null ? sc.getSubCategoryName() : null;
                }));

        PageResult<ItemVo> result = new PageResult<>();
        result.setRecords(items.stream().map(item -> toItemVo(item, categoryNameMap, subCategoryNameMap)).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    @Override
    public ItemVo getById(Long id) {
        WmsItem item = wmsItemMapper.selectById(id);
        if (item == null || item.getDelFlag() == 1) {
            throw new BizException("物品不存在");
        }
        ItemVo vo = toItemVo(item);
        // 填充标签列表
        vo.setTags(getItemTags(id));
        // 填充图片列表
        vo.setImages(getItemImages(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemVo create(ItemDto dto) {
        // 校验主类目存在
        WmsCategory category = wmsCategoryMapper.selectById(dto.getCategoryId());
        if (category == null || category.getDelFlag() == 1) {
            throw new BizException("主类目不存在");
        }
        // 校验细分类目存在(如果指定)
        if (dto.getSubCategoryId() != null) {
            WmsSubCategory subCategory = wmsSubCategoryMapper.selectById(dto.getSubCategoryId());
            if (subCategory == null || subCategory.getDelFlag() == 1) {
                throw new BizException("细分类目不存在");
            }
        }
        WmsItem item = new WmsItem();
        copyDtoToEntity(dto, item);
        // 自动生成物品编号: WP + 年月日 + 4位流水号
        item.setItemCode(generateItemCode());
        // 自动生成拼音首字母
        if (dto.getItemName() != null) {
            item.setPinyin(PinyinUtil.toFirstChar(dto.getItemName()));
        }
        // 默认状态为在库
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        if (item.getIsConsumable() == null) {
            item.setIsConsumable(0);
        }
        if (item.getIsReturnable() == null) {
            item.setIsReturnable(1);
        }
        if (item.getPurchasePrice() == null) {
            item.setPurchasePrice(BigDecimal.ZERO);
        }
        if (item.getStockQty() == null) {
            item.setStockQty(0);
        }
        wmsItemMapper.insert(item);
        // 保存物品标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveItemTags(item.getId(), dto.getTagIds());
        }
        return toItemVo(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemVo update(Long id, ItemDto dto) {
        WmsItem existing = wmsItemMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("物品不存在");
        }
        // 校验主类目存在
        WmsCategory category = wmsCategoryMapper.selectById(dto.getCategoryId());
        if (category == null || category.getDelFlag() == 1) {
            throw new BizException("主类目不存在");
        }
        // 校验细分类目存在(如果指定)
        if (dto.getSubCategoryId() != null) {
            WmsSubCategory subCategory = wmsSubCategoryMapper.selectById(dto.getSubCategoryId());
            if (subCategory == null || subCategory.getDelFlag() == 1) {
                throw new BizException("细分类目不存在");
            }
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编号和拼音
        existing.setItemCode(null);
        existing.setPinyin(null);
        wmsItemMapper.updateById(existing);
        // 如果dto中包含tagIds则同步更新物品标签关联
        if (dto.getTagIds() != null) {
            assignTags(id, dto.getTagIds());
        }
        return toItemVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsItem existing = wmsItemMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("物品不存在");
        }
        // 逻辑删除物品
        WmsItem updateEntity = new WmsItem();
        updateEntity.setId(id);
        updateEntity.setDelFlag(1);
        updateEntity.setLastOperType("d");
        wmsItemMapper.updateById(updateEntity);
        // 逻辑删除物品标签关联
        List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .eq(WmsItemTag::getItemId, id)
        );
        for (WmsItemTag itemTag : itemTags) {
            WmsItemTag updateItemTag = new WmsItemTag();
            updateItemTag.setId(itemTag.getId());
            updateItemTag.setDelFlag(1);
            updateItemTag.setLastOperType("d");
            wmsItemTagMapper.updateById(updateItemTag);
        }
        // 逻辑删除物品图片
        List<WmsItemImage> images = wmsItemImageMapper.selectList(
                new LambdaQueryWrapper<WmsItemImage>()
                        .eq(WmsItemImage::getItemId, id)
        );
        for (WmsItemImage image : images) {
            WmsItemImage updateImage = new WmsItemImage();
            updateImage.setId(image.getId());
            updateImage.setDelFlag(1);
            updateImage.setLastOperType("d");
            wmsItemImageMapper.updateById(updateImage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemImageVo uploadImage(Long itemId, MultipartFile file) {
        // 校验物品存在
        WmsItem item = wmsItemMapper.selectById(itemId);
        if (item == null || item.getDelFlag() == 1) {
            throw new BizException("物品不存在");
        }
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        // 生成MinIO存储URL(实际项目中应调用MinIO客户端上传)
        String imageUrl = "/wms/items/" + itemId + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        // 保存图片记录
        WmsItemImage image = new WmsItemImage();
        image.setItemId(itemId);
        image.setImageUrl(imageUrl);
        image.setImageName(file.getOriginalFilename());
        // 排序号: 当前最大+1
        Long maxSort = wmsItemImageMapper.selectCount(
                new LambdaQueryWrapper<WmsItemImage>()
                        .eq(WmsItemImage::getItemId, itemId)
        );
        image.setSortOrder(maxSort.intValue() + 1);
        wmsItemImageMapper.insert(image);
        return toImageVo(image);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long itemId, Long imageId) {
        WmsItemImage image = wmsItemImageMapper.selectById(imageId);
        if (image == null || image.getDelFlag() == 1) {
            throw new BizException("图片不存在");
        }
        if (!image.getItemId().equals(itemId)) {
            throw new BizException("图片不属于该物品");
        }
        WmsItemImage updateEntity = new WmsItemImage();
        updateEntity.setId(imageId);
        updateEntity.setDelFlag(1);
        updateEntity.setLastOperType("d");
        wmsItemImageMapper.updateById(updateEntity);
    }

    @Override
    public List<ItemVo> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        LambdaQueryWrapper<WmsItem> wrapper = new LambdaQueryWrapper<WmsItem>()
                .and(w -> w
                        .like(WmsItem::getItemName, keyword)
                        .or().like(WmsItem::getPinyin, keyword)
                        .or().like(WmsItem::getModel, keyword)
                        .or().like(WmsItem::getItemCode, keyword)
                )
                .orderByDesc(WmsItem::getCreateTime)
                .last("LIMIT 50");
        List<WmsItem> items = wmsItemMapper.selectList(wrapper);
        return items.stream().map(this::toItemVo).collect(Collectors.toList());
    }

    /**
     * 生成物品编号: WP + 年月日 + 4位流水号
     * 示例: WP202605140001
     */
    private String generateItemCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 查询当天最大编号
        LambdaQueryWrapper<WmsItem> wrapper = new LambdaQueryWrapper<WmsItem>()
                .likeRight(WmsItem::getItemCode, ITEM_CODE_PREFIX + datePart)
                .orderByDesc(WmsItem::getItemCode)
                .last("LIMIT 1");
        WmsItem lastItem = wmsItemMapper.selectOne(wrapper);
        int seq = 1;
        if (lastItem != null && lastItem.getItemCode() != null) {
            String lastCode = lastItem.getItemCode();
            String seqStr = lastCode.substring(lastCode.length() - 4);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return ITEM_CODE_PREFIX + datePart + String.format("%04d", seq);
    }

    /**
     * 批量保存物品标签关联
     *
     * @param itemId 物品ID
     * @param tagIds 标签ID列表
     */
    private void saveItemTags(Long itemId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            WmsItemTag itemTag = new WmsItemTag();
            itemTag.setItemId(itemId);
            itemTag.setTagId(tagId);
            wmsItemTagMapper.insert(itemTag);
        }
    }

    /**
     * 分配物品标签(先删后增)
     *
     * @param itemId 物品ID
     * @param tagIds 标签ID列表
     */
    private void assignTags(Long itemId, List<Long> tagIds) {
        // 先逻辑删除旧的物品标签关联
        List<WmsItemTag> oldItemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .eq(WmsItemTag::getItemId, itemId)
        );
        for (WmsItemTag oldTag : oldItemTags) {
            WmsItemTag updateTag = new WmsItemTag();
            updateTag.setId(oldTag.getId());
            updateTag.setDelFlag(1);
            updateTag.setLastOperType("d");
            wmsItemTagMapper.updateById(updateTag);
        }
        // 批量插入新的物品标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            saveItemTags(itemId, tagIds);
        }
    }

    /**
     * 获取物品的标签列表
     *
     * @param itemId 物品ID
     * @return 标签VO列表
     */
    private List<TagVo> getItemTags(Long itemId) {
        List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .eq(WmsItemTag::getItemId, itemId)
        );
        List<Long> tagIds = itemTags.stream().map(WmsItemTag::getTagId).collect(Collectors.toList());
        if (tagIds.isEmpty()) {
            return List.of();
        }
        List<WmsTag> tags = wmsTagMapper.selectList(
                new LambdaQueryWrapper<WmsTag>()
                        .in(WmsTag::getId, tagIds)
        );
        return tags.stream().map(tag -> {
            TagVo vo = new TagVo();
            vo.setId(tag.getId());
            vo.setTagName(tag.getTagName());
            vo.setTagColor(tag.getTagColor());
            vo.setTagDesc(tag.getTagDesc());
            vo.setScopeType(tag.getScopeType());
            vo.setScopeId(tag.getScopeId());
            vo.setCreateTime(tag.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取物品的图片列表
     *
     * @param itemId 物品ID
     * @return 图片VO列表
     */
    private List<ItemImageVo> getItemImages(Long itemId) {
        List<WmsItemImage> images = wmsItemImageMapper.selectList(
                new LambdaQueryWrapper<WmsItemImage>()
                        .eq(WmsItemImage::getItemId, itemId)
                        .orderByAsc(WmsItemImage::getSortOrder)
        );
        return images.stream().map(this::toImageVo).collect(Collectors.toList());
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(ItemDto dto, WmsItem entity) {
        entity.setItemName(dto.getItemName());
        entity.setModel(dto.getModel());
        entity.setSpec(dto.getSpec());
        entity.setUnit(dto.getUnit());
        entity.setBrand(dto.getBrand());
        entity.setCategoryId(dto.getCategoryId());
        entity.setSubCategoryId(dto.getSubCategoryId());
        entity.setSupplierId(dto.getSupplierId());
        entity.setStatus(dto.getStatus());
        entity.setIsConsumable(dto.getIsConsumable());
        entity.setIsReturnable(dto.getIsReturnable());
        entity.setPurchasePrice(dto.getPurchasePrice());
        entity.setStockQty(dto.getStockQty());
        entity.setStockLowerLimit(dto.getStockLowerLimit());
        entity.setStockUpperLimit(dto.getStockUpperLimit());
        entity.setReplenishThreshold(dto.getReplenishThreshold());
        entity.setIdleDays(dto.getIdleDays());
        entity.setRemark(dto.getRemark());
    }

    /**
     * WmsItem实体转ItemVo(单条查询时使用，内部查DB填充类目名称)
     *
     * @param item 物品实体
     * @return 物品VO
     */
    private ItemVo toItemVo(WmsItem item) {
        Map<Long, String> categoryNameMap = item.getCategoryId() != null
                ? Map.of(item.getCategoryId(),
                Optional.ofNullable(wmsCategoryMapper.selectById(item.getCategoryId()))
                        .map(WmsCategory::getCategoryName).orElse(null))
                : Map.of();
        Map<Long, String> subCategoryNameMap = item.getSubCategoryId() != null
                ? Map.of(item.getSubCategoryId(),
                Optional.ofNullable(wmsSubCategoryMapper.selectById(item.getSubCategoryId()))
                        .map(WmsSubCategory::getSubCategoryName).orElse(null))
                : Map.of();
        return toItemVo(item, categoryNameMap, subCategoryNameMap);
    }

    /**
     * WmsItem实体转ItemVo(使用预查询的类目名称Map，避免N+1查询)
     *
     * @param item 物品实体
     * @param categoryNameMap 主类目ID到名称的映射
     * @param subCategoryNameMap 细分类目ID到名称的映射
     * @return 物品VO
     */
    private ItemVo toItemVo(WmsItem item, Map<Long, String> categoryNameMap, Map<Long, String> subCategoryNameMap) {
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
        // 从Map中填充主类目名称
        if (item.getCategoryId() != null) {
            vo.setCategoryName(categoryNameMap.get(item.getCategoryId()));
        }
        // 从Map中填充细分类目名称
        if (item.getSubCategoryId() != null) {
            vo.setSubCategoryName(subCategoryNameMap.get(item.getSubCategoryId()));
        }
        return vo;
    }

    /**
     * WmsItemImage实体转ItemImageVo
     */
    private ItemImageVo toImageVo(WmsItemImage image) {
        ItemImageVo vo = new ItemImageVo();
        vo.setId(image.getId());
        vo.setItemId(image.getItemId());
        vo.setImageUrl(image.getImageUrl());
        vo.setImageName(image.getImageName());
        vo.setSortOrder(image.getSortOrder());
        return vo;
    }
}
