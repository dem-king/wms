package com.wms.auth.service;

public interface RateLimiterService {

    void tryAcquire(String ip);
}
