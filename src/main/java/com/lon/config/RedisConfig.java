package com.lon.config;

/**
 * @projectName: lon_v3
 * @package: com.lon.lon_v3.config
 * @className: RedisConfig
 * @author: LONZT
 * @description: TODO
 * @date: 2023/5/26 10:44
 * @version: 1.0
 */


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lon.comomon.RedisConstant;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 重写缓存Key生成策略。
     * 包名+方法名+参数列表。防止缓存Key冲突
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            // 存放最终结果
            StringBuilder resultStringBuilder = new StringBuilder("cache:key:");
            // 执行方法所在的类
            resultStringBuilder.append(target.getClass().getName()).append(".");
            // 执行的方法名称
            resultStringBuilder.append(method.getName()).append("(");

            // 存放参数
            StringBuilder paramStringBuilder = new StringBuilder();
            for (Object param : params) {
                if (param == null) {
                    paramStringBuilder.append("java.lang.Object[null],");
                } else {
                    paramStringBuilder
                            .append(param.getClass().getName())
                            .append("[")
                            .append(String.valueOf(param))
                            .append("],");
                }
            }
            if (StringUtils.hasText(paramStringBuilder.toString())) {
                // 去掉最后的逗号
                String trimLastComma = paramStringBuilder.substring(0, paramStringBuilder.length() - 1);
                resultStringBuilder.append(trimLastComma);
            }

            return resultStringBuilder.append(")").toString();
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(RedisConstant.CacheNames.CACHE_30_SECOND, createCacheConfig(Duration.ofSeconds(30)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_1_MINUTE, createCacheConfig(Duration.ofMinutes(1)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_5_MINUTE, createCacheConfig(Duration.ofMinutes(5)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_10_MINUTE, createCacheConfig(Duration.ofMinutes(10)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_30_MINUTE, createCacheConfig(Duration.ofMinutes(30)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_1_HOUR, createCacheConfig(Duration.ofHours(1)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_2_HOUR, createCacheConfig(Duration.ofHours(2)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_6_HOUR, createCacheConfig(Duration.ofHours(6)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_12_HOUR, createCacheConfig(Duration.ofHours(12)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_1_DAY, createCacheConfig(Duration.ofDays(1)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_15_DAY, createCacheConfig(Duration.ofDays(15)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_30_DAY, createCacheConfig(Duration.ofDays(30)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_60_DAY, createCacheConfig(Duration.ofDays(60)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_180_DAY, createCacheConfig(Duration.ofDays(180)));
        configurationMap.put(RedisConstant.CacheNames.CACHE_365_DAY, createCacheConfig(Duration.ofDays(365)));

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(7))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .initialCacheNames(configurationMap.keySet())
                .withInitialCacheConfigurations(configurationMap)
                // 如果key不在configurationMap中，则使用此配置
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(keySerializer());
        template.setValueSerializer(valueSerializer());

        template.setHashKeySerializer(keySerializer());
        template.setHashValueSerializer(valueSerializer());

        template.afterPropertiesSet();
        return template;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 此项必须配置，否则如果序列化的对象里边还有对象，会报如下错误：
        //     java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        // 旧版写法：
        // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

        return jdkSerializationRedisSerializer;
    }

    private RedisCacheConfiguration createCacheConfig(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
    }
}
