package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.entity.LabInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * LabInfo 表的 Mapper 接口
 * 继承 BaseMapper<LabInfo> 后，自动拥有：
 * - insert, delete, update, select 等通用方法
 * - 无需写 SQL！
 */
@Mapper // 告诉 Spring 这是一个 MyBatis Mapper（必须加！）
public interface LabInfoMapper extends BaseMapper<LabInfo> {
    // 基础CRUD方法已由MyBatis-Plus提供
    
    // 分页查询：所有实训室信息
    Page<LabInfo> selectAllLabs(
            Page<LabInfo> page,
            @Param("labName") String labName,
            @Param("location") String location,
            @Param("status") String status
    );
}
