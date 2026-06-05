package com.example.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    SaResult saveUser(User user);
    SaResult updateUser(User user);
    SaResult deleteUser(Long id);
    SaResult selectPage(Integer page, Integer size, Map<String, Object> params);
    SaResult initPassword(Long id);
}
