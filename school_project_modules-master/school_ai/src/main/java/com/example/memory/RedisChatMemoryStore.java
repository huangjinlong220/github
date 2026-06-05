package com.example.memory;

import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

@Slf4j
@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemoryStore(
            @Qualifier("chatAiStringRedisTemplate") StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String MEMORY_PREFIX = "ai:memory:";
    private static final int DEFAULT_MAX_MESSAGES = 20;
    private static final long DEFAULT_EXPIRE_DAYS = 7;

    @Override
    public List<ChatMessage> getMessages(Object conversationId) {
        String key = getKey(conversationId);
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
        
        if (jsonList == null || jsonList.isEmpty()) {
            return List.of();
        }
        
        return jsonList.stream()
                .map(this::deserializeMessage)
                .toList();
    }

    @Override
    public void updateMessages(Object conversationId, List<ChatMessage> messages) {
        String key = getKey(conversationId);

        for (ChatMessage message : messages) {
            try {
                String json = objectMapper.writeValueAsString(message);
                redisTemplate.opsForList().rightPush(key, json);
            } catch (JsonProcessingException e) {
                log.error("序列化消息失败: {}", e.getMessage());
            }
        }

        trimMessages(key);
    }

    @Override
    public void deleteMessages(Object conversationId) {
        String key = getKey(conversationId);
        redisTemplate.delete(key);
    }

    private String getKey(Object conversationId) {
        return MEMORY_PREFIX + conversationId;
    }

    private void trimMessages(String key) {
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > DEFAULT_MAX_MESSAGES) {
            redisTemplate.opsForList().trim(key, -DEFAULT_MAX_MESSAGES, -1);
        }
    }

    private ChatMessage deserializeMessage(String json) {
        try {
            return objectMapper.readValue(json, ChatMessage.class);
        } catch (JsonProcessingException e) {
            log.error("反序列化消息失败: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}