package com.example.controller;

/**
 * 用户实体控制器父接口
 */
public interface UserEntityControllerParent {
    /**
     * 根据手机号查询用户ID
     *
     * @param phone 手机号
     * @return 用户ID，如果未找到则返回 null
     */
    Long selectByPhone(String phone);
}