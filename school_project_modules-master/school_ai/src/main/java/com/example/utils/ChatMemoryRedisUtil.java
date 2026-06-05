package com.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ChatMemoryRedisUtil {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ChatMemoryRedisUtil(
            @Qualifier("chatAiStringRedisTemplate") StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String CHAT_MEMORY_PREFIX = "chat:memory:";
    private static final int DEFAULT_MAX_MESSAGES = 20;
    private static final long DEFAULT_EXPIRE_HOURS = 24;

    public void addUserMessage(String conversationId, String userMessage) {
        addMessage(conversationId, "user", userMessage);
    }

    public void addAiMessage(String conversationId, String aiMessage) {
        addMessage(conversationId, "ai", aiMessage);
    }

    private void addMessage(String conversationId, String role, String content) {
        try {
            String key = CHAT_MEMORY_PREFIX + conversationId;
            String messageJson = objectMapper.writeValueAsString(new ChatMessageRecord(role, content));
            redisTemplate.opsForList().rightPush(key, messageJson);
            trimMessages(key, DEFAULT_MAX_MESSAGES);
            redisTemplate.expire(key, DEFAULT_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.error("序列化聊天消息失败: {}", e.getMessage());
        }
    }

    public List<ChatMessage> getChatMessages(String conversationId) {
        List<ChatMessage> messages = new ArrayList<>();
        String key = CHAT_MEMORY_PREFIX + conversationId;
        List<String> messageJsonList = redisTemplate.opsForList().range(key, 0, -1);
        
        if (messageJsonList != null) {
            for (String messageJson : messageJsonList) {
                try {
                    ChatMessageRecord record = objectMapper.readValue(messageJson, ChatMessageRecord.class);
                    if ("user".equals(record.getRole())) {
                        messages.add(UserMessage.from(record.getContent()));
                    } else if ("ai".equals(record.getRole())) {
                        messages.add(AiMessage.from(record.getContent()));
                    }
                } catch (JsonProcessingException e) {
                    log.error("反序列化聊天消息失败: {}", e.getMessage());
                }
            }
        }
        return messages;
    }

    public void clearMemory(String conversationId) {
        String key = CHAT_MEMORY_PREFIX + conversationId;
        redisTemplate.delete(key);
    }

    private void trimMessages(String key, int maxMessages) {
        Long length = redisTemplate.opsForList().size(key);
        if (length != null && length > maxMessages) {
            redisTemplate.opsForList().trim(key, 0, maxMessages - 1);
        }
    }

    public long getMessageCount(String conversationId) {
        String key = CHAT_MEMORY_PREFIX + conversationId;
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    public static class ChatMessageRecord {
        private String role;
        private String content;

        public ChatMessageRecord() {
        }

        public ChatMessageRecord(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}