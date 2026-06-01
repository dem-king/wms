package com.wms.item.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.R;
import com.wms.item.domain.dto.LabelBindDto;
import com.wms.item.domain.dto.LabelGenerateDto;
import com.wms.item.domain.dto.LabelStatusDto;
import com.wms.item.domain.vo.ElectronicLabelVo;
import com.wms.item.service.ElectronicLabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电子标签管理控制器
 * 提供标签生成、绑定、状态管理、扫码查询、闲置检测等接口
 */
@Tag(name = "电子标签管理")
@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@Validated
public class LabelController {

    private final ElectronicLabelService electronicLabelService;

    /**
     * 标签分页列表
     * 支持按标签类型、标签状态、物品ID筛选
     */
    @Operation(summary = "标签分页列表")
    @GetMapping
    @PreAuthorize("hasAuthority('item:label:list')")
    @DataScope
    public R<Page<ElectronicLabelVo>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "20") Long size,
            @RequestParam(required = false) Integer labelType,
            @RequestParam(required = false) Integer labelStatus,
            @RequestParam(required = false) Long itemId) {
        Page<ElectronicLabelVo> page = new Page<>(current, size);
        return R.ok(electronicLabelService.page(page, itemId, labelType, labelStatus));
    }

    /**
     * 标签详情(含物品信息)
     */
    @Operation(summary = "标签详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('item:label:list')")
    @DataScope
    public R<ElectronicLabelVo> getById(@PathVariable Long id) {
        return R.ok(electronicLabelService.getById(id));
    }

    /**
     * 批量生成标签
     */
    @Operation(summary = "批量生成标签")
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('item:label:generate')")
    @OperLog(module = "item", type = "新增", desc = "批量生成电子标签")
    public R<List<ElectronicLabelVo>> generate(@Valid @RequestBody LabelGenerateDto dto) {
        return R.ok(electronicLabelService.generate(dto));
    }

    /**
     * 标签绑定物品
     */
    @Operation(summary = "标签绑定物品")
    @PutMapping("/{id}/bind")
    @PreAuthorize("hasAuthority('item:label:bind')")
    @OperLog(module = "item", type = "更新", desc = "标签绑定物品")
    public R<ElectronicLabelVo> bind(@PathVariable Long id, @Valid @RequestBody LabelBindDto dto) {
        return R.ok(electronicLabelService.bind(id, dto));
    }

    /**
     * 更新标签状态
     */
    @Operation(summary = "更新标签状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('item:label:status')")
    @OperLog(module = "item", type = "更新", desc = "更新标签状态")
    public R<ElectronicLabelVo> updateStatus(@PathVariable Long id, @Valid @RequestBody LabelStatusDto dto) {
        return R.ok(electronicLabelService.updateStatus(id, dto));
    }

    /**
     * 批量打印标签(更新打印状态为已打印)
     */
    @Operation(summary = "批量打印标签")
    @PostMapping("/print")
    @PreAuthorize("hasAuthority('item:label:print')")
    @OperLog(module = "item", type = "更新", desc = "批量打印标签")
    public R<Void> batchPrint(@RequestBody List<Long> labelIds) {
        electronicLabelService.batchPrint(labelIds);
        return R.ok();
    }

    /**
     * 扫码查询标签信息
     */
    @Operation(summary = "扫码查询标签信息")
    @GetMapping("/scan/{code}")
    @PreAuthorize("hasAuthority('item:label:list')")
    @DataScope
    public R<ElectronicLabelVo> scan(@PathVariable String code) {
        return R.ok(electronicLabelService.scan(code));
    }

    /**
     * 查询长期闲置标签
     */
    @Operation(summary = "查询长期闲置标签")
    @GetMapping("/idle")
    @PreAuthorize("hasAuthority('item:label:list')")
    @DataScope
    public R<List<ElectronicLabelVo>> listIdle() {
        return R.ok(electronicLabelService.listIdle());
    }
}
