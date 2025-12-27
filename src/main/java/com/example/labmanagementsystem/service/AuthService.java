package com.example.labmanagementsystem.service;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录，返回 JWT Token
     */
    String login(String username,String password);
}
