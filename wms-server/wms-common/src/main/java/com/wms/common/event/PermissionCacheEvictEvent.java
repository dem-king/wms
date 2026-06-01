package com.wms.common.event;

import java.util.Set;

/**
 * Permission cache eviction event.
 * Published when role or permission assignments change and affected users must reload permissions.
 *
 * @param userIds affected user IDs
 */
public record PermissionCacheEvictEvent(Set<Long> userIds) {
}
