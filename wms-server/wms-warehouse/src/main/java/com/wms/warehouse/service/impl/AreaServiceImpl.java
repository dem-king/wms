package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.dto.AreaDto;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsWarehouse;
import com.wms.warehouse.domain.vo.AreaVo;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsWarehouseMapper;
import com.wms.warehouse.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 存放区域服务实现类
 * 处理区域CRUD、按库房查询等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final WmsAreaMapper wmsAreaMapper;
    private final WmsWarehouseMapper wmsWarehouseMapper;
    private final SequenceGenerator sequenceGenerator;

    @Override
    public List<AreaVo> listByWarehouseId(Long warehouseId) {
        LambdaQueryWrapper<WmsArea> wrapper = new LambdaQueryWrapper<WmsArea>()
                .eq(WmsArea::getWarehouseId, warehouseId)
                .orderByAsc(WmsArea::getSortOrder)
                .orderByDesc(WmsArea::getCreateTime);
        List<WmsArea> list = wmsAreaMapper.selectList(wrapper);
        // 批量查询库房构建Map，避免N+1查询
        Set<Long> warehouseIds = list.stream()
                .map(WmsArea::getWarehouseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, WmsWarehouse> warehouseMap = warehouseIds.isEmpty()
                ? Map.of()
                : wmsWarehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(WmsWarehouse::getId, Function.identity()));
        return list.stream().map(area -> toAreaVo(area, warehouseMap)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AreaVo create(AreaDto dto) {
        // 校验库房存在且启用
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房不存在");
        }
        WmsArea area = new WmsArea();
        copyDtoToEntity(dto, area);
        // 自动生成区域编码: QY + 年月日 + 4位流水号
        area.setAreaCode(generateAreaCode());
        // 默认状态为启用
        if (area.getStatus() == null) {
            area.setStatus(BizConstants.STATUS_ENABLED);
        }
        if (area.getSortOrder() == null) {
            area.setSortOrder(BizConstants.DEFAULT_SORT_ORDER);
        }
        wmsAreaMapper.insert(area);
        return toAreaVo(area, Map.of());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AreaVo update(Long id, AreaDto dto) {
        WmsArea existing = wmsAreaMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("区域不存在");
        }
        // 校验库房存在
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库房不存在");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编码
        existing.setAreaCode(null);
        wmsAreaMapper.updateById(existing);
        return toAreaVo(existing, Map.of());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsArea existing = wmsAreaMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("区域不存在");
        }
        // 逻辑删除区域
        WmsArea updateEntity = new WmsArea();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);
        
        wmsAreaMapper.updateById(updateEntity);
    }

    /**
     * 生成区域编码: QY + 年月日 + 4位流水号
     * 基于Redis INCR原子操作保证并发安全
     * 示例: QY202605140001
     */
    private String generateAreaCode() {
        return sequenceGenerator.next(WarehouseConstants.AREA_CODE_PREFIX);
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(AreaDto dto, WmsArea entity) {
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setAreaName(dto.getAreaName());
        entity.setAreaCode(dto.getAreaCode());
        entity.setAreaType(dto.getAreaType());
        entity.setSortOrder(dto.getSortOrder());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
    }

    /**
     * WmsArea实体转AreaVo(填充库房名称)
     *
     * @param area 区域实体
     * @param warehouseMap 库房ID到实体的映射，避免N+1查询
     */
    private AreaVo toAreaVo(WmsArea area, Map<Long, WmsWarehouse> warehouseMap) {
        AreaVo vo = new AreaVo();
        vo.setId(area.getId());
        vo.setWarehouseId(area.getWarehouseId());
        vo.setAreaName(area.getAreaName());
        vo.setAreaCode(area.getAreaCode());
        vo.setAreaType(area.getAreaType());
        vo.setSortOrder(area.getSortOrder());
        vo.setStatus(area.getStatus());
        vo.setRemark(area.getRemark());
        vo.setCreateTime(area.getCreateTime());
        // 从Map中填充库房名称
        if (area.getWarehouseId() != null) {
            WmsWarehouse warehouse = warehouseMap.get(area.getWarehouseId());
            if (warehouse == null) {
                warehouse = wmsWarehouseMapper.selectById(area.getWarehouseId());
            }
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        return vo;
    }
}
