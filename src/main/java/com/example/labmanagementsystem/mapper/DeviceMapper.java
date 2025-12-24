package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.labmanagementsystem.entity.Device;
import org.apache.ibatis.annotations.Mapper;


/**
 * Device 表的 Mapper 接口
 * 继承 BaseMapper<Device> 后，自动拥有：
 * - insert, delete, update, select 等通用方法
 * - 无需写 SQL！
 */
@Mapper // ← 告诉 Spring 这是一个 MyBatis Mapper（必须加！）
public interface DeviceMapper extends BaseMapper<Device> {
    // 空着就行！MyBatis-Plus 已提供所有基础方法
}
