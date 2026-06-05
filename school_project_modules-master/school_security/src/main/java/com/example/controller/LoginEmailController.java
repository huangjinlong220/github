package com.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.LoginEmail;
import com.example.service.EmailAsyncService;
import com.example.utils.EmailCodeRedisUtil;
import com.example.validation.Valida;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Api(tags = "邮箱登录")
@RestController
@RequestMapping("/login")
public class LoginEmailController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailCodeRedisUtil emailCodeRedisUtil;
    @Autowired
    private EmailAsyncService emailAsyncService;

    @ApiOperation("发送邮箱验证码")
    @GetMapping("/sendEmail")
    public SaResult sendEmail(@ApiParam("电话号码") @RequestParam String phone) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userMapper.selectOne(wrapper);
            if (user == null) {
                return SaResult.error("没有该用户");
            }
            if (user.getEmail() == null || "".equals(user.getEmail())) {
                return SaResult.error("该用户未绑定邮箱");
            }
            String email = user.getEmail();
            if (emailCodeRedisUtil.hasValidCode(email)) {
                Long expireSeconds = emailCodeRedisUtil.getExpireTime(email);
                if (expireSeconds > 0) {
                    return SaResult.ok("验证码已发送，请查收邮件。剩余有效期：" + (expireSeconds / 60) + "分钟");
                }
            }
            if (emailCodeRedisUtil.isSendingPlaceholder(email)) {
                return SaResult.ok("邮件正在发送中，请稍后查看邮箱。若长时间未收到，请2分钟后重试");
            }
            String code = generateCode();
            emailCodeRedisUtil.createSendingPlaceholder(email);
            CompletableFuture<Boolean> future = emailAsyncService.sendVerificationCodeAsync(email, "我来咯我来咯");
            future.whenComplete((result, throwable) -> {
                if (throwable != null || !result) {
                    log.error("邮件发送失败，删除占位符：{}", email);
                    emailCodeRedisUtil.deleteEmailCode(email);
                }
            });
            return SaResult.ok("验证码发送请求已接收，请30秒后查收邮件");
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("发送失败，请稍后重试");
        }
    }

    @ApiOperation("邮箱登录")
    @PostMapping("/email")
    public SaResult loginEmail(@Validated(Valida.Create.class) @RequestBody LoginEmail loginEmail) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginEmail.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return SaResult.error("没有该账号");
        }
        String emailCode = emailCodeRedisUtil.getEmailCode(user.getEmail());
        if (!StringUtils.hasText(emailCode)) {
            return SaResult.error("该用户未发送验证码");
        }
        if (loginEmail.getCode().equals(emailCode)) {
            StpUtil.login(loginEmail.getPhone());
            emailCodeRedisUtil.deleteEmailCode(user.getEmail());
            return SaResult.ok("登录成功").setData(StpUtil.getTokenInfo());
        } else {
            return SaResult.error("验证码不正确");
        }
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
