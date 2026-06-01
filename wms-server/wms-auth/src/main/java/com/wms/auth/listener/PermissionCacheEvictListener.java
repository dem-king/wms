package com.wms.auth.listener;

import com.wms.auth.service.AuthorizeService;
import com.wms.common.event.PermissionCacheEvictEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Permission cache eviction listener.
 * Clears cached permission codes for users affected by role or permission changes.
 */
@Component
@RequiredArgsConstructor
public class PermissionCacheEvictListener {

    private final AuthorizeService authorizeService;

    /**
     * Clears permission cache for every affected user in the event.
     *
     * @param event permission cache eviction event
     */
    @EventListener
    public void onPermissionCacheEvict(PermissionCacheEvictEvent event) {
        if (event == null || event.userIds() == null || event.userIds().isEmpty()) {
            return;
        }
        event.userIds().forEach(authorizeService::refreshPermissionCache);
    }
}
