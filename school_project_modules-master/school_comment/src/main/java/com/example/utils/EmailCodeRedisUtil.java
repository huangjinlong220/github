package com.example.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码 Redis 工具类
 * 用于处理邮箱验证码的存储、验证和过期管理
 * 使用 Redis 作为缓存介质，支持验证码占位符、错误次数统计等功能
 */
@Component
public class EmailCodeRedisUtil {
    @Autowired
    @Qualifier("saTokenRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    // Redis key 前缀常量定义
    private static final String EMAIL_CODE_PREFIX = "email:code:";        // 邮箱验证码 key 前缀
    private static final String EMAIL_ATTEMPT_PREFIX = "email:attempt:";  // 邮箱验证尝试次数 key 前缀
    
    // 过期时间常量定义（单位：分钟）
    private static final long DEFAULT_CODE_EXPIRE = 5;   // 验证码默认过期时间：5 分钟
    private static final long DEFAULT_ATTEMPT_EXPIRE = 30; // 尝试次数记录默认过期时间：30 分钟
    
    // 验证码发送中占位符，用于防止重复发送
    private static final String SENDING_PLACEHOLDER = "SENDING";

    /**
     * 创建验证码发送中占位符
     * 在开始发送邮件时调用，防止用户重复点击发送验证码
     * @param email 目标邮箱地址
     */
    public void createSendingPlaceholder(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        // 设置 30 秒的占位符，30 秒内无法再次请求发送验证码
        redisTemplate.opsForValue().set(key, SENDING_PLACEHOLDER, 30, TimeUnit.SECONDS);
    }

    /**
     * 将占位符更新为实际验证码
     * 仅在当前值为占位符时才更新，确保并发安全
     * @param email 目标邮箱地址
     * @param code 要设置的验证码
     */
    public void updatePlaceholderToCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        Object currentValue = redisTemplate.opsForValue().get(key);
        if (SENDING_PLACEHOLDER.equals(currentValue)) {
            // 如果当前是占位符，则更新为验证码，保持 5 分钟有效期
            redisTemplate.opsForValue().set(key, code, DEFAULT_CODE_EXPIRE, TimeUnit.MINUTES);
        } else {
            // 如果不是占位符，直接设置验证码（覆盖）
            redisTemplate.opsForValue().set(key, code, DEFAULT_CODE_EXPIRE, TimeUnit.MINUTES);
        }
    }

    /**
     * 检查是否正在发送验证码（占位符状态）
     * @param email 目标邮箱地址
     * @return true-正在发送中，false-未发送或已完成
     */
    public boolean isSendingPlaceholder(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        Object value = redisTemplate.opsForValue().get(key);
        return SENDING_PLACEHOLDER.equals(value);
    }

    /**
     * 保存邮箱验证码（使用默认过期时间）
     * @param email 目标邮箱地址
     * @param code 验证码字符串
     */
    public void saveEmailCode(String email, String code) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, DEFAULT_CODE_EXPIRE, TimeUnit.MINUTES);
    }

    /**
     * 保存邮箱验证码（自定义过期时间）
     * @param email 目标邮箱地址
     * @param code 验证码字符串
     * @param expireMinutes 过期时间（分钟）
     */
    public void saveEmailCode(String email, String code, long expireMinutes) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, expireMinutes, TimeUnit.MINUTES);
    }

    /**
     * 获取邮箱验证码
     * 如果当前是占位符状态，返回 null（表示验证码还在发送中）
     * @param email 目标邮箱地址
     * @return 验证码字符串，不存在或发送中时返回 null
     */
    public String getEmailCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        Object code = redisTemplate.opsForValue().get(key);
        if (SENDING_PLACEHOLDER.equals(code)) {
            return null;  // 发送中状态，返回 null
        }
        return code != null ? code.toString() : null;
    }

    /**
     * 验证邮箱验证码
     * 验证成功会删除验证码并清空尝试次数；验证失败会增加尝试次数
     * @param email 目标邮箱地址
     * @param inputCode 用户输入的验证码
     * @return true-验证成功，false-验证失败
     */
    public boolean validateEmailCode(String email, String inputCode) {
        String savedCode = getEmailCode(email);
        if (savedCode == null) {
            return false;  // 验证码不存在或已过期
        }
        if (savedCode.equals(inputCode)) {
            // 验证码正确，删除验证码并清空尝试次数
            deleteEmailCode(email);
            clearAttemptCount(email);
            return true;
        } else {
            // 验证码错误，增加尝试次数
            incrementAttemptCount(email);
            return false;
        }
    }

    /**
     * 删除邮箱验证码
     * @param email 目标邮箱地址
     */
    public void deleteEmailCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        redisTemplate.delete(key);
    }

    /**
     * 检查是否存在有效的验证码（排除占位符状态）
     * @param email 目标邮箱地址
     * @return true-存在有效验证码，false-无验证码或仅为占位符
     */
    public boolean hasValidCode(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null && !SENDING_PLACEHOLDER.equals(value);
    }

    /**
     * 获取验证码剩余过期时间
     * @param email 目标邮箱地址
     * @return 剩余秒数；-1 表示正在发送中；-2 表示不存在或已过期
     */
    public Long getExpireTime(String email) {
        String key = EMAIL_CODE_PREFIX + email;
        if (isSendingPlaceholder(email)) {
            return -1L;  // 发送中状态
        }
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 增加验证尝试次数
     * 首次增加时会设置 30 分钟的过期时间
     * @param email 目标邮箱地址
     * @return 当前累计尝试次数
     */
    public Long incrementAttemptCount(String email) {
        String key = EMAIL_ATTEMPT_PREFIX + email;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            // 第一次尝试，设置 30 分钟过期时间
            redisTemplate.expire(key, DEFAULT_ATTEMPT_EXPIRE, TimeUnit.MINUTES);
        }
        return count;
    }

    /**
     * 获取验证尝试次数
     * @param email 目标邮箱地址
     * @return 尝试次数，不存在时返回 0
     */
    public Integer getAttemptCount(String email) {
        String key = EMAIL_ATTEMPT_PREFIX + email;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count.toString()) : 0;
    }

    /**
     * 清空验证尝试次数
     * @param email 目标邮箱地址
     */
    public void clearAttemptCount(String email) {
        String key = EMAIL_ATTEMPT_PREFIX + email;
        redisTemplate.delete(key);
    }

    /**
     * 检查是否超过最大尝试次数限制
     * @param email 目标邮箱地址
     * @param maxAttempts 最大允许尝试次数
     * @return true-已超过限制，false-未超过限制
     */
    public boolean isAttemptLimitExceeded(String email, int maxAttempts) {
        Integer attempts = getAttemptCount(email);
        return attempts >= maxAttempts;
    }

    /**
     * 检查是否超过默认最大尝试次数限制（5 次）
     * @param email 目标邮箱地址
     * @return true-已超过限制，false-未超过限制
     */
    public boolean isAttemptLimitExceeded(String email) {
        return isAttemptLimitExceeded(email, 5);
    }
}
