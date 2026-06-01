package com.wms.auth.listener;

import com.wms.auth.service.AuthorizeService;
import com.wms.common.event.PermissionCacheEvictEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.verify;

/**
 * 权限缓存失效事件监听器测试
 */
@DisplayName("PermissionCacheEvictListener 测试")
@ExtendWith(MockitoExtension.class)
class PermissionCacheEvictListenerTest {

    @Mock
    private AuthorizeService authorizeService;

    @Test
    @DisplayName("收到权限缓存失效事件时应逐个清理用户权限缓存")
    void shouldClearPermissionCacheForEventUsers() {
        PermissionCacheEvictListener listener = new PermissionCacheEvictListener(authorizeService);

        listener.onPermissionCacheEvict(new PermissionCacheEvictEvent(Set.of(1001L, 1002L)));

        verify(authorizeService).refreshPermissionCache(1001L);
        verify(authorizeService).refreshPermissionCache(1002L);
    }
}
