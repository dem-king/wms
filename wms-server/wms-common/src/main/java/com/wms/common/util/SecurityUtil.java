package com.wms.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // JWT 认证链路中 principal 保存的是 userId，username 放在 details 里。
            if (auth.getDetails() instanceof String details && !details.isBlank()) {
                return details;
            }
            return auth.getName();
        }
        return "anonymous";
    }
}
