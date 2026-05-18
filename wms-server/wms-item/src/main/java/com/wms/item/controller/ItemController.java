package com.wms.item.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.item.domain.dto.ItemDto;
import com.wms.item.domain.vo.ItemImageVo;
import com.wms.item.domain.vo.ItemVo;
import com.wms.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 物品管理控制器
 * 提供物品CRUD、分页查询、图片管理、快速搜索等接口
 */
@Tag(name = "物品管理")
@RestController
@RequestMapping("/item/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    /**
     * 物品分页列表
     */
    @Operation(summary = "物品分页列表")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<ItemVo>> page(PageParam pageParam,
                                      @RequestParam(required = false) Long categoryId,
                                      @RequestParam(required = false) Long subCategoryId,
                                      @RequestParam(required = false) Long tagId,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) String keyword) {
        return R.ok(itemService.page(pageParam, categoryId, subCategoryId, tagId, status, keyword));
    }

    /**
     * 物品详情
     */
    @Operation(summary = "物品详情")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<ItemVo> getById(@PathVariable Long id) {
        return R.ok(itemService.getById(id));
    }

    /**
     * 新增物品
     */
    @Operation(summary = "新增物品")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "新增", desc = "新增物品")
    public R<ItemVo> create(@Valid @RequestBody ItemDto dto) {
        return R.ok(itemService.create(dto));
    }

    /**
     * 更新物品
     */
    @Operation(summary = "更新物品")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "更新", desc = "更新物品")
    public R<ItemVo> update(@PathVariable Long id, @Valid @RequestBody ItemDto dto) {
        return R.ok(itemService.update(id, dto));
    }

    /**
     * 删除物品
     */
    @Operation(summary = "删除物品")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "删除", desc = "删除物品")
    public R<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return R.ok();
    }

    /**
     * 上传物品图片
     */
    @Operation(summary = "上传物品图片")
    @PostMapping("/{itemId}/images")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "新增", desc = "上传物品图片")
    public R<ItemImageVo> uploadImage(@PathVariable Long itemId,
                                      @RequestParam("file") MultipartFile file) {
        return R.ok(itemService.uploadImage(itemId, file));
    }

    /**
     * 删除物品图片
     */
    @Operation(summary = "删除物品图片")
    @DeleteMapping("/{itemId}/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "item", type = "删除", desc = "删除物品图片")
    public R<Void> deleteImage(@PathVariable Long itemId, @PathVariable Long imageId) {
        itemService.deleteImage(itemId, imageId);
        return R.ok();
    }

    /**
     * 快速搜索物品
     */
    @Operation(summary = "快速搜索物品")
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<List<ItemVo>> search(@RequestParam String keyword) {
        return R.ok(itemService.search(keyword));
    }
}
