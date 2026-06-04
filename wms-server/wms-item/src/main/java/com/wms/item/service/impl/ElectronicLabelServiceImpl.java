package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.wms.common.enums.LabelStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.item.converter.ElectronicLabelConverter;
import com.wms.item.domain.constant.ItemConstants;
import com.wms.item.domain.constant.LabelConstants;
import com.wms.item.domain.dto.LabelBindDto;
import com.wms.item.domain.dto.LabelGenerateDto;
import com.wms.item.domain.dto.LabelStatusDto;
import com.wms.item.domain.entity.WmsElectronicLabel;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.entity.WmsItemBin;
import com.wms.item.domain.entity.WmsStock;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.mapper.WmsCategoryMapper;
import com.wms.item.mapper.WmsElectronicLabelMapper;
import com.wms.item.mapper.WmsItemBinMapper;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.mapper.WmsStockMapper;
import com.wms.item.mapper.WmsSubCategoryMapper;
import com.wms.item.service.ElectronicLabelService;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 电子标签服务实现类。
 * 负责库位 RFID 标签生成、绑定、状态流转、查询和详情补全。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElectronicLabelServiceImpl implements ElectronicLabelService {

    private final WmsElectronicLabelMapper labelMapper;
    private final WmsItemMapper wmsItemMapper;
    private final WmsStockMapper wmsStockMapper;
    private final WmsCategoryMapper wmsCategoryMapper;
    private final WmsSubCategoryMapper wmsSubCategoryMapper;
    private final WmsItemBinMapper wmsItemBinMapper;
    private final WmsBinMapper wmsBinMapper;
    private final WmsCabinetMapper wmsCabinetMapper;
    private final WmsAreaMapper wmsAreaMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final ElectronicLabelConverter converter;
    private final SequenceGenerator sequenceGenerator;

    /**
     * 分页查询电子标签。
     *
     * @param page 分页参数
     * @param itemId 物品ID
     * @param labelType 标签类型
     * @param labelStatus 标签状态
     * @return 电子标签分页结果
     */
    @Override
    public Page<ElectronicLabelVo> page(Page<ElectronicLabelVo> page, Long itemId, Integer labelType, Integer labelStatus) {
        return page(page, itemId, labelType, labelStatus, null);
    }

    /**
     * 分页查询电子标签，支持标签编号、RFID、物品和库位关键字。
     *
     * @param page 分页参数
     * @param itemId 物品ID
     * @param labelType 标签类型
     * @param labelStatus 标签状态
     * @param keyword 查询关键字
     * @return 电子标签分页结果
     */
    @Override
    public Page<ElectronicLabelVo> page(Page<ElectronicLabelVo> page, Long itemId, Integer labelType,
                                        Integer labelStatus, String keyword) {
        LambdaQueryWrapper<WmsElectronicLabel> wrapper = new LambdaQueryWrapper<>();
        if (itemId != null) {
            wrapper.eq(WmsElectronicLabel::getItemId, itemId);
        }
        if (labelType != null) {
            wrapper.eq(WmsElectronicLabel::getLabelType, labelType);
        }
        if (labelStatus != null) {
            wrapper.eq(WmsElectronicLabel::getLabelStatus, labelStatus);
        }
        applyKeyword(wrapper, keyword);
        wrapper.orderByDesc(WmsElectronicLabel::getCreateTime);

        Page<WmsElectronicLabel> entityPage = labelMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        List<WmsElectronicLabel> records = safeList(entityPage.getRecords());
        LabelEnrichment enrichment = loadEnrichment(records);
        List<ElectronicLabelVo> voList = records.stream()
                .map(label -> enrich(converter.toVo(label), enrichment))
                .collect(Collectors.toList());

        Page<ElectronicLabelVo> result = new Page<>(page.getCurrent(), page.getSize(), entityPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 查询电子标签详情。
     *
     * @param id 标签ID
     * @return 电子标签详情
     */
    @Override
    public ElectronicLabelVo getById(Long id) {
        WmsElectronicLabel entity = labelMapper.selectById(id);
        if (entity == null) {
            throw new BizException("标签不存在");
        }
        return enrichWithRelatedInfo(entity);
    }

    /**
     * 手工生成或复用库位电子标签。
     *
     * @param dto 标签生成参数
     * @return 生成或复用的标签列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ElectronicLabelVo> generate(LabelGenerateDto dto) {
        if (dto.getCount() != null && dto.getCount() > LabelConstants.BIND_TYPE_SINGLE) {
            throw new BizException("库位电子标签一次只能生成一个");
        }
        WmsItem item = loadOptionalItem(dto.getItemId());
        WmsBin bin = wmsBinMapper.selectById(dto.getBinId());
        if (bin == null) {
            throw new BizException("库位不存在: binId=" + dto.getBinId());
        }
        if (dto.getItemId() != null) {
            validateSingleItemBins(dto.getItemId(), List.of(dto.getBinId()));
        }

        WmsElectronicLabel existing = findByBinId(dto.getBinId());
        if (existing != null) {
            if (existing.getItemId() != null && dto.getItemId() != null && !Objects.equals(existing.getItemId(), dto.getItemId())) {
                throw new BizException("库位已绑定其他物品标签: binId=" + dto.getBinId());
            }
            if (existing.getItemId() == null && dto.getItemId() != null) {
                existing.setItemId(dto.getItemId());
                existing.setBindType(dto.getBindType());
                labelMapper.updateById(existing);
            }
            return List.of(enrichWithRelatedInfo(existing));
        }

        WmsElectronicLabel label = createRfidLabel(dto.getItemId(), dto.getBinId(), dto.getLabelPrefix());
        label.setLabelType(dto.getLabelType());
        label.setBindType(dto.getBindType());
        if (LabelConstants.LABEL_TYPE_RFID != dto.getLabelType()) {
            label.setRfidCode(null);
        }
        labelMapper.insert(label);

        ElectronicLabelVo vo = converter.toVo(label);
        if (item != null) {
            vo.setItemName(item.getItemName());
            vo.setItemCode(item.getItemCode());
        }
        return List.of(enrich(vo, loadEnrichment(List.of(label))));
    }

    /**
     * 为物品默认库位确保唯一 RFID 标签。
     *
     * @param itemId 物品ID
     * @param binIds 库位ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureRfidLabelsForItemBins(Long itemId, List<Long> binIds) {
        if (itemId == null || binIds == null || binIds.isEmpty()) {
            return;
        }
        List<Long> normalizedBinIds = binIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (normalizedBinIds.isEmpty()) {
            return;
        }
        validateSingleItemBins(itemId, normalizedBinIds);

        List<WmsElectronicLabel> existingLabels = safeList(labelMapper.selectList(new LambdaQueryWrapper<WmsElectronicLabel>()
                .in(WmsElectronicLabel::getBinId, normalizedBinIds)));
        Map<Long, WmsElectronicLabel> labelByBinId = existingLabels.stream()
                .filter(label -> label.getBinId() != null)
                .collect(Collectors.toMap(WmsElectronicLabel::getBinId, label -> label, (left, right) -> left));

        List<WmsElectronicLabel> newLabels = new ArrayList<>();
        for (Long binId : normalizedBinIds) {
            WmsElectronicLabel existing = labelByBinId.get(binId);
            if (existing == null) {
                newLabels.add(createRfidLabel(itemId, binId, LabelConstants.LABEL_NO_PREFIX));
                continue;
            }
            if (existing.getItemId() != null && !Objects.equals(existing.getItemId(), itemId)) {
                throw new BizException("库位已绑定其他物品标签: binId=" + binId);
            }
            if (!Objects.equals(existing.getItemId(), itemId)
                    || !Objects.equals(existing.getLabelType(), LabelConstants.LABEL_TYPE_RFID)) {
                existing.setItemId(itemId);
                existing.setLabelType(LabelConstants.LABEL_TYPE_RFID);
                existing.setBindType(LabelConstants.BIND_TYPE_SINGLE);
                labelMapper.updateById(existing);
            }
        }
        if (!newLabels.isEmpty()) {
            Db.saveBatch(newLabels);
        }
    }

    /**
     * 出库审批通过后标记标签为正在使用。
     *
     * @param labelId 标签ID
     * @param binId 库位ID
     * @param borrowerName 领用人
     * @param expectedReturnTime 预计归还时间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markBorrowed(Long labelId, Long binId, String borrowerName, LocalDateTime expectedReturnTime) {
        WmsElectronicLabel label = findLabelForOrderDetail(labelId, binId);
        validateBorrowStatus(label.getLabelStatus());
        label.setLabelStatus(LabelStatusEnum.IN_USE.getCode());
        label.setBorrowerName(borrowerName);
        label.setBorrowTime(LocalDateTime.now());
        label.setExpectedReturn(expectedReturnTime);
        label.setReturnerName(null);
        label.setReturnTime(null);
        labelMapper.updateById(label);
    }

    /**
     * 归还审批通过后标记标签为已归还。
     *
     * @param labelId 标签ID
     * @param binId 库位ID
     * @param returnerName 归还人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markReturned(Long labelId, Long binId, String returnerName) {
        WmsElectronicLabel label = findLabelForOrderDetail(labelId, binId);
        validateStatusTransition(label.getLabelStatus(), LabelStatusEnum.RETURNED.getCode());
        label.setLabelStatus(LabelStatusEnum.RETURNED.getCode());
        label.setReturnerName(returnerName);
        label.setReturnTime(LocalDateTime.now());
        labelMapper.updateById(label);
    }

    /**
     * 绑定标签到物品。
     *
     * @param id 标签ID
     * @param dto 绑定参数
     * @return 绑定后的标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicLabelVo bind(Long id, LabelBindDto dto) {
        WmsElectronicLabel entity = labelMapper.selectById(id);
        if (entity == null) {
            throw new BizException("标签不存在");
        }
        if (!Objects.equals(entity.getLabelStatus(), LabelStatusEnum.IDLE.getCode())) {
            throw new BizException("只有闲置状态的标签才能绑定物品");
        }
        WmsItem item = wmsItemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new BizException("物品不存在");
        }

        entity.setItemId(dto.getItemId());
        entity.setBindType(dto.getBindType());
        entity.setLabelStatus(LabelStatusEnum.IN_STOCK.getCode());
        labelMapper.updateById(entity);

        return enrichWithRelatedInfo(entity);
    }

    /**
     * 更新标签状态。
     *
     * @param id 标签ID
     * @param dto 状态更新参数
     * @return 更新后的标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicLabelVo updateStatus(Long id, LabelStatusDto dto) {
        WmsElectronicLabel entity = labelMapper.selectById(id);
        if (entity == null) {
            throw new BizException("标签不存在");
        }
        validateStatusTransition(entity.getLabelStatus(), dto.getLabelStatus());

        entity.setLabelStatus(dto.getLabelStatus());
        if (Objects.equals(dto.getLabelStatus(), LabelStatusEnum.IN_USE.getCode())) {
            entity.setBorrowTime(LocalDateTime.now());
        }
        labelMapper.updateById(entity);
        return enrichWithRelatedInfo(entity);
    }

    /**
     * 批量标记标签为已打印。
     *
     * @param labelIds 标签ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPrint(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            throw new BizException("标签ID列表不能为空");
        }
        List<WmsElectronicLabel> labels = safeList(labelMapper.selectBatchIds(labelIds));
        List<WmsElectronicLabel> toUpdate = labels.stream()
                .filter(label -> !Objects.equals(label.getPrintStatus(), LabelConstants.PRINT_STATUS_DONE))
                .peek(label -> label.setPrintStatus(LabelConstants.PRINT_STATUS_DONE))
                .collect(Collectors.toList());
        if (!toUpdate.isEmpty()) {
            Db.updateBatchById(toUpdate);
        }
    }

    /**
     * 扫码查询标签。
     *
     * @param code 扫码内容
     * @return 标签信息
     */
    @Override
    public ElectronicLabelVo scan(String code) {
        if (code == null || code.isBlank()) {
            throw new BizException("扫码内容不能为空");
        }
        WmsElectronicLabel entity = labelMapper.selectOne(new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getLabelNo, code)
                .or().eq(WmsElectronicLabel::getRfidCode, code)
                .or().eq(WmsElectronicLabel::getQrContent, code)
                .or().eq(WmsElectronicLabel::getBarcodeContent, code)
                .last("LIMIT 1"));
        if (entity == null) {
            throw new BizException("未找到匹配的标签");
        }
        return enrichWithRelatedInfo(entity);
    }

    /**
     * 查询长期闲置标签。
     *
     * @return 闲置标签列表
     */
    @Override
    public List<ElectronicLabelVo> listIdle() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(LabelConstants.IDLE_THRESHOLD_DAYS);
        List<WmsElectronicLabel> idleLabels = safeList(labelMapper.selectList(new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getLabelStatus, LabelStatusEnum.IDLE.getCode())));
        List<WmsElectronicLabel> staleLabels = safeList(labelMapper.selectList(new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getLabelStatus, LabelStatusEnum.IN_STOCK.getCode())
                .lt(WmsElectronicLabel::getUpdateTime, threshold)));

        List<WmsElectronicLabel> allLabels = new ArrayList<>(idleLabels);
        allLabels.addAll(staleLabels);
        LabelEnrichment enrichment = loadEnrichment(allLabels);
        return allLabels.stream()
                .map(label -> enrich(converter.toVo(label), enrichment))
                .collect(Collectors.toList());
    }

    private WmsItem loadOptionalItem(Long itemId) {
        if (itemId == null) {
            return null;
        }
        WmsItem item = wmsItemMapper.selectById(itemId);
        if (item == null) {
            throw new BizException("物品不存在");
        }
        return item;
    }

    private String generateLabelNo(String labelPrefix) {
        String prefix = labelPrefix == null || labelPrefix.isBlank()
                ? LabelConstants.LABEL_NO_PREFIX : labelPrefix.trim();
        return sequenceGenerator.next(prefix);
    }

    private String generateRfidCode() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(LabelConstants.RFID_RANDOM_MAX - LabelConstants.RFID_RANDOM_MIN)
                + LabelConstants.RFID_RANDOM_MIN;
        return LabelConstants.RFID_CODE_PREFIX + timePart + random;
    }

    private WmsElectronicLabel createRfidLabel(Long itemId, Long binId, String labelPrefix) {
        WmsElectronicLabel label = new WmsElectronicLabel();
        label.setLabelNo(generateLabelNo(labelPrefix));
        label.setLabelType(LabelConstants.LABEL_TYPE_RFID);
        label.setItemId(itemId);
        label.setBinId(binId);
        label.setBindType(LabelConstants.BIND_TYPE_SINGLE);
        label.setLabelStatus(LabelStatusEnum.IN_STOCK.getCode());
        label.setPrintStatus(LabelConstants.PRINT_STATUS_NOT);
        label.setQrContent(label.getLabelNo());
        label.setBarcodeContent(label.getLabelNo());
        label.setRfidCode(generateRfidCode());
        return label;
    }

    private WmsElectronicLabel findLabelForOrderDetail(Long labelId, Long binId) {
        WmsElectronicLabel label = labelId == null ? null : labelMapper.selectById(labelId);
        if (label == null && binId != null) {
            label = findByBinId(binId);
        }
        if (label == null) {
            throw new BizException("库位电子标签不存在: binId=" + binId);
        }
        return label;
    }

    private WmsElectronicLabel findByBinId(Long binId) {
        if (binId == null) {
            return null;
        }
        return labelMapper.selectOne(new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getBinId, binId)
                .last("LIMIT 1"));
    }

    private void validateBorrowStatus(Integer currentStatus) {
        if (Objects.equals(currentStatus, LabelStatusEnum.IN_STOCK.getCode())
                || Objects.equals(currentStatus, LabelStatusEnum.RETURNED.getCode())
                || Objects.equals(currentStatus, LabelStatusEnum.IN_USE.getCode())) {
            return;
        }
        throw new BizException("标签状态不允许变更为正在使用: currentStatus=" + currentStatus);
    }

    private void validateStatusTransition(Integer currentStatus, Integer targetStatus) {
        if (Objects.equals(currentStatus, targetStatus)) {
            return;
        }
        Set<Integer> allowedTargets = LabelConstants.STATUS_TRANSITIONS.get(currentStatus);
        if (allowedTargets == null || !allowedTargets.contains(targetStatus)) {
            throw new BizException("标签状态不允许从" + currentStatus + "变更为" + targetStatus);
        }
    }

    private void applyKeyword(LambdaQueryWrapper<WmsElectronicLabel> wrapper, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return;
        }
        String value = keyword.trim();
        List<Long> itemIds = safeList(wmsItemMapper.selectList(new LambdaQueryWrapper<WmsItem>()
                .like(WmsItem::getItemName, value)
                .or()
                .like(WmsItem::getItemCode, value)))
                .stream()
                .map(WmsItem::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Long> binIds = safeList(wmsBinMapper.selectList(new LambdaQueryWrapper<WmsBin>()
                .like(WmsBin::getBinCode, value)))
                .stream()
                .map(WmsBin::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        wrapper.and(query -> {
            query.like(WmsElectronicLabel::getLabelNo, value)
                    .or().like(WmsElectronicLabel::getRfidCode, value)
                    .or().like(WmsElectronicLabel::getQrContent, value)
                    .or().like(WmsElectronicLabel::getBarcodeContent, value);
            if (!itemIds.isEmpty()) {
                query.or().in(WmsElectronicLabel::getItemId, itemIds);
            }
            if (!binIds.isEmpty()) {
                query.or().in(WmsElectronicLabel::getBinId, binIds);
            }
        });
    }

    private void validateSingleItemBins(Long itemId, List<Long> binIds) {
        if (itemId == null || binIds == null || binIds.isEmpty()) {
            return;
        }
        List<WmsItemBin> itemBins = safeList(wmsItemBinMapper.selectList(new LambdaQueryWrapper<WmsItemBin>()
                .in(WmsItemBin::getBinId, binIds)));
        for (WmsItemBin itemBin : itemBins) {
            if (itemBin.getItemId() != null && !Objects.equals(itemBin.getItemId(), itemId)) {
                throw new BizException("库位已分配给其他物品: binId=" + itemBin.getBinId());
            }
        }

        List<WmsStock> stocks = safeList(wmsStockMapper.selectList(new LambdaQueryWrapper<WmsStock>()
                .in(WmsStock::getBinId, binIds)));
        for (WmsStock stock : stocks) {
            if (stock.getItemId() != null
                    && !Objects.equals(stock.getItemId(), itemId)
                    && stock.getQuantity() != null
                    && stock.getQuantity() > ItemConstants.DEFAULT_STOCK_QTY) {
                throw new BizException("库位已有其他物品库存: binId=" + stock.getBinId());
            }
        }
    }

    private ElectronicLabelVo enrichWithRelatedInfo(WmsElectronicLabel entity) {
        return enrich(converter.toVo(entity), loadEnrichment(List.of(entity)));
    }

    private LabelEnrichment loadEnrichment(List<WmsElectronicLabel> labels) {
        if (labels == null || labels.isEmpty()) {
            return new LabelEnrichment(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }
        Set<Long> itemIds = labels.stream().map(WmsElectronicLabel::getItemId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> binIds = labels.stream().map(WmsElectronicLabel::getBinId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, WmsItem> itemMap = itemIds.isEmpty() ? Map.of()
                : safeList(wmsItemMapper.selectBatchIds(itemIds)).stream()
                .collect(Collectors.toMap(WmsItem::getId, item -> item, (left, right) -> left));
        Set<Long> categoryIds = itemMap.values().stream().map(WmsItem::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> subCategoryIds = itemMap.values().stream().map(WmsItem::getSubCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> categoryMap = categoryIds.isEmpty() ? Map.of()
                : safeList(wmsCategoryMapper.selectBatchIds(categoryIds)).stream()
                .collect(Collectors.toMap(category -> category.getId(), category -> category.getCategoryName(), (left, right) -> left));
        Map<Long, String> subCategoryMap = subCategoryIds.isEmpty() ? Map.of()
                : safeList(wmsSubCategoryMapper.selectBatchIds(subCategoryIds)).stream()
                .collect(Collectors.toMap(subCategory -> subCategory.getId(), subCategory -> subCategory.getSubCategoryName(), (left, right) -> left));

        Map<Long, WmsBin> binMap = binIds.isEmpty() ? Map.of()
                : safeList(wmsBinMapper.selectBatchIds(binIds)).stream()
                .collect(Collectors.toMap(WmsBin::getId, bin -> bin, (left, right) -> left));
        Set<Long> cabinetIds = binMap.values().stream().map(WmsBin::getCabinetId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, WmsCabinet> cabinetMap = cabinetIds.isEmpty() ? Map.of()
                : safeList(wmsCabinetMapper.selectBatchIds(cabinetIds)).stream()
                .collect(Collectors.toMap(WmsCabinet::getId, cabinet -> cabinet, (left, right) -> left));
        Set<Long> areaIds = cabinetMap.values().stream().map(WmsCabinet::getAreaId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, WmsArea> areaMap = areaIds.isEmpty() ? Map.of()
                : safeList(wmsAreaMapper.selectBatchIds(areaIds)).stream()
                .collect(Collectors.toMap(WmsArea::getId, area -> area, (left, right) -> left));

        Set<Long> warehouseIds = new HashSet<>();
        binMap.values().stream().map(WmsBin::getWarehouseId).filter(Objects::nonNull).forEach(warehouseIds::add);
        cabinetMap.values().stream().map(WmsCabinet::getWarehouseId).filter(Objects::nonNull).forEach(warehouseIds::add);
        areaMap.values().stream().map(WmsArea::getWarehouseId).filter(Objects::nonNull).forEach(warehouseIds::add);
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty() ? Map.of()
                : safeList(wmsWarehouseMapper.selectBatchIds(warehouseIds)).stream()
                .collect(Collectors.toMap(WmsWarehouse::getId, warehouse -> warehouse, (left, right) -> left));
        Map<Long, Integer> stockMap = binIds.isEmpty() ? Map.of()
                : safeList(wmsStockMapper.selectList(new LambdaQueryWrapper<WmsStock>().in(WmsStock::getBinId, binIds))).stream()
                .collect(Collectors.groupingBy(WmsStock::getBinId,
                        Collectors.summingInt(stock -> stock.getQuantity() == null ? ItemConstants.DEFAULT_STOCK_QTY : stock.getQuantity())));

        return new LabelEnrichment(itemMap, categoryMap, subCategoryMap, binMap, cabinetMap, areaMap, warehouseMap, stockMap);
    }

    private ElectronicLabelVo enrich(ElectronicLabelVo vo, LabelEnrichment enrichment) {
        if (vo.getItemId() != null) {
            WmsItem item = enrichment.itemMap().get(vo.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
                vo.setCategoryName(item.getCategoryId() == null ? null : enrichment.categoryMap().get(item.getCategoryId()));
                vo.setSubCategoryName(item.getSubCategoryId() == null ? null : enrichment.subCategoryMap().get(item.getSubCategoryId()));
            }
        }
        if (vo.getBinId() != null) {
            WmsBin bin = enrichment.binMap().get(vo.getBinId());
            if (bin != null) {
                vo.setBinCode(bin.getBinCode());
                WmsCabinet cabinet = bin.getCabinetId() == null ? null : enrichment.cabinetMap().get(bin.getCabinetId());
                WmsArea area = cabinet == null || cabinet.getAreaId() == null ? null : enrichment.areaMap().get(cabinet.getAreaId());
                Long warehouseId = bin.getWarehouseId();
                if (warehouseId == null && cabinet != null) {
                    warehouseId = cabinet.getWarehouseId();
                }
                if (warehouseId == null && area != null) {
                    warehouseId = area.getWarehouseId();
                }
                WmsWarehouse warehouse = warehouseId == null ? null : enrichment.warehouseMap().get(warehouseId);
                vo.setLocationText(buildLocationText(warehouse, area, cabinet, bin));
            }
            vo.setStockQuantity(enrichment.stockMap().getOrDefault(vo.getBinId(), ItemConstants.DEFAULT_STOCK_QTY));
        }
        return vo;
    }

    private String buildLocationText(WmsWarehouse warehouse, WmsArea area, WmsCabinet cabinet, WmsBin bin) {
        return java.util.stream.Stream.of(
                        warehouse == null ? null : warehouse.getWarehouseName(),
                        area == null ? null : area.getAreaName(),
                        cabinet == null ? null : cabinet.getCabinetName(),
                        bin == null ? null : bin.getBinCode())
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining("/"));
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private <T> List<T> safeList(Collection<T> collection) {
        return collection == null ? List.of() : List.copyOf(collection);
    }

    private record LabelEnrichment(
            Map<Long, WmsItem> itemMap,
            Map<Long, String> categoryMap,
            Map<Long, String> subCategoryMap,
            Map<Long, WmsBin> binMap,
            Map<Long, WmsCabinet> cabinetMap,
            Map<Long, WmsArea> areaMap,
            Map<Long, WmsWarehouse> warehouseMap,
            Map<Long, Integer> stockMap) {
    }
}
