package com.wms.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 编号序列生成器
 * 基于Redis INCR原子操作生成每日递增流水号，保证并发安全
 *
 * <p>使用方式：
 * <pre>
 *   String orderNo = sequenceGenerator.next("RK");
 *   // 产出: RK202605180001
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class SequenceGenerator {

    private static final String SEQ_KEY_PREFIX = "wms:seq:";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final int SEQ_LENGTH = 4;
    private static final int TTL_DAYS = 2;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成下一个带日期的序列号
     *
     * @param prefix 编号前缀，如 RK、CK、KF 等
     * @return 完整编号，格式为 前缀+日期+4位流水号，如 RK202605180001
     */
    public String next(String prefix) {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        String redisKey = SEQ_KEY_PREFIX + prefix + ":" + datePart;
        Long seq = stringRedisTemplate.opsForValue().increment(redisKey);
        if (seq != null && seq == 1L) {
            stringRedisTemplate.expire(redisKey, TTL_DAYS, TimeUnit.DAYS);
        }
        return prefix + datePart + String.format("%0" + SEQ_LENGTH + "d", seq);
    }
}