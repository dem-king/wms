package com.wms.item.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.item.domain.dto.MachineSpareDto;
import com.wms.item.domain.vo.MachineSpareVo;
import com.wms.item.service.MachineSpareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机器-备件关联管理控制器
 * 提供机器-备件关联的CRUD及按机器编号查询接口
 */
@Tag(name = "机器-备件关联管理")
@RestController
@RequestMapping("/machine-spare")
@RequiredArgsConstructor
@Validated
public class MachineSpareController {

    private final MachineSpareService machineSpareService;

    /**
     * 分页查询机器-备件关联
     */
    @Operation(summary = "分页查询机器-备件关联")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<MachineSpareVo>> pageList(PageParam pageParam,
                                                  @RequestParam(required = false) String machineName,
                                                  @RequestParam(required = false) Long spareItemId) {
        return R.ok(machineSpareService.pageList(pageParam, machineName, spareItemId));
    }

    /**
     * 根据ID获取机器-备件关联详情
     */
    @Operation(summary = "获取机器-备件关联详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<MachineSpareVo> getById(@PathVariable Long id) {
        return R.ok(machineSpareService.getById(id));
    }

    /**
     * 按机器编号查询备件列表
     */
    @Operation(summary = "按机器编号查询备件列表")
    @GetMapping("/machine/{machineCode}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<MachineSpareVo>> listByMachineCode(@PathVariable String machineCode) {
        return R.ok(machineSpareService.listByMachineCode(machineCode));
    }

    /**
     * 新增机器-备件关联
     */
    @Operation(summary = "新增机器-备件关联")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "新增", desc = "新增机器-备件关联")
    public R<MachineSpareVo> create(@Valid @RequestBody MachineSpareDto dto) {
        return R.ok(machineSpareService.create(dto));
    }

    /**
     * 更新机器-备件关联
     */
    @Operation(summary = "更新机器-备件关联")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "更新", desc = "更新机器-备件关联")
    public R<MachineSpareVo> update(@PathVariable Long id, @Valid @RequestBody MachineSpareDto dto) {
        return R.ok(machineSpareService.update(id, dto));
    }

    /**
     * 删除机器-备件关联
     */
    @Operation(summary = "删除机器-备件关联")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "删除", desc = "删除机器-备件关联")
    public R<Void> delete(@PathVariable Long id) {
        machineSpareService.delete(id);
        return R.ok();
    }
}
