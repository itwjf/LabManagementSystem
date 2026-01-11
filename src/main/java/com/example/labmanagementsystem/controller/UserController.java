package com.example.labmanagementsystem.controller;

import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.entity.User;
import com.example.labmanagementsystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users") // 所有接口路径前缀
public class UserController {

    private final UserMapper userMapper;

    @Autowired
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 查询所有用户，用于下拉选择
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        return Result.success(users);
    }
}
