package com.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.example.utils.ChatMemoryRedisUtil;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Api(tags = "AI聊天")
@Slf4j
@RestController
@RequestMapping("/book/ai")
public class ChatController {

    @Value("${langchain4j.community.dashscope.chat-model.api-key}")
    private String aliQwenApi;

    @Autowired
    private ChatMemoryRedisUtil chatMemoryRedisUtil;

    @ApiOperation("AI聊天（普通模式）")
    @GetMapping("/chat")
    public SaResult chat(@ApiParam("用户消息") @RequestParam(defaultValue = "你是谁") String message) {
        String conversationId = getConversationId();
        
        chatMemoryRedisUtil.addUserMessage(conversationId, message);
        
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(chatMemoryRedisUtil.getChatMessages(conversationId))
                .build();
        
        QwenChatModel chatModel = QwenChatModel.builder().apiKey(aliQwenApi).temperature(0.7f).build();
        ChatResponse response = chatModel.chat(chatRequest);
        String aiResponse = response.aiMessage().text();
        
        chatMemoryRedisUtil.addAiMessage(conversationId, aiResponse);
        
        return SaResult.ok().setData(aiResponse);
    }

    @ApiOperation("AI聊天（流式输出）")
    @GetMapping(value = "/stream", produces = {"text/event-stream;charset=UTF-8", "text/plain;charset=UTF-8"})
    public Flux<String> stream(@ApiParam("用户消息") @RequestParam(defaultValue = "你是谁") String message) {
        StpUtil.checkLogin();
        String conversationId = getConversationId();
        
        log.info("收到流式聊天请求，会话ID: {}, 消息: {}", conversationId, message);
        
        chatMemoryRedisUtil.addUserMessage(conversationId, message);
        
        return Flux.create(fluxSink -> {
            QwenStreamingChatModel qwenStreamingChatModel = QwenStreamingChatModel.builder().apiKey(aliQwenApi).temperature(0.7f).build();
            
            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(chatMemoryRedisUtil.getChatMessages(conversationId))
                    .build();
            
            StringBuilder fullResponse = new StringBuilder();
            
            try {
                qwenStreamingChatModel.chat(chatRequest, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String partialResponse) {
                        if (!fluxSink.isCancelled()) {
                            fullResponse.append(partialResponse);
                            fluxSink.next(partialResponse);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse chatResponse) {
                        if (!fluxSink.isCancelled()) {
                            chatMemoryRedisUtil.addAiMessage(conversationId, fullResponse.toString());
                            log.info("流式聊天完成，会话ID: {}", conversationId);
                            fluxSink.complete();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (!fluxSink.isCancelled()) {
                            log.error("流式聊天出错，会话ID: {}, 错误信息: {}", conversationId, throwable.getMessage(), throwable);
                            fluxSink.error(throwable);
                        }
                    }
                });
            } catch (Exception e) {
                log.error("调用流式聊天模型异常，会话ID: {}, 错误信息: {}", conversationId, e.getMessage());
                fluxSink.error(e);
            }
            
            fluxSink.onCancel(() -> {
                log.warn("客户端断开连接，会话ID: {}", conversationId);
            });
        }, FluxSink.OverflowStrategy.BUFFER);
    }

    @ApiOperation("清除聊天记忆")
    @GetMapping("/clearMemory")
    public SaResult clearMemory() {
        String conversationId = getConversationId();
        chatMemoryRedisUtil.clearMemory(conversationId);
        return SaResult.ok("聊天记忆已清除");
    }

    @ApiOperation("获取聊天记忆数量")
    @GetMapping("/memoryCount")
    public SaResult getMemoryCount() {
        String conversationId = getConversationId();
        long count = chatMemoryRedisUtil.getMessageCount(conversationId);
        return SaResult.ok().setData(count);
    }

    private String getConversationId() {
        if (StpUtil.isLogin()) {
            return StpUtil.getLoginIdAsString();
        }
        return "anonymous";
    }
}