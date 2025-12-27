package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.labmanagementsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * MyBatis-Plus 自动提供 selectById, insert 等
     * lambdaQuery().eq(User::getUsername, username) 查询，无需手写 SQL
     */


}
