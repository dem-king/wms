package com.wms.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.constant.BizConstants;
import com.wms.common.constant.DelFlagConstants;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.exception.BizException;
import com.wms.common.util.SequenceGenerator;
import com.wms.warehouse.converter.CabinetConverter;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.dto.CabinetDto;
import com.wms.warehouse.domain.dto.CabinetLayoutBatchSaveDto;
import com.wms.warehouse.domain.dto.CabinetLayoutItemDto;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.domain.vo.CabinetLayoutSaveVo;
import com.wms.warehouse.domain.vo.CabinetVo;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import com.wms.warehouse.service.CabinetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    private final CabinetConverter cabinetConverter;

    /**
     * 按区域ID分页查询存放柜列表
     *
     * @param pageParam 分页参数
     * @param areaId 区域ID
     * @return 存放柜分页结果
     */
    @Override
    public PageResult<CabinetVo> page(PageParam pageParam, Long areaId) {
        LambdaQueryWrapper<WmsCabinet> wrapper = new LambdaQueryWrapper<WmsCabinet>()
                .eq(WmsCabinet::getAreaId, areaId)
                .orderByAsc(WmsCabinet::getSortOrder)
                .orderByAsc(WmsCabinet::getId);
        Page<WmsCabinet> page = wmsCabinetMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getSize()), wrapper);

        Set<Long> areaIds = page.getRecords().stream()
                .map(WmsCabinet::getAreaId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, WmsArea> areaMap = areaIds.isEmpty()
                ? Map.of()
                : wmsAreaMapper.selectBatchIds(areaIds).stream()
                        .collect(Collectors.toMap(WmsArea::getId, Function.identity()));

        PageResult<CabinetVo> result = new PageResult<>();
        result.setRecords(page.getRecords().stream()
                .sorted(Comparator
                        .comparing((WmsCabinet cabinet) -> cabinet.getSortOrder() == null ? BizConstants.DEFAULT_SORT_ORDER : cabinet.getSortOrder())
                        .thenComparing(WmsCabinet::getId))
                .map(cabinet -> cabinetConverter.toVo(cabinet, getAreaName(cabinet.getAreaId(), areaMap), null))
                .collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        result.setPage(pageParam.getPage());
        result.setSize(pageParam.getSize());
        return result;
    }

    /**
     * 按区域ID查询存放柜列表
     * 查询结果按排序号和主键稳定排序，便于前端布局回显
     *
     * @param areaId 区域ID
     * @return 存放柜VO列表
     */
    @Override
    public List<CabinetVo> listByAreaId(Long areaId) {
        LambdaQueryWrapper<WmsCabinet> wrapper = new LambdaQueryWrapper<WmsCabinet>()
                .eq(WmsCabinet::getAreaId, areaId)
                .orderByAsc(WmsCabinet::getSortOrder)
                .orderByAsc(WmsCabinet::getId);
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
        return list.stream()
                .sorted(Comparator
                        .comparing((WmsCabinet cabinet) -> cabinet.getSortOrder() == null ? BizConstants.DEFAULT_SORT_ORDER : cabinet.getSortOrder())
                        .thenComparing(WmsCabinet::getId))
                .map(cabinet -> cabinetConverter.toVo(cabinet, getAreaName(cabinet.getAreaId(), areaMap), null))
                .collect(Collectors.toList());
    }

    /**
     * 查询存放柜详情
     *
     * @param id 存放柜ID
     * @return 存放柜详情VO
     */
    @Override
    public CabinetVo getById(Long id) {
        WmsCabinet cabinet = wmsCabinetMapper.selectById(id);
        if (cabinet == null) {
            throw new BizException("存放柜不存在");
        }
        if (cabinet.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
        }
        WmsArea area = cabinet.getAreaId() == null ? null : wmsAreaMapper.selectById(cabinet.getAreaId());
        // 填充物品数量(占用库位数)
        Long occupiedCount = wmsBinMapper.selectCount(
                new LambdaQueryWrapper<WmsBin>()
                        .eq(WmsBin::getCabinetId, id)
                        .eq(WmsBin::getIsOccupied, WarehouseConstants.IS_OCCUPIED_YES)
        );
        return cabinetConverter.toVo(cabinet, area == null ? null : area.getAreaName(), occupiedCount.intValue());
    }

    /**
     * 新增存放柜
     * 自动生成编码并补全默认状态和默认排序号
     *
     * @param dto 存放柜新增参数
     * @return 新增后的存放柜VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CabinetVo create(CabinetDto dto) {
        // 校验区域存在且启用
        WmsArea area = wmsAreaMapper.selectById(dto.getAreaId());
        if (area == null) {
            throw new BizException("区域不存在");
        }
        if (area.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("区域已删除");
        }
        WmsCabinet cabinet = cabinetConverter.toEntity(dto);
        // 自动生成存放柜编码: CG + 年月日 + 4位流水号
        cabinet.setCabinetCode(generateCabinetCode());
        // 默认状态为启用
        if (cabinet.getStatus() == null) {
            cabinet.setStatus(BizConstants.STATUS_ENABLED);
        }
        if (cabinet.getSortOrder() == null) {
            cabinet.setSortOrder(BizConstants.DEFAULT_SORT_ORDER);
        }
        wmsCabinetMapper.insert(cabinet);
        return cabinetConverter.toVo(cabinet, area.getAreaName(), null);
    }

    /**
     * 更新存放柜
     *
     * @param id 存放柜ID
     * @param dto 存放柜更新参数
     * @return 更新后的存放柜VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CabinetVo update(Long id, CabinetDto dto) {
        WmsCabinet existing = wmsCabinetMapper.selectById(id);
        if (existing == null) {
            throw new BizException("存放柜不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
        }
        // 校验区域存在
        WmsArea area = wmsAreaMapper.selectById(dto.getAreaId());
        if (area == null) {
            throw new BizException("区域不存在");
        }
        if (area.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("区域已删除");
        }
        cabinetConverter.copyToEntity(dto, existing);
        existing.setId(id);
        // 编辑时不修改编码
        existing.setCabinetCode(null);
        if (existing.getSortOrder() == null) {
            existing.setSortOrder(BizConstants.DEFAULT_SORT_ORDER);
        }
        wmsCabinetMapper.updateById(existing);
        return cabinetConverter.toVo(existing, area.getAreaName(), null);
    }

    /**
     * 删除存放柜
     *
     * @param id 存放柜ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WmsCabinet existing = wmsCabinetMapper.selectById(id);
        if (existing == null) {
            throw new BizException("存放柜不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
        }
        // 逻辑删除存放柜
        WmsCabinet updateEntity = new WmsCabinet();
        updateEntity.setId(id);
        updateEntity.setDelFlag(DelFlagConstants.DELETED);

        wmsCabinetMapper.updateById(updateEntity);
    }

    /**
     * 更新存放柜位置坐标
     *
     * @param id 存放柜ID
     * @param x X坐标
     * @param y Y坐标
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePosition(Long id, Integer x, Integer y) {
        WmsCabinet existing = wmsCabinetMapper.selectById(id);
        if (existing == null) {
            throw new BizException("存放柜不存在");
        }
        if (existing.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("存放柜已删除");
        }
        WmsCabinet updateEntity = new WmsCabinet();
        updateEntity.setId(id);
        updateEntity.setPositionX(x);
        updateEntity.setPositionY(y);
        wmsCabinetMapper.updateById(updateEntity);
    }

    /**
     * 批量保存存放柜布局
     * 仅更新位置和排序字段，确保返回保存后的布局结果
     *
     * @param dto 存放柜布局批量保存参数
     * @return 保存后的布局结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CabinetLayoutSaveVo saveLayout(CabinetLayoutBatchSaveDto dto) {
        List<Long> cabinetIds = dto.getCabinets().stream()
                .map(CabinetLayoutItemDto::getId)
                .toList();
        List<WmsCabinet> cabinets = wmsCabinetMapper.selectBatchIds(cabinetIds);
        if (cabinets.size() != cabinetIds.size()) {
            throw new BizException("存放柜不存在");
        }
        Map<Long, WmsCabinet> cabinetMap = cabinets.stream()
                .collect(Collectors.toMap(WmsCabinet::getId, Function.identity()));

        // 保存前校验柜体归属，避免跨区域写入错误布局
        for (CabinetLayoutItemDto item : dto.getCabinets()) {
            WmsCabinet cabinet = cabinetMap.get(item.getId());
            if (cabinet == null) {
                throw new BizException("存放柜不存在");
            }
            if (cabinet.getDelFlag() == DelFlagConstants.DELETED) {
                throw new BizException("存放柜已删除");
            }
            if (!dto.getAreaId().equals(cabinet.getAreaId())) {
                throw new BizException("存放柜不属于当前区域");
            }
        }

        List<CabinetVo> savedCabinets = dto.getCabinets().stream()
                .sorted(Comparator.comparing(CabinetLayoutItemDto::getSortOrder).thenComparing(CabinetLayoutItemDto::getId))
                .map(item -> {
                    WmsCabinet updateEntity = new WmsCabinet();
                    updateEntity.setId(item.getId());
                    updateEntity.setPositionX(item.getPositionX());
                    updateEntity.setPositionY(item.getPositionY());
                    updateEntity.setSortOrder(item.getSortOrder());
                    wmsCabinetMapper.updateById(updateEntity);

                    WmsCabinet source = cabinetMap.get(item.getId());
                    source.setPositionX(item.getPositionX());
                    source.setPositionY(item.getPositionY());
                    source.setSortOrder(item.getSortOrder());
                    return cabinetConverter.toVo(source, null, null);
                })
                .collect(Collectors.toList());

        CabinetLayoutSaveVo result = new CabinetLayoutSaveVo();
        result.setAreaId(dto.getAreaId());
        result.setCabinets(savedCabinets);
        return result;
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
     * 获取区域名称
     * 优先复用批量查询结果，避免列表接口产生N+1查询
     *
     * @param areaId 区域ID
     * @param areaMap 区域映射
     * @return 区域名称
     */
    private String getAreaName(Long areaId, Map<Long, WmsArea> areaMap) {
        if (areaId == null) {
            return null;
        }
        WmsArea area = areaMap.get(areaId);
        return area == null ? null : area.getAreaName();
    }
}
