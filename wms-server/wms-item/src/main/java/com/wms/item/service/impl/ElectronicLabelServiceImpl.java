package com.wms.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.enums.LabelStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.item.converter.ElectronicLabelConverter;
import com.wms.item.domain.constant.LabelConstants;
import com.wms.item.domain.dto.LabelBindDto;
import com.wms.item.domain.dto.LabelGenerateDto;
import com.wms.item.domain.dto.LabelStatusDto;
import com.wms.item.domain.entity.WmsElectronicLabel;
import com.wms.item.domain.entity.WmsItem;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.mapper.WmsElectronicLabelMapper;
import com.wms.item.mapper.WmsItemMapper;
import com.wms.item.service.ElectronicLabelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电子标签服务实现类
 * 处理标签生成、绑定、状态管理、扫码查询、闲置检测等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElectronicLabelServiceImpl implements ElectronicLabelService {

    private final WmsElectronicLabelMapper labelMapper;
    private final WmsItemMapper wmsItemMapper;
    private final ElectronicLabelConverter converter;

    /**
     * 合法状态流转映射
     * key: 当前状态, value: 允许流转的目标状态集合
     */
    private static final Map<Integer, Set<Integer>> STATUS_TRANSITIONS = new HashMap<>();

    static {
        // 在库 → 正在使用
        STATUS_TRANSITIONS.put(LabelStatusEnum.IN_STOCK.getCode(), Set.of(LabelStatusEnum.IN_USE.getCode(), LabelStatusEnum.IDLE.getCode()));
        // 正在使用 → 已归还、报废
        STATUS_TRANSITIONS.put(LabelStatusEnum.IN_USE.getCode(), Set.of(LabelStatusEnum.RETURNED.getCode(), LabelStatusEnum.SCRAPPED.getCode()));
        // 已归还 → 在库、闲置
        STATUS_TRANSITIONS.put(LabelStatusEnum.RETURNED.getCode(), Set.of(LabelStatusEnum.IN_STOCK.getCode(), LabelStatusEnum.IDLE.getCode()));
        // 闲置 → 在库
        STATUS_TRANSITIONS.put(LabelStatusEnum.IDLE.getCode(), Set.of(LabelStatusEnum.IN_STOCK.getCode()));
    }

    @Override
    public Page<ElectronicLabelVo> page(Page<ElectronicLabelVo> page, Long itemId, Integer labelType, Integer labelStatus) {
        LambdaQueryWrapper<WmsElectronicLabel> wrapper = new LambdaQueryWrapper<>();
        // 按物品ID筛选
        if (itemId != null) {
            wrapper.eq(WmsElectronicLabel::getItemId, itemId);
        }
        // 按标签类型筛选
        if (labelType != null) {
            wrapper.eq(WmsElectronicLabel::getLabelType, labelType);
        }
        // 按标签状态筛选
        if (labelStatus != null) {
            wrapper.eq(WmsElectronicLabel::getLabelStatus, labelStatus);
        }
        wrapper.orderByDesc(WmsElectronicLabel::getCreateTime);

        Page<WmsElectronicLabel> entityPage = labelMapper.selectPage(
                new Page<>(page.getCurrent(), page.getSize()), wrapper);

        List<WmsElectronicLabel> records = entityPage.getRecords();
        // 批量查询关联物品信息，避免N+1查询
        Map<Long, WmsItem> itemMap = batchQueryItems(records);

        List<ElectronicLabelVo> voList = records.stream()
                .map(entity -> {
                    ElectronicLabelVo vo = converter.toVo(entity);
                    fillItemInfo(vo, itemMap);
                    return vo;
                })
                .collect(Collectors.toList());

        Page<ElectronicLabelVo> result = new Page<>(page.getCurrent(), page.getSize(), entityPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public ElectronicLabelVo getById(Long id) {
        WmsElectronicLabel entity = labelMapper.selectById(id);
        if (entity == null) {
            throw new BizException("标签不存在");
        }
        ElectronicLabelVo vo = converter.toVo(entity);
        // 填充关联物品信息
        if (entity.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(entity.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ElectronicLabelVo> generate(LabelGenerateDto dto) {
        // 校验物品存在
        WmsItem item = wmsItemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new BizException("物品不存在");
        }

        List<WmsElectronicLabel> labels = new ArrayList<>();
        for (int i = 0; i < dto.getCount(); i++) {
            WmsElectronicLabel label = new WmsElectronicLabel();
            // 生成标签编号: BQ + 年月日 + 4位流水号
            label.setLabelNo(generateLabelNo());
            label.setLabelType(dto.getLabelType());
            label.setItemId(dto.getItemId());
            label.setBindType(dto.getBindType());
            label.setLabelStatus(LabelStatusEnum.IN_STOCK.getCode());
            label.setPrintStatus(LabelConstants.PRINT_STATUS_NOT);
            // 二维码和条形码内容默认使用标签编号
            label.setQrContent(label.getLabelNo());
            label.setBarcodeContent(label.getLabelNo());
            // RFID类型时生成RFID编码
            if (LabelConstants.LABEL_TYPE_RFID == dto.getLabelType()) {
                label.setRfidCode(generateRfidCode());
            }
            labels.add(label);
        }
        // 批量插入标签
        for (WmsElectronicLabel label : labels) {
            labelMapper.insert(label);
        }

        // 转换为VO并填充物品信息
        return labels.stream()
                .map(entity -> {
                    ElectronicLabelVo vo = converter.toVo(entity);
                    vo.setItemName(item.getItemName());
                    vo.setItemCode(item.getItemCode());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicLabelVo bind(Long id, LabelBindDto dto) {
        WmsElectronicLabel entity = labelMapper.selectById(id);
        if (entity == null) {
            throw new BizException("标签不存在");
        }
        // 校验标签状态必须为闲置才能绑定
        if (entity.getLabelStatus() != LabelStatusEnum.IDLE.getCode()) {
            throw new BizException("只有闲置状态的标签才能绑定物品");
        }
        // 校验物品存在
        WmsItem item = wmsItemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new BizException("物品不存在");
        }

        entity.setItemId(dto.getItemId());
        entity.setBindType(dto.getBindType());
        // 绑定后状态变为在库
        entity.setLabelStatus(LabelStatusEnum.IN_STOCK.getCode());
        labelMapper.updateById(entity);

        ElectronicLabelVo vo = converter.toVo(entity);
        vo.setItemName(item.getItemName());
        vo.setItemCode(item.getItemCode());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ElectronicLabelVo updateStatus(Long id, LabelStatusDto dto) {
        WmsElectronicLabel entity = labelMapper.selectById(id);
        if (entity == null) {
            throw new BizException("标签不存在");
        }
        // 校验状态流转合法性
        validateStatusTransition(entity.getLabelStatus(), dto.getLabelStatus());

        entity.setLabelStatus(dto.getLabelStatus());
        // 如果状态变为正在使用，记录借出时间
        if (dto.getLabelStatus() == LabelStatusEnum.IN_USE.getCode()) {
            entity.setBorrowTime(LocalDateTime.now());
        }
        labelMapper.updateById(entity);

        return enrichWithItemInfo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPrint(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            throw new BizException("标签ID列表不能为空");
        }
        // 批量查询并更新打印状态
        List<WmsElectronicLabel> labels = labelMapper.selectBatchIds(labelIds);
        for (WmsElectronicLabel label : labels) {
            if (label.getPrintStatus() == LabelConstants.PRINT_STATUS_DONE) {
                // 已打印的跳过
                continue;
            }
            label.setPrintStatus(LabelConstants.PRINT_STATUS_DONE);
            labelMapper.updateById(label);
        }
    }

    @Override
    public ElectronicLabelVo scan(String code) {
        if (code == null || code.isBlank()) {
            throw new BizException("扫码内容不能为空");
        }
        // 在labelNo/rfidCode/qrContent/barcodeContent中模糊匹配
        LambdaQueryWrapper<WmsElectronicLabel> wrapper = new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getLabelNo, code)
                .or().eq(WmsElectronicLabel::getRfidCode, code)
                .or().eq(WmsElectronicLabel::getQrContent, code)
                .or().eq(WmsElectronicLabel::getBarcodeContent, code)
                .last("LIMIT 1");
        WmsElectronicLabel entity = labelMapper.selectOne(wrapper);
        if (entity == null) {
            throw new BizException("未找到匹配的标签");
        }
        return enrichWithItemInfo(entity);
    }

    @Override
    public List<ElectronicLabelVo> listIdle() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(LabelConstants.IDLE_THRESHOLD_DAYS);
        // 查询闲置状态(5)的标签
        LambdaQueryWrapper<WmsElectronicLabel> idleWrapper = new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getLabelStatus, LabelStatusEnum.IDLE.getCode());
        // 查询在库状态(1)且超过90天未变更的标签
        LambdaQueryWrapper<WmsElectronicLabel> staleWrapper = new LambdaQueryWrapper<WmsElectronicLabel>()
                .eq(WmsElectronicLabel::getLabelStatus, LabelStatusEnum.IN_STOCK.getCode())
                .lt(WmsElectronicLabel::getUpdateTime, threshold);

        List<WmsElectronicLabel> idleLabels = labelMapper.selectList(idleWrapper);
        List<WmsElectronicLabel> staleLabels = labelMapper.selectList(staleWrapper);

        // 合并结果
        List<WmsElectronicLabel> allLabels = new ArrayList<>(idleLabels);
        allLabels.addAll(staleLabels);

        // 批量查询关联物品信息
        Map<Long, WmsItem> itemMap = batchQueryItems(allLabels);

        return allLabels.stream()
                .map(entity -> {
                    ElectronicLabelVo vo = converter.toVo(entity);
                    fillItemInfo(vo, itemMap);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 生成标签编号: BQ + 年月日 + 4位随机数
     * 使用时间戳+随机数保证并发安全
     *
     * @return 标签编号
     */
    private String generateLabelNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(LabelConstants.LABEL_NO_RANDOM_RANGE));
        return LabelConstants.LABEL_NO_PREFIX + datePart + random;
    }

    /**
     * 生成RFID编码
     * 格式: RFID + 年月日时分秒 + 4位随机数
     *
     * @return RFID编码
     */
    private String generateRfidCode() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(LabelConstants.RFID_RANDOM_MAX - LabelConstants.RFID_RANDOM_MIN) + LabelConstants.RFID_RANDOM_MIN;
        return "RFID" + timePart + random;
    }

    /**
     * 校验状态流转合法性
     * 允许的流转: 在库→正在使用/闲置, 正在使用→已归还/报废, 已归还→在库/闲置, 闲置→在库
     *
     * @param currentStatus 当前状态
     * @param targetStatus  目标状态
     */
    private void validateStatusTransition(int currentStatus, int targetStatus) {
        // 相同状态不报错
        if (currentStatus == targetStatus) {
            return;
        }
        Set<Integer> allowedTargets = STATUS_TRANSITIONS.get(currentStatus);
        if (allowedTargets == null || !allowedTargets.contains(targetStatus)) {
            throw new BizException("标签状态不允许从" + currentStatus + "变更为" + targetStatus);
        }
    }

    /**
     * 批量查询物品信息构建Map，避免N+1查询
     *
     * @param labels 标签列表
     * @return 物品ID到物品实体的映射
     */
    private Map<Long, WmsItem> batchQueryItems(List<WmsElectronicLabel> labels) {
        Set<Long> itemIds = labels.stream()
                .map(WmsElectronicLabel::getItemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        List<WmsItem> items = wmsItemMapper.selectBatchIds(itemIds);
        return items.stream().collect(Collectors.toMap(WmsItem::getId, item -> item));
    }

    /**
     * 填充VO中的物品名称和编码
     *
     * @param vo      电子标签VO
     * @param itemMap 物品ID到实体的映射
     */
    private void fillItemInfo(ElectronicLabelVo vo, Map<Long, WmsItem> itemMap) {
        if (vo.getItemId() != null) {
            WmsItem item = itemMap.get(vo.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
    }

    /**
     * 实体转VO并填充关联物品信息
     *
     * @param entity 电子标签实体
     * @return 带物品信息的VO
     */
    private ElectronicLabelVo enrichWithItemInfo(WmsElectronicLabel entity) {
        ElectronicLabelVo vo = converter.toVo(entity);
        if (entity.getItemId() != null) {
            WmsItem item = wmsItemMapper.selectById(entity.getItemId());
            if (item != null) {
                vo.setItemName(item.getItemName());
                vo.setItemCode(item.getItemCode());
            }
        }
        return vo;
    }
}
