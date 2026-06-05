package com.example.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    // ---------- Sa-Token Redis (db=1) ----------
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisProperties saTokenRedisProperties() {
        return new RedisProperties();
    }

    @Bean
    @Primary
    public RedisConnectionFactory saTokenRedisConnectionFactory(
            @Qualifier("saTokenRedisProperties") RedisProperties properties) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(properties.getHost(), properties.getPort());
        factory.setDatabase(properties.getDatabase());
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean("saTokenRedisTemplate")
    public RedisTemplate<String, Object> saTokenRedisTemplate(
            @Qualifier("saTokenRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(
            @Qualifier("saTokenRedisConnectionFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    // ---------- Spring Cache Redis (db=2) ----------
    @Bean
    @ConfigurationProperties(prefix = "springcache.redis")
    public RedisProperties springCacheRedisProperties() {
        return new RedisProperties();
    }

    @Bean
    public RedisConnectionFactory springCacheRedisConnectionFactory(
            @Qualifier("springCacheRedisProperties") RedisProperties properties) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(properties.getHost(), properties.getPort());
        factory.setDatabase(properties.getDatabase());
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean("springCacheRedisTemplate")
    public RedisTemplate<String, Object> springCacheRedisTemplate(
            @Qualifier("springCacheRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
        return template;
    }

    // ---------- Spring Cache Manager ----------
    @Bean
    @Primary
    public RedisCacheManager cacheManager(
            @Qualifier("springCacheRedisConnectionFactory") RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericFastJsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(5));
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    // ---------- KeyGenerator ----------
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName()).append(":");
            sb.append(method.getName()).append(":");
            for (Object param : params) {
                sb.append(param.toString()).append(":");
            }
            return sb.toString();
        };
    }
}