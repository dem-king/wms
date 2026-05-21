package com.wms.warehouse.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.warehouse.domain.dto.CabinetDto;
import com.wms.warehouse.domain.dto.CabinetLayoutBatchSaveDto;
import com.wms.warehouse.domain.vo.CabinetLayoutSaveVo;
import com.wms.warehouse.domain.vo.CabinetVo;
import com.wms.warehouse.service.CabinetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 存放柜管理控制器
 * 提供存放柜CRUD、按区域查询、详情查询、位置更新等接口
 */
@Tag(name = "存放柜管理")
@RestController
@RequestMapping("/warehouse/cabinets")
@RequiredArgsConstructor
@Validated
public class CabinetController {

    private final CabinetService cabinetService;

    /**
     * 按区域ID查询存放柜列表
     */
    @Operation(summary = "按区域查询存放柜列表")
    @GetMapping("/area/{areaId}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<CabinetVo>> listByAreaId(@PathVariable Long areaId) {
        return R.ok(cabinetService.listByAreaId(areaId));
    }

    /**
     * 存放柜详情
     */
    @Operation(summary = "存放柜详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<CabinetVo> getById(@PathVariable Long id) {
        return R.ok(cabinetService.getById(id));
    }

    /**
     * 新增存放柜
     */
    @Operation(summary = "新增存放柜")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "新增", desc = "新增存放柜")
    public R<CabinetVo> create(@Valid @RequestBody CabinetDto dto) {
        return R.ok(cabinetService.create(dto));
    }

    /**
     * 更新存放柜
     */
    @Operation(summary = "更新存放柜")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "更新", desc = "更新存放柜")
    public R<CabinetVo> update(@PathVariable Long id, @Valid @RequestBody CabinetDto dto) {
        return R.ok(cabinetService.update(id, dto));
    }

    /**
     * 删除存放柜
     */
    @Operation(summary = "删除存放柜")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "删除", desc = "删除存放柜")
    public R<Void> delete(@PathVariable Long id) {
        cabinetService.delete(id);
        return R.ok();
    }

    /**
     * 更新存放柜位置坐标
     */
    @Operation(summary = "更新存放柜位置坐标")
    @PutMapping("/{id}/position")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "更新", desc = "更新存放柜位置坐标")
    public R<Void> updatePosition(@PathVariable Long id,
                                  @RequestParam Integer x,
                                  @RequestParam Integer y) {
        cabinetService.updatePosition(id, x, y);
        return R.ok();
    }

    /**
     * 批量保存存放柜布局
     */
    @Operation(summary = "批量保存存放柜布局")
    @PostMapping("/layout")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "warehouse", type = "更新", desc = "批量保存存放柜布局")
    public R<CabinetLayoutSaveVo> saveLayout(@Valid @RequestBody CabinetLayoutBatchSaveDto dto) {
        return R.ok(cabinetService.saveLayout(dto));
    }
}
