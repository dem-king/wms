package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.wms.common.constant.BizConstants;

import com.wms.common.exception.BizException;
import com.wms.common.util.LogicDeleteHelper;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.converter.LayoutElementConverter;
import com.wms.warehouse.domain.constant.LayoutElementConstants;
import com.wms.warehouse.domain.dto.LayoutElementBatchSaveDto;
import com.wms.warehouse.domain.dto.LayoutElementDto;
import com.wms.warehouse.domain.dto.LayoutElementUpdateItemDto;
import com.wms.warehouse.domain.entity.WmsLayoutElement;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.LayoutElementBatchSaveVo;
import com.wms.warehouse.domain.vo.LayoutElementFailedItemVo;
import com.wms.warehouse.domain.vo.LayoutElementVo;
import com.wms.warehouse.mapper.WmsLayoutElementMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import com.wms.warehouse.service.LayoutElementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 库房布局元素服务实现类
 * 处理布局元素CRUD、按库房查询、批量保存等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LayoutElementServiceImpl implements LayoutElementService {

    private final WmsLayoutElementMapper layoutElementMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SequenceGenerator sequenceGenerator;
    private final LayoutElementConverter layoutElementConverter;

    /**
     * 按库房ID查询布局元素列表
     * 可选按区域ID筛选，按排序号和创建时间排序
     *
     * @param warehouseId 库房ID
     * @param areaId     区域ID（可选，为null时不筛选）
     * @return 布局元素VO列表
     */
    @Override
    public List<LayoutElementVo> listByWarehouseId(Long warehouseId, Long areaId) {
        LambdaQueryWrapper<WmsLayoutElement> wrapper = new LambdaQueryWrapper<WmsLayoutElement>()
                .eq(WmsLayoutElement::getWarehouseId, warehouseId);
        // 按区域ID筛选（可选）
        if (areaId != null) {
            wrapper.eq(WmsLayoutElement::getAreaId, areaId);
        }
        wrapper.orderByAsc(WmsLayoutElement::getSortOrder)
                .orderByDesc(WmsLayoutElement::getCreateTime);
        List<WmsLayoutElement> list = layoutElementMapper.selectList(wrapper);
        return layoutElementConverter.toVoList(list);
    }

    /**
     * 新增布局元素
     * 校验库房存在且启用，自动生成元素编码，默认状态为启用
     *
     * @param dto 布局元素创建参数
     * @return 新增后的布局元素VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LayoutElementVo create(LayoutElementDto dto) {
        // 校验库房存在且启用（selectById已自动过滤逻辑删除记录，无需再判delFlag）
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BizException("库房不存在或已删除");
        }
        if (warehouse.getStatus() == BizConstants.STATUS_DISABLED) {
            throw new BizException("库房已禁用");
        }

        WmsLayoutElement entity = layoutElementConverter.toEntity(dto);
        // 自动生成元素编码: LE + 年月日 + 4位流水号
        entity.setElementCode(generateElementCode());
        // 默认状态为启用
        if (entity.getStatus() == null) {
            entity.setStatus(BizConstants.STATUS_ENABLED);
        }
        if (entity.getSortOrder() == null) {
            entity.setSortOrder(BizConstants.DEFAULT_SORT_ORDER);
        }
        layoutElementMapper.insert(entity);
        return layoutElementConverter.toVo(entity);
    }

    /**
     * 更新布局元素
     * 校验元素存在且未删除
     *
     * @param id  布局元素ID
     * @param dto 布局元素更新参数
     * @return 更新后的布局元素VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LayoutElementVo update(Long id, LayoutElementDto dto) {
        // 校验元素存在（selectById已自动过滤逻辑删除记录）
        WmsLayoutElement existing = layoutElementMapper.selectById(id);
        if (existing == null) {
            throw new BizException("布局元素不存在或已删除");
        }

        // 将Dto属性拷贝到existing，编辑时不修改编码
        existing.setWarehouseId(dto.getWarehouseId());
        existing.setAreaId(dto.getAreaId());
        existing.setElementName(dto.getElementName());
        existing.setElementType(dto.getElementType());
        existing.setShapeType(dto.getShapeType());
        existing.setPositionX(dto.getPositionX());
        existing.setPositionY(dto.getPositionY());
        existing.setLayoutWidth(dto.getLayoutWidth());
        existing.setLayoutHeight(dto.getLayoutHeight());
        existing.setRotation(dto.getRotation());
        existing.setPointData(dto.getPointData());
        existing.setStyleData(dto.getStyleData());
        existing.setLabelText(dto.getLabelText());
        existing.setSortOrder(dto.getSortOrder());
        existing.setRemark(dto.getRemark());
        existing.setId(id);
        layoutElementMapper.updateById(existing);
        return layoutElementConverter.toVo(existing);
    }

    /**
     * 删除布局元素(逻辑删除)
     *
     * @param id 布局元素ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 校验元素存在（selectById已自动过滤逻辑删除记录）
        WmsLayoutElement existing = layoutElementMapper.selectById(id);
        if (existing == null) {
            throw new BizException("布局元素不存在或已删除");
        }
        // 使用LogicDeleteHelper执行逻辑删除，避免@TableLogic字段更新问题
        LogicDeleteHelper.markDeleted(layoutElementMapper, WmsLayoutElement.class, id);
    }

    /**
     * 批量保存布局元素
     * 遍历created/updated/deletedIds分别执行新增/更新/逻辑删除，
     * 统计成功和失败数量，返回BatchSaveVo
     *
     * @param dto 批量保存参数
     * @return 批量保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LayoutElementBatchSaveVo batchSave(LayoutElementBatchSaveDto dto) {
        int createdCount = 0;
        int updatedCount = 0;
        int deletedCount = 0;
        List<LayoutElementFailedItemVo> failedItems = new ArrayList<>();

        // 处理新增
        if (dto.getCreated() != null) {
            for (LayoutElementDto createDto : dto.getCreated()) {
                try {
                    create(createDto);
                    createdCount++;
                } catch (BizException e) {
                    // 记录失败项
                    LayoutElementFailedItemVo failedItem = new LayoutElementFailedItemVo();
                    failedItem.setElementName(createDto.getElementName());
                    failedItem.setReason(e.getMessage());
                    failedItems.add(failedItem);
                    log.warn("批量保存-新增布局元素失败: elementName={}, reason={}",
                            createDto.getElementName(), e.getMessage());
                }
            }
        }

        // 处理更新
        if (dto.getUpdated() != null) {
            for (LayoutElementUpdateItemDto updateDto : dto.getUpdated()) {
                try {
                    // 校验元素存在（selectById已自动过滤逻辑删除记录）
                    WmsLayoutElement existing = layoutElementMapper.selectById(updateDto.getId());
                    if (existing == null) {
                        throw new BizException("布局元素不存在或已删除");
                    }
                    // 将UpdateItemDto属性拷贝到existing，编辑时不修改编码
                    layoutElementConverter.copyToEntity(updateDto, existing);
                    existing.setId(updateDto.getId());
                    layoutElementMapper.updateById(existing);
                    updatedCount++;
                } catch (BizException e) {
                    // 记录失败项
                    LayoutElementFailedItemVo failedItem = new LayoutElementFailedItemVo();
                    failedItem.setElementName(updateDto.getElementName());
                    failedItem.setReason(e.getMessage());
                    failedItems.add(failedItem);
                    log.warn("批量保存-更新布局元素失败: id={}, elementName={}, reason={}",
                            updateDto.getId(), updateDto.getElementName(), e.getMessage());
                }
            }
        }

        // 处理删除
        if (dto.getDeletedIds() != null) {
            for (Long deleteId : dto.getDeletedIds()) {
                try {
                    // 校验元素存在（selectById已自动过滤逻辑删除记录）
                    WmsLayoutElement existing = layoutElementMapper.selectById(deleteId);
                    if (existing == null) {
                        throw new BizException("布局元素不存在或已删除");
                    }
                    // 使用LogicDeleteHelper执行逻辑删除
                    LogicDeleteHelper.markDeleted(layoutElementMapper, WmsLayoutElement.class, deleteId);
                    deletedCount++;
                } catch (BizException e) {
                    // 记录失败项
                    LayoutElementFailedItemVo failedItem = new LayoutElementFailedItemVo();
                    failedItem.setElementName("id=" + deleteId);
                    failedItem.setReason(e.getMessage());
                    failedItems.add(failedItem);
                    log.warn("批量保存-删除布局元素失败: id={}, reason={}", deleteId, e.getMessage());
                }
            }
        }

        LayoutElementBatchSaveVo result = new LayoutElementBatchSaveVo();
        result.setCreatedCount(createdCount);
        result.setUpdatedCount(updatedCount);
        result.setDeletedCount(deletedCount);
        result.setFailedItems(failedItems);
        return result;
    }

    /**
     * 生成元素编码: LE + 年月日 + 4位流水号
     * 基于Redis INCR原子操作保证并发安全
     * 示例: LE202605140001
     */
    private String generateElementCode() {
        return sequenceGenerator.next(LayoutElementConstants.ELEMENT_CODE_PREFIX);
    }
}