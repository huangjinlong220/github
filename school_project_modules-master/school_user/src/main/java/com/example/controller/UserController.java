package com.example.controller;

import cn.dev33.satoken.util.SaResult;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.validation.Valida;
import com.example.entity.User;
import com.example.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController implements UserEntityControllerParent {

    @Resource
    private UserService userService;

    @ApiOperation("测试接口")
    @GetMapping("/h1")
    public String h1() {
        return "hello user";
    }

    @ApiOperation("查询所有用户")
    @GetMapping("/list")
    public SaResult list() {
        List<User> list = userService.list();
        return SaResult.ok().setData(list);
    }

    @ApiOperation("根据ID查询用户")
    @GetMapping("/getById")
    public SaResult getById(@ApiParam("用户ID") @RequestParam Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return SaResult.ok().setData(user);
        }
        return SaResult.error("用户不存在");
    }

    @ApiOperation("保存用户")
    @PostMapping("/save")
    public SaResult save(@Validated(Valida.Create.class) @RequestBody User user) {
        return userService.saveUser(user);
    }

    @ApiOperation("修改用户")
    @PostMapping("/update")
    public SaResult update(@Validated(Valida.Update.class) @RequestBody User user) {
        return userService.updateUser(user);
    }

    @ApiOperation("删除用户")
    @GetMapping("/delete")
    public SaResult delete(@ApiParam("用户ID") @RequestParam Long id) {
        return userService.deleteUser(id);
    }

    @ApiOperation("分页条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", defaultValue = "1", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页大小", defaultValue = "10", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "用户名", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "string", paramType = "query")
    })
    @GetMapping("/page")
    public SaResult selectPage(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam Map<String, Object> params) {
        return userService.selectPage(page, size, params);
    }

    @ApiOperation("初始化密码")
    @GetMapping("/initPassword")
    public SaResult initPassword(@ApiParam("用户ID") @RequestParam Long id) {
        return userService.initPassword(id);
    }

    @Override
    @ApiOperation(value = "根据电话号码查询用户ID", notes = "内部接口，用于解决模块间循环依赖")
    @GetMapping("/selectByPhone")
    public Long selectByPhone(@ApiParam(value = "电话号码", required = true) @RequestParam String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userService.getOne(wrapper);
        return user != null ? user.getId() : null;
    }
}
