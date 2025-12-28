package com.example.labmanagementsystem.controller;

import com.example.labmanagementsystem.entity.User;
import com.example.labmanagementsystem.security.LoginUserDetails;
import com.example.labmanagementsystem.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/me")
    public ResponseEntity<?> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录");
        }
        LoginUserDetails details = (LoginUserDetails) authentication.getPrincipal();
        User user = details.getUser();
        return ResponseEntity.ok(new CurrentUserResponse(user));
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

    @Data
    public static class CurrentUserResponse {
        private String username;
        private String realName;
        private String role;
        private String department;
        private String contact;

        public CurrentUserResponse(User user) {
            this.username = user.getUsername();
            this.realName = user.getRealName();
            this.role = user.getRole();
            this.department = user.getDepartment();
            this.contact = user.getContact();
        }
    }
}
