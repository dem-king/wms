package com.wms.system.controller;

import com.wms.common.annotation.DataScope;
import com.wms.common.annotation.OperLog;
import com.wms.common.domain.PageParam;
import com.wms.common.domain.PageResult;
import com.wms.common.domain.R;
import com.wms.system.domain.dto.SysMessageQueryDto;
import com.wms.system.domain.vo.SysMessageVo;
import com.wms.system.service.SysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站内信控制器。
 */
@Tag(name = "站内信")
@RestController
@RequestMapping("/system/messages")
@RequiredArgsConstructor
@Validated
public class SysMessageController {

    private final SysMessageService sysMessageService;

    /** 当前用户站内信分页 */
    @Operation(summary = "当前用户站内信分页")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<PageResult<SysMessageVo>> page(PageParam pageParam, SysMessageQueryDto query) {
        return R.ok(sysMessageService.pageMessages(pageParam, query));
    }

    /** 当前用户未读站内信数量 */
    @Operation(summary = "当前用户未读站内信数量")
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @DataScope
    public R<Long> unreadCount() {
        return R.ok(sysMessageService.countUnread());
    }

    /** 标记单条站内信为已读 */
    @Operation(summary = "标记单条站内信为已读")
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "system", type = "更新", desc = "标记站内信已读")
    public R<Void> markRead(@PathVariable Long id) {
        sysMessageService.markRead(id);
        return R.ok();
    }

    /** 标记当前用户全部站内信为已读 */
    @Operation(summary = "标记当前用户全部站内信为已读")
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @OperLog(module = "system", type = "更新", desc = "标记全部站内信已读")
    public R<Void> markAllRead() {
        sysMessageService.markAllRead();
        return R.ok();
    }
}
