package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.exception.BizException;
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

    /** 区域编码前缀 */
    private static final String AREA_CODE_PREFIX = "QY";

    @Override
    public List<AreaVo> listByWarehouseId(Long warehouseId) {
        LambdaQueryWrapper<WmsArea> wrapper = new LambdaQueryWrapper<WmsArea>()
                .eq(WmsArea::getWarehouseId, warehouseId)
                .orderByAsc(WmsArea::getSortOrder)
                .orderByDesc(WmsArea::getCreateTime);
        List<WmsArea> list = wmsAreaMapper.selectList(wrapper);
        return list.stream().map(this::toAreaVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AreaVo create(AreaDto dto) {
        // 校验库房存在且启用
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == 1) {
            throw new BizException("库房不存在");
        }
        WmsArea area = new WmsArea();
        copyDtoToEntity(dto, area);
        // 自动生成区域编码: QY + 年月日 + 4位流水号
        area.setAreaCode(generateAreaCode());
        // 默认状态为启用
        if (area.getStatus() == null) {
            area.setStatus(1);
        }
        if (area.getSortOrder() == null) {
            area.setSortOrder(0);
        }
        wmsAreaMapper.insert(area);
        return toAreaVo(area);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AreaVo update(Long id, AreaDto dto) {
        WmsArea existing = wmsAreaMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("区域不存在");
        }
        // 校验库房存在
        WmsWarehouse warehouse = wmsWarehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getDelFlag() == 1) {
            throw new BizException("库房不存在");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编码
        existing.setAreaCode(null);
        wmsAreaMapper.updateById(existing);
        return toAreaVo(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsArea existing = wmsAreaMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == 1) {
            throw new BizException("区域不存在");
        }
        // 逻辑删除区域
        WmsArea updateEntity = new WmsArea();
        updateEntity.setId(id);
        updateEntity.setDelFlag(1);
        updateEntity.setLastOperType("d");
        wmsAreaMapper.updateById(updateEntity);
    }

    /**
     * 生成区域编码: QY + 年月日 + 4位流水号
     * 示例: QY202605140001
     */
    private String generateAreaCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<WmsArea> wrapper = new LambdaQueryWrapper<WmsArea>()
                .likeRight(WmsArea::getAreaCode, AREA_CODE_PREFIX + datePart)
                .orderByDesc(WmsArea::getAreaCode)
                .last("LIMIT 1");
        WmsArea last = wmsAreaMapper.selectOne(wrapper);
        int seq = 1;
        if (last != null && last.getAreaCode() != null) {
            String lastCode = last.getAreaCode();
            String seqStr = lastCode.substring(lastCode.length() - 4);
            try {
                seq = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                seq = 1;
            }
        }
        return AREA_CODE_PREFIX + datePart + String.format("%04d", seq);
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
     */
    private AreaVo toAreaVo(WmsArea area) {
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
        // 填充库房名称
        if (area.getWarehouseId() != null) {
            WmsWarehouse warehouse = wmsWarehouseMapper.selectById(area.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        return vo;
    }
}
