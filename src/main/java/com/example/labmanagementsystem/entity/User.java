package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username; // 学号/工号

    @TableField("password")
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("role")
    private String role; // STUDENT / TEACHER / ADMIN

    @TableField("department")
    private String department;

    @TableField("contact")
    private String contact;

    @TableField("created_at")
    private java.util.Date createdAt;
}
