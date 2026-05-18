package com.wms.auth.service;

public interface LoginLockService {

    void recordFailure(String username);

    boolean isLocked(String username);

    void clearFailureCount(String username);

    long getRemainingLockTime(String username);
}
