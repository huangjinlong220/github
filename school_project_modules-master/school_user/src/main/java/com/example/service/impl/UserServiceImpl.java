package com.example.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public SaResult saveUser(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, user.getPhone());
        User existUser = this.getOne(wrapper);
        if (existUser != null) {
            return SaResult.error("该电话号码已注册");
        }
        
        String encryptedPwd = BCrypt.hashpw(user.getPassword());
        user.setPassword(encryptedPwd);
        user.setSubmitTime(LocalDateTime.now());
        
        boolean success = this.save(user);
        if (success) {
            return SaResult.ok("用户保存成功");
        }
        return SaResult.error("用户保存失败");
    }

    @Override
    public SaResult updateUser(User user) {
        if (user.getId() == null) {
            return SaResult.error("用户ID不能为空");
        }
        
        User existUser = this.getById(user.getId());
        if (existUser == null) {
            return SaResult.error("用户不存在");
        }
        
        if (user.getPassword() != null && !user.getPassword().equals(existUser.getPassword())) {
            String encryptedPwd = BCrypt.hashpw(user.getPassword());
            user.setPassword(encryptedPwd);
        }
        
        boolean success = this.updateById(user);
        if (success) {
            return SaResult.ok("用户修改成功");
        }
        return SaResult.error("用户修改失败");
    }

    @Override
    public SaResult deleteUser(Long id) {
        if (id == null) {
            return SaResult.error("用户ID不能为空");
        }
        
        User existUser = this.getById(id);
        if (existUser == null) {
            return SaResult.error("用户不存在");
        }
        
        boolean success = this.removeById(id);
        if (success) {
            return SaResult.ok("用户删除成功");
        }
        return SaResult.error("用户删除失败");
    }

    @Override
    public SaResult selectPage(Integer page, Integer size, Map<String, Object> params) {
        Page<User> page1 = new Page<>(page, size);
        String name = params.get("name") != null ? params.get("name").toString() : null;
        String startTime = params.get("startTime") != null ? params.get("startTime").toString() : null;
        String endTime = params.get("endTime") != null ? params.get("endTime").toString() : null;
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name), User::getName, name)
                .between(StringUtils.hasText(startTime) && StringUtils.hasText(endTime), 
                        User::getSubmitTime, startTime, endTime)
                .ge(StringUtils.hasText(startTime), User::getSubmitTime, startTime)
                .le(StringUtils.hasText(endTime), User::getSubmitTime, endTime);
        
        Page<User> userPage = this.page(page1, queryWrapper);
        return SaResult.data(userPage);
    }

    @Override
    public SaResult initPassword(Long id) {
        if (id == null) {
            return SaResult.error("用户ID不能为空");
        }
        
        User user = this.getById(id);
        if (user == null) {
            return SaResult.error("用户不存在");
        }
        
        String encryptedPwd = BCrypt.hashpw("123456");
        user.setPassword(encryptedPwd);
        boolean success = this.updateById(user);
        
        if (success) {
            return SaResult.ok("密码初始化成功，默认为123456");
        }
        return SaResult.error("密码初始化失败");
    }
}
