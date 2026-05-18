package com.wms.auth.service;

import com.wms.auth.domain.dto.PasswordReq;

public interface PasswordService {

    void changePassword(Long userId, PasswordReq req);

    boolean validatePasswordStrength(String password);
}
