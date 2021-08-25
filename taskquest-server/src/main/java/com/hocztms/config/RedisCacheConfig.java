package com.hocztms.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;


@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //设置CacheResolver配置后相关的 失效时间
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));

        HashMap<String,RedisCacheConfiguration> ttlConfigurations = new HashMap<>();
        ttlConfigurations.put("class",config.entryTtl(Duration.ofMinutes(10)));
        ttlConfigurations.put("college",config.entryTtl(Duration.ofMinutes(10)));
        ttlConfigurations.put("user",config.entryTtl(Duration.ofMinutes(45)));
        ttlConfigurations.put("userRole",config.entryTtl(Duration.ofMinutes(45)));
        ttlConfigurations.put("admin",config.entryTtl(Duration.ofMinutes(45)));
        ttlConfigurations.put("adminRole",config.entryTtl(Duration.ofMinutes(45)));
        ttlConfigurations.put("userDto",config.entryTtl(Duration.ofMinutes(20)));

        ttlConfigurations.put("message",config.entryTtl(Duration.ofMinutes(3)));
        ttlConfigurations.put("userMessage",config.entryTtl(Duration.ofMinutes(3)));

        ttlConfigurations.put("taskRecords",config.entryTtl(Duration.ofMinutes(8)));
        ttlConfigurations.put("taskMembers",config.entryTtl(Duration.ofMinutes(30)));
        ttlConfigurations.put("taskStudentRecords",config.entryTtl(Duration.ofMinutes(15)));



        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(ttlConfigurations)
                .build();
        return cacheManager;
    }



}
