package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.dto.CabinetDto;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.CabinetVo;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.service.CabinetService;
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
 * 存放柜服务实现类
 * 处理存放柜CRUD、按区域查询、详情查询、位置更新等业务逻辑
 */
@Service
@RequiredArgsConstructor
public class CabinetServiceImpl implements CabinetService {

    private final WmsCabinetMapper wmsCabinetMapper;
    private final WmsAreaMapper wmsAreaMapper;
    private final WmsBinMapper wmsBinMapper;
    private final SequenceGenerator sequenceGenerator;

    @Override
    public List<CabinetVo> listByAreaId(Long areaId) {
        LambdaQueryWrapper<WmsCabinet> wrapper = new LambdaQueryWrapper<WmsCabinet>()
                .eq(WmsCabinet::getAreaId, areaId)
                .orderByDesc(WmsCabinet::getCreateTime);
        List<WmsCabinet> list = wmsCabinetMapper.selectList(wrapper);
        // 批量查询区域构建Map，避免N+1查询
        Set<Long> areaIds = list.stream()
                .map(WmsCabinet::getAreaId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, WmsArea> areaMap = areaIds.isEmpty()
                ? Map.of()
                : wmsAreaMapper.selectBatchIds(areaIds).stream()
                        .collect(Collectors.toMap(WmsArea::getId, Function.identity()));
        return list.stream().map(cabinet -> toCabinetVo(cabinet, areaMap)).collect(Collectors.toList());
    }

    @Override
    public CabinetVo getById(Long id) {
        WmsCabinet cabinet = wmsCabinetMapper.selectById(id);
        if (cabinet == null || cabinet.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜不存在");
        }
        CabinetVo vo = toCabinetVo(cabinet, Map.of());
        // 填充物品数量(占用库位数)
        Long occupiedCount = wmsBinMapper.selectCount(
                new LambdaQueryWrapper<WmsBin>()
                        .eq(WmsBin::getCabinetId, id)
                        .eq(WmsBin::getIsOccupied, WarehouseConstants.IS_OCCUPIED_YES)
        );
        vo.setItemCount(occupiedCount.intValue());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CabinetVo create(CabinetDto dto) {
        // 校验区域存在且启用
        WmsArea area = wmsAreaMapper.selectById(dto.getAreaId());
        if (area == null || area.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("区域不存在");
        }
        WmsCabinet cabinet = new WmsCabinet();
        copyDtoToEntity(dto, cabinet);
        // 自动生成存放柜编码: CG + 年月日 + 4位流水号
        cabinet.setCabinetCode(generateCabinetCode());
        // 默认状态为启用
        if (cabinet.getStatus() == null) {
            cabinet.setStatus(BizConstants.STATUS_ENABLED);
        }
        wmsCabinetMapper.insert(cabinet);
        return toCabinetVo(cabinet, Map.of());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CabinetVo update(Long id, CabinetDto dto) {
        WmsCabinet existing = wmsCabinetMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜不存在");
        }
        // 校验区域存在
        WmsArea area = wmsAreaMapper.selectById(dto.getAreaId());
        if (area == null || area.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("区域不存在");
        }
        copyDtoToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编码
        existing.setCabinetCode(null);
        wmsCabinetMapper.updateById(existing);
        return toCabinetVo(existing, Map.of());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsCabinet existing = wmsCabinetMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜不存在");
        }
        // 逻辑删除存放柜
        WmsCabinet updateEntity = new WmsCabinet();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);

        wmsCabinetMapper.updateById(updateEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(Long id, Integer x, Integer y) {
        WmsCabinet existing = wmsCabinetMapper.selectById(id);
        if (existing == null || existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜不存在");
        }
        WmsCabinet updateEntity = new WmsCabinet();
        updateEntity.setId(id);
        updateEntity.setPositionX(x);
        updateEntity.setPositionY(y);
        wmsCabinetMapper.updateById(updateEntity);
    }

    /**
     * 生成存放柜编码: CG + 年月日 + 4位流水号
     * 基于Redis INCR原子操作保证并发安全
     * 示例: CG202605140001
     */
    private String generateCabinetCode() {
        return sequenceGenerator.next(WarehouseConstants.CABINET_CODE_PREFIX);
    }

    /**
     * DTO属性拷贝到Entity
     */
    private void copyDtoToEntity(CabinetDto dto, WmsCabinet entity) {
        entity.setAreaId(dto.getAreaId());
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setCabinetName(dto.getCabinetName());
        entity.setCabinetCode(dto.getCabinetCode());
        entity.setPositionX(dto.getPositionX());
        entity.setPositionY(dto.getPositionY());
        entity.setCabinetType(dto.getCabinetType());
        entity.setRows(dto.getRows());
        entity.setCols(dto.getCols());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
    }

    /**
     * WmsCabinet实体转CabinetVo(填充区域名称)
     *
     * @param cabinet 存放柜实体
     * @param areaMap 区域ID到实体的映射，避免N+1查询
     */
    private CabinetVo toCabinetVo(WmsCabinet cabinet, Map<Long, WmsArea> areaMap) {
        CabinetVo vo = new CabinetVo();
        vo.setId(cabinet.getId());
        vo.setAreaId(cabinet.getAreaId());
        vo.setWarehouseId(cabinet.getWarehouseId());
        vo.setCabinetName(cabinet.getCabinetName());
        vo.setCabinetCode(cabinet.getCabinetCode());
        vo.setPositionX(cabinet.getPositionX());
        vo.setPositionY(cabinet.getPositionY());
        vo.setCabinetType(cabinet.getCabinetType());
        vo.setRows(cabinet.getRows());
        vo.setCols(cabinet.getCols());
        vo.setStatus(cabinet.getStatus());
        vo.setRemark(cabinet.getRemark());
        vo.setCreateTime(cabinet.getCreateTime());
        // 从Map中填充区域名称
        if (cabinet.getAreaId() != null) {
            WmsArea area = areaMap.get(cabinet.getAreaId());
            if (area == null) {
                area = wmsAreaMapper.selectById(cabinet.getAreaId());
            }
            if (area != null) {
                vo.setAreaName(area.getAreaName());
            }
        }
        return vo;
    }
}
