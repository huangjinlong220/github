package com.example.controller;

import com.example.entity.UserEntity;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private com.example.controller.UserService userService;

    /**
     * 根据id查询用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable("id") Long id) {
        return userService.getById ( id );
    }
}
