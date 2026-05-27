package com.wms.item.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.item.domain.dto.TagDto;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.domain.vo.TagVo;
import com.wms.item.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 * 提供标签CRUD和按标签筛选物品接口
 */
@Tag(name = "标签管理")
@RestController
@RequestMapping("/item/tags")
@RequiredArgsConstructor
@Validated
public class TagController {

    private final TagService tagService;

    /**
     * 标签分页列表
     */
    @Operation(summary = "标签列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<TagVo>> page(PageParam pageParam) {
        return R.ok(tagService.page(pageParam));
    }

    /**
     * 标签列表
     */
    @Operation(summary = "标签列表")
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<TagVo>> listAll() {
        return R.ok(tagService.listAll());
    }

    /**
     * 新增标签
     */
    @Operation(summary = "新增标签")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "新增", desc = "新增标签")
    public R<TagVo> create(@Valid @RequestBody TagDto dto) {
        return R.ok(tagService.create(dto));
    }

    /**
     * 更新标签
     */
    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "更新", desc = "更新标签")
    public R<TagVo> update(@PathVariable Long id, @Valid @RequestBody TagDto dto) {
        return R.ok(tagService.update(id, dto));
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "删除", desc = "删除标签")
    public R<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return R.ok();
    }

    /**
     * 按标签筛选物品
     */
    @Operation(summary = "按标签筛选物品")
    @GetMapping("/{tagId}/items")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<ItemVo>> getItemsByTag(@PathVariable Long tagId) {
        return R.ok(tagService.getItemsByTag(tagId));
    }
}
