package com.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class ChatAiRedisConfig {

    @Bean
    @ConfigurationProperties(prefix = "chatai.redis")
    public RedisProperties chatAiRedisProperties() {
        return new RedisProperties();
    }

    @Bean
    public RedisConnectionFactory chatAiRedisConnectionFactory(
            @Qualifier("chatAiRedisProperties") RedisProperties chatAiRedisProperties) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
                chatAiRedisProperties.getHost(), chatAiRedisProperties.getPort());
        factory.setDatabase(chatAiRedisProperties.getDatabase());
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public StringRedisTemplate chatAiStringRedisTemplate(
            @Qualifier("chatAiRedisConnectionFactory") RedisConnectionFactory chatAiRedisConnectionFactory) {
        return new StringRedisTemplate(chatAiRedisConnectionFactory);
    }
}