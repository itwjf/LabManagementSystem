package com.example.labmanagementsystem.controller;

import com.example.labmanagementsystem.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器：提供登录接口
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 登录请求 DTO
     */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * 登录响应 DTO
     */
    @Data
    public static class LoginResponse {
        private String token;
        private String tokenType = "Bearer";

        public LoginResponse(String token) {
            this.token = token;
        }
    }
}
