package com.wms.auth.service;

import com.wms.auth.domain.entity.AuthLoginLog;
import com.wms.auth.domain.entity.AuthOperLog;

public interface AuthAuditService {

    void recordLoginLog(AuthLoginLog log);

    void recordOperLog(AuthOperLog log);
}
