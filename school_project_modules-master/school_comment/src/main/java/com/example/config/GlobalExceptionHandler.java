package com.example.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public SaResult handlerNotLoginException(NotLoginException e) {
        String message;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN: message = "未提供Token"; break;
            case NotLoginException.INVALID_TOKEN: message = "Token无效"; break;
            case NotLoginException.TOKEN_TIMEOUT: message = "Token已过期"; break;
            case NotLoginException.BE_REPLACED: message = "账号已被顶下线"; break;
            case NotLoginException.KICK_OUT: message = "账号已被踢下线"; break;
            default: message = "当前会话未登录"; break;
        }
        return SaResult.error(message).setCode(401);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SaResult handleValidException(MethodArgumentNotValidException e) {
        String errorMsg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return SaResult.error(errorMsg);
    }

    @ExceptionHandler(value = Exception.class)
    public SaResult handleException(Exception e) {
        return SaResult.error("服务器内部错误" + e.getMessage()).setCode(500);
    }
}
