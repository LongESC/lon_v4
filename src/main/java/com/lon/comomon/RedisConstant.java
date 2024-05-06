package com.lon.comomon;

/**
 * @projectName: lon_v3
 * @package: com.lon.lon_v3.comomon
 * @className: RedisConstant
 * @author: LONZT
 * @description: TODO
 * @date: 2023/5/26 10:45
 * @version: 1.0
 */


public class RedisConstant {
    /**
     * @Cacheable的cacheNames属性使用的值
     */
    public interface CacheNames {
        String CACHE_30_SECOND = "redis_cache_30_second";
        String CACHE_1_MINUTE = "redis_cache_1_minute";
        String CACHE_5_MINUTE = "redis_cache_5_minute";
        String CACHE_10_MINUTE = "redis_cache_10_minute";
        String CACHE_30_MINUTE = "redis_cache_30_minute";
        String CACHE_1_HOUR = "redis_cache_1_hour";
        String CACHE_2_HOUR = "redis_cache_2_hour";
        String CACHE_6_HOUR = "redis_cache_6_hour";
        String CACHE_12_HOUR = "redis_cache_12_hour";
        String CACHE_1_DAY = "redis_cache_1_day";
        String CACHE_15_DAY = "redis_cache_15_day";
        String CACHE_30_DAY = "redis_cache_30_day";
        String CACHE_60_DAY = "redis_cache_60_day";
        String CACHE_180_DAY = "redis_cache_180_day";
        String CACHE_365_DAY = "redis_cache_365_day";
    }
}
