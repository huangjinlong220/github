package com.example.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, username).eq(User::getPassword, password);
        User user = userMapper.selectOne(wrapper);
        
        if (user != null) {
            StpUtil.login(user.getId());
            return StpUtil.getTokenValue();
        }
        return null;
    }
}
