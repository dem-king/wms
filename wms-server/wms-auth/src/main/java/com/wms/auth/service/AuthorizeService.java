package com.wms.auth.service;

import java.util.List;

public interface AuthorizeService {

    boolean hasPermission(Long userId, String permCode);

    List<String> getUserPermissions(Long userId);

    void refreshPermissionCache(Long userId);
}
