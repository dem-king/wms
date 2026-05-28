package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.storage.StorageConstants;
import com.wms.common.storage.StorageStrategy;
import com.wms.common.util.PinyinUtil;
import com.wms.item.domain.constant.ItemConstants;
import com.wms.item.domain.dto.ItemDto;
import com.wms.item.domain.entity.*;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.converter.ItemConverter;
import com.wms.item.mapper.*;
import com.wms.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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
    private final StorageStrategy storageStrategy;
    private final ItemConverter itemConverter;

    /**
     * 分页查询物品
     * 支持按类目、标签、状态、关键字筛选
     * 
     * @param pageParam 分页参数
     * @param categoryId 主类目ID(可选)
     * @param subCategoryId 细分类目ID(可选)
     * @param tagId 标签ID(可选)
     * @param status 状态(可选)
     * @param keyword 关键字(可选，模糊匹配名称/编号/型号/拼音)
     * @return 物品分页结果
     */
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
        java.util.Set<Long> categoryIds = items.stream()
                .map(WmsItem::getCategoryId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());
        java.util.Set<Long> subCategoryIds = items.stream()
                .map(WmsItem::getSubCategoryId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());
        Map<Long, String> categoryNameMap = categoryIds.isEmpty()
                ? Map.of()
                : wmsCategoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(java.util.stream.Collectors.toMap(WmsCategory::getId, WmsCategory::getCategoryName));
        Map<Long, String> subCategoryNameMap = subCategoryIds.isEmpty()
                ? Map.of()
                : wmsSubCategoryMapper.selectBatchIds(subCategoryIds).stream()
                        .collect(java.util.stream.Collectors.toMap(WmsSubCategory::getId, WmsSubCategory::getSubCategoryName));

        PageResult<ItemVo> result = new PageResult<>();
        result.setRecords(items.stream().map(item -> itemConverter.toVo(item, categoryNameMap, subCategoryNameMap)).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 根据ID查询物品详情
     * 
     * @param id 物品ID
     * @return 物品VO(含标签列表和图片列表)
     */
    @Override
    public ItemVo getById(Long id) {
        WmsItem item = wmsItemMapper.selectById(id);
        if (item == null) {
            throw new BizException("物品不存在");
        }
        if (item.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("物品已删除");
        }
        ItemVo vo = itemConverter.toVo(item, getCategoryName(item.getCategoryId()), getSubCategoryName(item.getSubCategoryId()));
        // 填充标签列表
        vo.setTags(getItemTags(id));
        // 填充图片列表
        vo.setImages(getItemImages(id));
        return vo;
    }

    /**
     * 新增物品
     * 自动生成物品编号和拼音，默认状态为启用
     * 
     * @param dto 物品新增参数
     * @return 新增后的物品VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemVo create(ItemDto dto) {
        // 校验主类目存在
        WmsCategory category = wmsCategoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BizException("主类目不存在");
        }
        if (category.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("主类目已删除");
        }
        // 校验细分类目存在(如果指定)
        if (dto.getSubCategoryId() != null) {
            WmsSubCategory subCategory = wmsSubCategoryMapper.selectById(dto.getSubCategoryId());
            if (subCategory == null) {
                throw new BizException("细分类目不存在");
            }
            if (subCategory.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("细分类目已删除");
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
            item.setStatus(BizConstants.STATUS_ENABLED);
        }
        if (item.getIsConsumable() == null) {
            item.setIsConsumable(ItemConstants.IS_CONSUMABLE_NO);
        }
        if (item.getIsReturnable() == null) {
            item.setIsReturnable(ItemConstants.IS_RETURNABLE_YES);
        }
        if (item.getPurchasePrice() == null) {
            item.setPurchasePrice(BigDecimal.ZERO);
        }
        if (item.getStockQty() == null) {
            item.setStockQty(ItemConstants.DEFAULT_STOCK_QTY);
        }
        wmsItemMapper.insert(item);
        // 保存物品标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            saveItemTags(item.getId(), dto.getTagIds());
        }
        return itemConverter.toVo(item, getCategoryName(item.getCategoryId()), getSubCategoryName(item.getSubCategoryId()));
    }

    /**
     * 更新物品
     * 编辑时不修改编号和拼音
     * 
     * @param id 物品ID
     * @param dto 物品更新参数
     * @return 更新后的物品VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemVo update(Long id, ItemDto dto) {
        WmsItem existing = wmsItemMapper.selectById(id);
        if (existing == null) {
            throw new BizException("物品不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("物品已删除");
        }
        // 校验主类目存在
        WmsCategory category = wmsCategoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BizException("主类目不存在");
        }
        if (category.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("主类目已删除");
        }
        // 校验细分类目存在(如果指定)
        if (dto.getSubCategoryId() != null) {
            WmsSubCategory subCategory = wmsSubCategoryMapper.selectById(dto.getSubCategoryId());
            if (subCategory == null) {
                throw new BizException("细分类目不存在");
            }
            if (subCategory.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("细分类目已删除");
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
        return itemConverter.toVo(existing, getCategoryName(existing.getCategoryId()), getSubCategoryName(existing.getSubCategoryId()));
    }

    /**
     * 删除物品(逻辑删除)
     * 同时逻辑删除标签关联和图片记录
     * 
     * @param id 物品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsItem existing = wmsItemMapper.selectById(id);
        if (existing == null) {
            throw new BizException("物品不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("物品已删除");
        }
        // 逻辑删除物品
        WmsItem updateEntity = new WmsItem();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsItemMapper.updateById(updateEntity);
        // 逻辑删除物品标签关联
        List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .eq(WmsItemTag::getItemId, id)
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
        // 逻辑删除物品图片
        List<WmsItemImage> images = wmsItemImageMapper.selectList(
                new LambdaQueryWrapper<WmsItemImage>()
                        .eq(WmsItemImage::getItemId, id)
        );
        List<WmsItemImage> updateImageList = new ArrayList<>();
        for (WmsItemImage image : images) {
            WmsItemImage updateImage = new WmsItemImage();
            updateImage.setId(image.getId());
            updateImage.setDelFlag(DelFlagConstants.DELETED);

            updateImageList.add(updateImage);
        }
        if (!updateImageList.isEmpty()) {
            Db.updateBatchById(updateImageList);
        }
    }

    /**
     * 上传物品图片
     * 通过StorageStrategy统一上传，支持本地/MinIO存储
     * 
     * @param itemId 物品ID
     * @param file 上传的图片文件
     * @return 图片VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemImageVo uploadImage(Long itemId, MultipartFile file) {
        // 校验物品存在
        WmsItem item = wmsItemMapper.selectById(itemId);
        if (item == null) {
            throw new BizException("物品不存在");
        }
        if (item.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("物品已删除");
        }
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        // 生成对象存储路径: itemId/uuid.ext
        String extension = "";
        if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
            extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        }
        String objectName = itemId + "/" + UUID.randomUUID().toString().replace("-", "") + extension;
        // 通过StorageStrategy统一上传，自动适配本地/MinIO存储
        String imageUrl;
        try {
            imageUrl = storageStrategy.upload(StorageConstants.BUCKET_ITEMS, objectName,
                    file.getInputStream(), file.getContentType(), file.getSize());
        } catch (Exception e) {
            throw new BizException("图片上传失败: " + e.getMessage());
        }
        // 保存图片记录
        WmsItemImage image = new WmsItemImage();
        image.setItemId(itemId);
        image.setImageUrl(imageUrl);
        image.setImageName(file.getOriginalFilename());
        // 排序号: 当前数量+1
        Long maxSort = wmsItemImageMapper.selectCount(
                new LambdaQueryWrapper<WmsItemImage>()
                        .eq(WmsItemImage::getItemId, itemId)
        );
        image.setSortOrder(maxSort.intValue() + 1);
        wmsItemImageMapper.insert(image);
        return itemConverter.toImageVo(image);
    }

    /**
     * 删除物品(逻辑删除)
     * 同时逻辑删除标签关联和图片记录
     * 
     * @param id 物品ID
     */
    /**
     * 删除物品图片(逻辑删除)
     * 
     * @param itemId 物品ID
     * @param imageId 图片ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long itemId, Long imageId) {
        WmsItemImage image = wmsItemImageMapper.selectById(imageId);
        if (image == null) {
            throw new BizException("图片不存在");
        }
        if (image.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("图片已删除");
        }
        if (!image.getItemId().equals(itemId)) {
            throw new BizException("图片不属于该物品");
        }
        WmsItemImage updateEntity = new WmsItemImage();
        updateEntity.setId(imageId);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsItemImageMapper.updateById(updateEntity);
    }

    /**
     * 快速搜索物品
     * 按关键字模糊匹配名称/拼音/型号/编号，限制返回数量
     * 
     * @param keyword 搜索关键字
     * @return 物品VO列表
     */
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
                .last("LIMIT " + ItemConstants.QUICK_SEARCH_LIMIT);
        List<WmsItem> items = wmsItemMapper.selectList(wrapper);
        // 批量查询类目名称，避免N+1查询
        java.util.Set<Long> categoryIds = items.stream()
                .map(WmsItem::getCategoryId).filter(id -> id != null).collect(java.util.stream.Collectors.toSet());
        java.util.Set<Long> subCategoryIds = items.stream()
                .map(WmsItem::getSubCategoryId).filter(id -> id != null).collect(java.util.stream.Collectors.toSet());
        Map<Long, String> categoryNameMap = categoryIds.isEmpty()
                ? Map.of()
                : wmsCategoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(java.util.stream.Collectors.toMap(WmsCategory::getId, WmsCategory::getCategoryName));
        Map<Long, String> subCategoryNameMap = subCategoryIds.isEmpty()
                ? Map.of()
                : wmsSubCategoryMapper.selectBatchIds(subCategoryIds).stream()
                        .collect(java.util.stream.Collectors.toMap(WmsSubCategory::getId, WmsSubCategory::getSubCategoryName));
        return items.stream().map(item -> itemConverter.toVo(item, categoryNameMap, subCategoryNameMap)).collect(Collectors.toList());
    }

    /**
     * 生成物品编号: WP + 年月日 + 4位随机数
     * 使用时间戳+随机数保证并发安全
     */
    private String generateItemCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return ItemConstants.ITEM_CODE_PREFIX + datePart + random;
    }

    /**
     * 批量保存物品标签关联
     *
     * @param itemId 物品ID
     * @param tagIds 标签ID列表
     */
    private void saveItemTags(Long itemId, List<Long> tagIds) {
        List<WmsItemTag> itemTagList = new ArrayList<>();
        for (Long tagId : tagIds) {
            WmsItemTag itemTag = new WmsItemTag();
            itemTag.setItemId(itemId);
            itemTag.setTagId(tagId);
            itemTagList.add(itemTag);
        }
        if (!itemTagList.isEmpty()) {
            Db.saveBatch(itemTagList);
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
        List<WmsItemTag> updateTagList = new ArrayList<>();
        for (WmsItemTag oldTag : oldItemTags) {
            WmsItemTag updateTag = new WmsItemTag();
            updateTag.setId(oldTag.getId());
            updateTag.setDelFlag(DelFlagConstants.DELETED);

            updateTagList.add(updateTag);
        }
        if (!updateTagList.isEmpty()) {
            Db.updateBatchById(updateTagList);
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
        return images.stream().map(itemConverter::toImageVo).collect(Collectors.toList());
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

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return Optional.ofNullable(wmsCategoryMapper.selectById(categoryId))
                .map(WmsCategory::getCategoryName)
                .orElse(null);
    }

    private String getSubCategoryName(Long subCategoryId) {
        if (subCategoryId == null) {
            return null;
        }
        return Optional.ofNullable(wmsSubCategoryMapper.selectById(subCategoryId))
                .map(WmsSubCategory::getSubCategoryName)
                .orElse(null);
    }
}
