package com.example.labmanagementsystem.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labmanagementsystem.entity.User;
import com.example.labmanagementsystem.mapper.UserMapper;
import com.example.labmanagementsystem.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // 1. 检查 Header 是否存在且以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 提取 Token
        String jwt = authHeader.substring(7); // 去掉 "Bearer "
        String username = jwtUtil.extractUsername(jwt);

        // 3. 如果用户名存在且未认证，则进行认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 查询用户
            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, username)
            );
            if (user == null) {
                throw new RuntimeException("Token 中的用户不存在");
            }

            // 4. 验证 Token 有效性
            if (jwtUtil.isTokenValid(jwt, username)) {
                // 构造 UserDetails
                LoginUserDetails userDetails = new LoginUserDetails(user);
                // 构造 Authentication 对象
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
