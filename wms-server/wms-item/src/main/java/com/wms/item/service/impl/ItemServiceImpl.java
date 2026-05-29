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
import com.wms.item.domain.dto.ItemImageDto;
import com.wms.item.domain.entity.*;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.domain.vo.ItemLocationVo;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.converter.ItemConverter;
import com.wms.item.mapper.*;
import com.wms.item.service.ItemService;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
    private final WmsStockMapper wmsStockMapper;
    private final WmsCategoryMapper wmsCategoryMapper;
    private final WmsSubCategoryMapper wmsSubCategoryMapper;
    private final WmsTagMapper wmsTagMapper;
    private final StorageStrategy storageStrategy;
    private final ItemConverter itemConverter;
    private final WmsItemBinMapper wmsItemBinMapper;
    private final WmsBinMapper wmsBinMapper;
    private final WmsCabinetMapper wmsCabinetMapper;
    private final WmsAreaMapper wmsAreaMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;

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
        Map<Long, List<ItemLocationVo>> locationMap = loadItemLocationsMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        Map<Long, List<ItemImageVo>> imageMap = loadItemImagesMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        Map<Long, List<TagVo>> tagMap = loadItemTagsMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        Map<Long, Integer> currentStockMap = loadCurrentStockMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));

        PageResult<ItemVo> result = new PageResult<>();
        result.setRecords(items.stream()
                .map(item -> withCurrentStock(
                        withTags(
                                withImages(
                                        withLocations(itemConverter.toVo(item, categoryNameMap, subCategoryNameMap),
                                                locationMap.get(item.getId())),
                                        imageMap.get(item.getId())),
                                tagMap.get(item.getId())),
                        currentStockMap.get(item.getId())))
                .collect(Collectors.toList()));
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
        withLocations(vo, loadItemLocationsMap(List.of(id)).get(id));
        withCurrentStock(vo, loadCurrentStockMap(List.of(id)).get(id));
        withTags(vo, getItemTags(id));
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
        List<Long> binIds = normalizeBinIds(dto.getBinIds());
        validateBins(binIds);

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
        syncItemBins(item.getId(), binIds);
        return withLocations(itemConverter.toVo(item, getCategoryName(item.getCategoryId()), getSubCategoryName(item.getSubCategoryId())),
                loadItemLocationsMap(List.of(item.getId())).get(item.getId()));
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
        List<Long> binIds = dto.getBinIds() == null ? null : normalizeBinIds(dto.getBinIds());
        if (binIds != null) {
            validateBins(binIds);
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
        if (binIds != null) {
            syncItemBins(id, binIds);
        }
        return withLocations(itemConverter.toVo(existing, getCategoryName(existing.getCategoryId()), getSubCategoryName(existing.getSubCategoryId())),
                loadItemLocationsMap(List.of(id)).get(id));
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
        logicalDeleteItemBins(id);
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
        image.setBucket(StorageConstants.BUCKET_ITEMS);
        image.setObjectName(objectName);
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
     * 关联已通过统一存储接口上传的物品图片
     *
     * @param itemId 物品ID
     * @param dto    物品图片关联参数
     * @return 图片VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ItemImageVo attachImage(Long itemId, ItemImageDto dto) {
        WmsItem item = wmsItemMapper.selectById(itemId);
        if (item == null) {
            throw new BizException("物品不存在");
        }
        if (item.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("物品已删除");
        }

        WmsItemImage image = new WmsItemImage();
        image.setItemId(itemId);
        image.setImageUrl(dto.getImageUrl());
        image.setBucket(StorageConstants.BUCKET_ITEMS);
        image.setObjectName(dto.getObjectName());
        image.setImageName(dto.getImageName());
        if (dto.getSortOrder() != null) {
            image.setSortOrder(dto.getSortOrder());
        } else {
            Long maxSort = wmsItemImageMapper.selectCount(
                    new LambdaQueryWrapper<WmsItemImage>()
                            .eq(WmsItemImage::getItemId, itemId)
            );
            image.setSortOrder(maxSort.intValue() + 1);
        }
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
        Map<Long, List<ItemLocationVo>> locationMap = loadItemLocationsMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        Map<Long, List<ItemImageVo>> imageMap = loadItemImagesMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        Map<Long, List<TagVo>> tagMap = loadItemTagsMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        Map<Long, Integer> currentStockMap = loadCurrentStockMap(
                items.stream().map(WmsItem::getId).collect(Collectors.toList()));
        return items.stream()
                .map(item -> withCurrentStock(
                        withTags(
                                withImages(
                                        withLocations(itemConverter.toVo(item, categoryNameMap, subCategoryNameMap),
                                                locationMap.get(item.getId())),
                                        imageMap.get(item.getId())),
                                tagMap.get(item.getId())),
                        currentStockMap.get(item.getId())))
                .collect(Collectors.toList());
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
        return tags.stream().map(this::toTagVo).collect(Collectors.toList());
    }

    /**
     * 批量加载物品标签，供列表展示和编辑回填使用，避免逐条查询。
     *
     * @param itemIds 物品ID集合
     * @return 物品ID到标签列表的映射
     */
    private Map<Long, List<TagVo>> loadItemTagsMap(Collection<Long> itemIds) {
        List<Long> ids = itemIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<WmsItemTag> itemTags = wmsItemTagMapper.selectList(
                new LambdaQueryWrapper<WmsItemTag>()
                        .in(WmsItemTag::getItemId, ids));
        if (itemTags == null || itemTags.isEmpty()) {
            return Map.of();
        }
        Set<Long> tagIds = itemTags.stream()
                .map(WmsItemTag::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, WmsTag> tagMap = tagIds.isEmpty()
                ? Map.of()
                : wmsTagMapper.selectBatchIds(tagIds).stream()
                        .collect(Collectors.toMap(WmsTag::getId, Function.identity()));
        Map<Long, List<TagVo>> result = new LinkedHashMap<>();
        for (WmsItemTag itemTag : itemTags) {
            WmsTag tag = tagMap.get(itemTag.getTagId());
            if (tag == null) {
                continue;
            }
            result.computeIfAbsent(itemTag.getItemId(), key -> new ArrayList<>()).add(toTagVo(tag));
        }
        return result;
    }

    private TagVo toTagVo(WmsTag tag) {
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
     * 批量加载物品图片，供列表和快速搜索展示首图使用，避免逐条查询。
     *
     * @param itemIds 物品ID集合
     * @return 物品ID到图片列表的映射
     */
    private Map<Long, List<ItemImageVo>> loadItemImagesMap(Collection<Long> itemIds) {
        List<Long> ids = itemIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<WmsItemImage> images = wmsItemImageMapper.selectList(
                new LambdaQueryWrapper<WmsItemImage>()
                        .in(WmsItemImage::getItemId, ids)
                        .orderByAsc(WmsItemImage::getSortOrder));
        if (images == null || images.isEmpty()) {
            return Map.of();
        }
        return images.stream()
                .map(itemConverter::toImageVo)
                .collect(Collectors.groupingBy(ItemImageVo::getItemId, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 批量汇总物品实时库存，以wms_stock.quantity为准。
     *
     * @param itemIds 物品ID集合
     * @return 物品ID到实时库存数量的映射
     */
    private Map<Long, Integer> loadCurrentStockMap(Collection<Long> itemIds) {
        List<Long> ids = itemIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<WmsStock> stocks = wmsStockMapper.selectList(
                new LambdaQueryWrapper<WmsStock>()
                        .in(WmsStock::getItemId, ids));
        if (stocks == null || stocks.isEmpty()) {
            return Map.of();
        }
        return stocks.stream()
                .collect(Collectors.groupingBy(WmsStock::getItemId,
                        Collectors.summingInt(stock -> stock.getQuantity() == null ? 0 : stock.getQuantity())));
    }

    /**
     * DTO属性拷贝到Entity
     */
    /**
     * 规范化默认库位ID，去重并保留前端选择顺序。
     *
     * @param binIds 默认库位ID列表
     * @return 去重后的默认库位ID列表
     */
    private List<Long> normalizeBinIds(List<Long> binIds) {
        if (binIds == null || binIds.isEmpty()) {
            return List.of();
        }
        return binIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 校验默认库位存在且父级库房结构完整。
     *
     * @param binIds 默认库位ID列表
     */
    private void validateBins(List<Long> binIds) {
        if (binIds.isEmpty()) {
            return;
        }
        Map<Long, WmsBin> binMap = wmsBinMapper.selectBatchIds(binIds).stream()
                .collect(Collectors.toMap(WmsBin::getId, Function.identity()));
        for (Long binId : binIds) {
            if (!binMap.containsKey(binId)) {
                throw new BizException("库位不存在: binId=" + binId);
            }
        }
        for (WmsBin bin : binMap.values()) {
            if (Objects.equals(bin.getBinStatus(), WarehouseConstants.BIN_STATUS_DISABLED)) {
                throw new BizException("库位已禁用: binId=" + bin.getId());
            }
        }
        Set<Long> cabinetIds = binMap.values().stream()
                .map(WmsBin::getCabinetId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, WmsCabinet> cabinetMap = cabinetIds.isEmpty()
                ? Map.of()
                : wmsCabinetMapper.selectBatchIds(cabinetIds).stream()
                        .collect(Collectors.toMap(WmsCabinet::getId, Function.identity()));
        for (WmsBin bin : binMap.values()) {
            if (bin.getCabinetId() != null && !cabinetMap.containsKey(bin.getCabinetId())) {
                throw new BizException("库位所属存放柜不存在: binId=" + bin.getId());
            }
        }
        for (WmsCabinet cabinet : cabinetMap.values()) {
            if (!Objects.equals(cabinet.getStatus(), BizConstants.STATUS_ENABLED)) {
                throw new BizException("存放柜已禁用: cabinetId=" + cabinet.getId());
            }
        }
        Set<Long> areaIds = cabinetMap.values().stream()
                .map(WmsCabinet::getAreaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, WmsArea> areaMap = areaIds.isEmpty()
                ? Map.of()
                : wmsAreaMapper.selectBatchIds(areaIds).stream()
                        .collect(Collectors.toMap(WmsArea::getId, Function.identity()));
        if (areaMap.size() != areaIds.size()) {
            throw new BizException("库位所属区域不存在");
        }
        for (WmsArea area : areaMap.values()) {
            if (!Objects.equals(area.getStatus(), BizConstants.STATUS_ENABLED)) {
                throw new BizException("区域已禁用: areaId=" + area.getId());
            }
        }
        Set<Long> warehouseIds = binMap.values().stream()
                .map(WmsBin::getWarehouseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        warehouseIds.addAll(cabinetMap.values().stream()
                .map(WmsCabinet::getWarehouseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        warehouseIds.addAll(areaMap.values().stream()
                .map(WmsArea::getWarehouseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty()
                ? Map.of()
                : wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouse::getId, Function.identity()));
        if (warehouseMap.size() != warehouseIds.size()) {
            throw new BizException("库位所属库房不存在");
        }
        for (WmsWarehouse warehouse : warehouseMap.values()) {
            if (!Objects.equals(warehouse.getStatus(), BizConstants.STATUS_ENABLED)) {
                throw new BizException("库房已禁用: warehouseId=" + warehouse.getId());
            }
        }
    }

    /**
     * 同步物品默认库位，使用逻辑删除/恢复避免破坏历史记录。
     *
     * @param itemId 物品ID
     * @param binIds 默认库位ID列表
     */
    private void syncItemBins(Long itemId, List<Long> binIds) {
        List<WmsItemBin> existingList = wmsItemBinMapper.selectAllByItemId(itemId);
        Map<Long, WmsItemBin> existingMap = existingList.stream()
                .collect(Collectors.toMap(WmsItemBin::getBinId, Function.identity(), (left, right) -> left));
        Set<Long> selected = Set.copyOf(binIds);
        List<WmsItemBin> deleteList = new ArrayList<>();
        for (WmsItemBin existing : existingList) {
            if (!selected.contains(existing.getBinId())
                    && !Objects.equals(existing.getDelFlag(), DelFlagConstants.DELETED)) {
                WmsItemBin update = new WmsItemBin();
                update.setId(existing.getId());
                update.setDelFlag(DelFlagConstants.DELETED);
                deleteList.add(update);
            }
        }
        if (!deleteList.isEmpty()) {
            Db.updateBatchById(deleteList);
        }
        for (int index = 0; index < binIds.size(); index++) {
            Long binId = binIds.get(index);
            WmsItemBin existing = existingMap.get(binId);
            if (existing == null) {
                WmsItemBin itemBin = new WmsItemBin();
                itemBin.setItemId(itemId);
                itemBin.setBinId(binId);
                itemBin.setSortOrder(index + 1);
                wmsItemBinMapper.insert(itemBin);
            } else if (Objects.equals(existing.getDelFlag(), DelFlagConstants.DELETED)) {
                wmsItemBinMapper.restoreById(existing.getId(), index + 1, DelFlagConstants.NORMAL);
            } else if (!Objects.equals(existing.getSortOrder(), index + 1)) {
                WmsItemBin update = new WmsItemBin();
                update.setId(existing.getId());
                update.setSortOrder(index + 1);
                wmsItemBinMapper.updateById(update);
            }
        }
    }

    /**
     * 逻辑删除物品默认库位关联。
     *
     * @param itemId 物品ID
     */
    private void logicalDeleteItemBins(Long itemId) {
        List<WmsItemBin> itemBins = wmsItemBinMapper.selectList(
                new LambdaQueryWrapper<WmsItemBin>().eq(WmsItemBin::getItemId, itemId));
        List<WmsItemBin> updateList = new ArrayList<>();
        for (WmsItemBin itemBin : itemBins) {
            WmsItemBin update = new WmsItemBin();
            update.setId(itemBin.getId());
            update.setDelFlag(DelFlagConstants.DELETED);
            updateList.add(update);
        }
        if (!updateList.isEmpty()) {
            Db.updateBatchById(updateList);
        }
    }

    /**
     * 批量加载物品默认库位并组装库位路径。
     *
     * @param itemIds 物品ID集合
     * @return 物品ID到默认库位列表的映射
     */
    private Map<Long, List<ItemLocationVo>> loadItemLocationsMap(Collection<Long> itemIds) {
        List<Long> ids = itemIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<WmsItemBin> itemBins = wmsItemBinMapper.selectList(
                new LambdaQueryWrapper<WmsItemBin>()
                        .in(WmsItemBin::getItemId, ids)
                        .orderByAsc(WmsItemBin::getSortOrder));
        if (itemBins.isEmpty()) {
            return Map.of();
        }
        Set<Long> binIds = itemBins.stream().map(WmsItemBin::getBinId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, WmsBin> binMap = binIds.isEmpty()
                ? Map.of()
                : wmsBinMapper.selectBatchIds(binIds).stream()
                        .collect(Collectors.toMap(WmsBin::getId, Function.identity()));
        Set<Long> cabinetIds = binMap.values().stream().map(WmsBin::getCabinetId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, WmsCabinet> cabinetMap = cabinetIds.isEmpty()
                ? Map.of()
                : wmsCabinetMapper.selectBatchIds(cabinetIds).stream()
                        .collect(Collectors.toMap(WmsCabinet::getId, Function.identity()));
        Set<Long> areaIds = cabinetMap.values().stream().map(WmsCabinet::getAreaId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, WmsArea> areaMap = areaIds.isEmpty()
                ? Map.of()
                : wmsAreaMapper.selectBatchIds(areaIds).stream()
                        .collect(Collectors.toMap(WmsArea::getId, Function.identity()));
        Set<Long> warehouseIds = binMap.values().stream().map(WmsBin::getWarehouseId).filter(Objects::nonNull).collect(Collectors.toSet());
        warehouseIds.addAll(cabinetMap.values().stream().map(WmsCabinet::getWarehouseId).filter(Objects::nonNull).collect(Collectors.toSet()));
        warehouseIds.addAll(areaMap.values().stream().map(WmsArea::getWarehouseId).filter(Objects::nonNull).collect(Collectors.toSet()));
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty()
                ? Map.of()
                : wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouse::getId, Function.identity()));

        Map<Long, List<ItemLocationVo>> result = new LinkedHashMap<>();
        for (WmsItemBin itemBin : itemBins) {
            WmsBin bin = binMap.get(itemBin.getBinId());
            if (bin == null) {
                continue;
            }
            WmsCabinet cabinet = cabinetMap.get(bin.getCabinetId());
            WmsArea area = cabinet == null ? null : areaMap.get(cabinet.getAreaId());
            Long warehouseId = resolveWarehouseId(bin, cabinet, area);
            WmsWarehouse warehouse = warehouseId == null ? null : warehouseMap.get(warehouseId);
            result.computeIfAbsent(itemBin.getItemId(), key -> new ArrayList<>())
                    .add(toLocationVo(bin, cabinet, area, warehouse));
        }
        return result;
    }

    private ItemVo withLocations(ItemVo vo, List<ItemLocationVo> locations) {
        List<ItemLocationVo> safeLocations = locations == null ? List.of() : locations;
        vo.setLocations(safeLocations);
        vo.setBinIds(safeLocations.stream().map(ItemLocationVo::getBinId).collect(Collectors.toList()));
        return vo;
    }

    private ItemVo withImages(ItemVo vo, List<ItemImageVo> images) {
        vo.setImages(images == null ? List.of() : images);
        return vo;
    }

    private ItemVo withTags(ItemVo vo, List<TagVo> tags) {
        List<TagVo> safeTags = tags == null ? List.of() : tags;
        vo.setTags(safeTags);
        vo.setTagIds(safeTags.stream().map(TagVo::getId).collect(Collectors.toList()));
        vo.setTagNames(safeTags.stream().map(TagVo::getTagName).collect(Collectors.toList()));
        return vo;
    }

    private ItemVo withCurrentStock(ItemVo vo, Integer currentStock) {
        vo.setCurrentStock(currentStock == null ? 0 : currentStock);
        return vo;
    }

    private ItemLocationVo toLocationVo(WmsBin bin, WmsCabinet cabinet, WmsArea area, WmsWarehouse warehouse) {
        ItemLocationVo vo = new ItemLocationVo();
        vo.setBinId(bin.getId());
        vo.setBinCode(bin.getBinCode());
        vo.setCabinetId(bin.getCabinetId());
        vo.setCabinetName(cabinet == null ? null : cabinet.getCabinetName());
        vo.setAreaId(cabinet == null ? null : cabinet.getAreaId());
        vo.setAreaName(area == null ? null : area.getAreaName());
        Long warehouseId = resolveWarehouseId(bin, cabinet, area);
        vo.setWarehouseId(warehouseId);
        vo.setWarehouseName(warehouse == null ? null : warehouse.getWarehouseName());
        vo.setLocationText(buildLocationText(vo));
        return vo;
    }

    private Long resolveWarehouseId(WmsBin bin, WmsCabinet cabinet, WmsArea area) {
        if (bin.getWarehouseId() != null) {
            return bin.getWarehouseId();
        }
        if (cabinet != null && cabinet.getWarehouseId() != null) {
            return cabinet.getWarehouseId();
        }
        return area == null ? null : area.getWarehouseId();
    }

    private String buildLocationText(ItemLocationVo location) {
        return java.util.stream.Stream.of(location.getWarehouseName(), location.getAreaName(),
                        location.getCabinetName(), location.getBinCode())
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining("/"));
    }

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
