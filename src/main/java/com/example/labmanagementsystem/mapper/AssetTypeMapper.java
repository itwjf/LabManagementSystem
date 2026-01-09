package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.entity.AssetType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AssetType 表的 Mapper 接口
 * 继承 BaseMapper<AssetType> 后，自动拥有：
 * - insert, delete, update, select 等通用方法
 * - 无需写 SQL！
 */
@Mapper // 告诉 Spring 这是一个 MyBatis Mapper（必须加！）
public interface AssetTypeMapper extends BaseMapper<AssetType> {
    // 基础CRUD方法已由MyBatis-Plus提供
    
    // 分页查询：所有资产类型
    Page<AssetType> selectAllAssetTypes(
            Page<AssetType> page,
            @Param("typeName") String typeName
    );
}
