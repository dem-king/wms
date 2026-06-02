package com.wms.business.service.support;

import com.wms.common.constant.DelFlagConstants;
import com.wms.common.exception.BizException;
import com.wms.warehouse.domain.constant.WarehouseConstants;
import com.wms.warehouse.domain.entity.WmsArea;
import com.wms.warehouse.domain.entity.WmsBin;
import com.wms.warehouse.domain.entity.WmsCabinet;
import com.wms.warehouse.mapper.WmsAreaMapper;
import com.wms.warehouse.mapper.WmsBinMapper;
import com.wms.warehouse.mapper.WmsCabinetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 单据明细库位所属库房校验器。
 * 通过库位、存放柜、区域逐级解析库房，兼容历史数据中冗余 warehouseId 为空的情况。
 */
@Component
@RequiredArgsConstructor
public class BinWarehouseValidator {

    private final WmsBinMapper wmsBinMapper;
    private final WmsCabinetMapper wmsCabinetMapper;
    private final WmsAreaMapper wmsAreaMapper;

    /**
     * 校验库位归属指定库房。
     *
     * @param binIds 库位ID列表
     * @param warehouseId 目标库房ID
     * @param mismatchMessage 归属不一致时的错误信息
     */
    public void validateBelongToWarehouse(List<Long> binIds, Long warehouseId, String mismatchMessage) {
        List<Long> ids = binIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }

        Map<Long, WmsBin> binMap = wmsBinMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(WmsBin::getId, Function.identity()));
        Map<Long, WmsCabinet> cabinetMap = buildCabinetMap(binMap);
        Map<Long, WmsArea> areaMap = buildAreaMap(cabinetMap);

        for (Long binId : ids) {
            WmsBin bin = binMap.get(binId);
            validateBin(binId, bin);
            Long resolvedWarehouseId = resolveWarehouseId(bin, cabinetMap, areaMap);
            if (!Objects.equals(resolvedWarehouseId, warehouseId)) {
                throw new BizException(mismatchMessage + ": binId=" + binId + ", warehouseId=" + warehouseId);
            }
        }
    }

    private Map<Long, WmsCabinet> buildCabinetMap(Map<Long, WmsBin> binMap) {
        Set<Long> cabinetIds = binMap.values().stream()
                .map(WmsBin::getCabinetId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (cabinetIds.isEmpty()) {
            return Map.of();
        }
        return wmsCabinetMapper.selectBatchIds(cabinetIds).stream()
                .collect(Collectors.toMap(WmsCabinet::getId, Function.identity()));
    }

    private Map<Long, WmsArea> buildAreaMap(Map<Long, WmsCabinet> cabinetMap) {
        Set<Long> areaIds = cabinetMap.values().stream()
                .map(WmsCabinet::getAreaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (areaIds.isEmpty()) {
            return Map.of();
        }
        return wmsAreaMapper.selectBatchIds(areaIds).stream()
                .collect(Collectors.toMap(WmsArea::getId, Function.identity()));
    }

    private void validateBin(Long binId, WmsBin bin) {
        if (bin == null) {
            throw new BizException("库位不存在: binId=" + binId);
        }
        if (bin.getDelFlag() == DelFlagConstants.DELETED) {
            throw new BizException("库位已删除: binId=" + binId);
        }
        if (bin.getBinStatus() != null && bin.getBinStatus() == WarehouseConstants.BIN_STATUS_DISABLED) {
            throw new BizException("库位已禁用: binId=" + binId);
        }
    }

    private Long resolveWarehouseId(WmsBin bin, Map<Long, WmsCabinet> cabinetMap, Map<Long, WmsArea> areaMap) {
        if (bin.getWarehouseId() != null) {
            return bin.getWarehouseId();
        }
        WmsCabinet cabinet = bin.getCabinetId() == null ? null : cabinetMap.get(bin.getCabinetId());
        if (cabinet != null && cabinet.getWarehouseId() != null) {
            return cabinet.getWarehouseId();
        }
        WmsArea area = cabinet == null || cabinet.getAreaId() == null ? null : areaMap.get(cabinet.getAreaId());
        return area == null ? null : area.getWarehouseId();
    }
}
