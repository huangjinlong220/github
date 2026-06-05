package com.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public SaResult register(@ApiParam("用户名") @RequestParam String username,
                             @ApiParam("密码") @RequestParam String password,
                             @ApiParam("手机号") @RequestParam String phone,
                             @ApiParam("邮箱") @RequestParam String email) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, username);
        User existingUser = userMapper.selectOne(wrapper);
        
        if (existingUser != null) {
            return SaResult.error("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setName(username);
        user.setPhone(phone);
        user.setPassword(BCrypt.hashpw(password));
        user.setEmail(email);
        user.setStatus("1"); // 默认启用状态
        
        int result = userMapper.insert(user);
        if (result > 0) {
            return SaResult.ok("注册成功");
        } else {
            return SaResult.error("注册失败");
        }
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public SaResult login(@ApiParam("用户名") @RequestParam String username, 
                          @ApiParam("密码") @RequestParam String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            return SaResult.error("用户不存在");
        }

        boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());
        if (!passwordMatch) {
            return SaResult.error("密码错误");
        }
        
        StpUtil.login(user.getId());
        return SaResult.ok("登录成功").setData(StpUtil.getTokenValue());
    }

    @ApiOperation("用户退出")
    @PostMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("退出成功");
    }
}
