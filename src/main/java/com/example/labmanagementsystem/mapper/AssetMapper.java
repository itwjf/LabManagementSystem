package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Asset 表的 Mapper 接口
 * 继承 BaseMapper<Asset> 后，自动拥有：
 * - insert, delete, update, select 等通用方法
 * - 无需写 SQL！
 */
@Mapper // 告诉 Spring 这是一个 MyBatis Mapper（必须加！）
public interface AssetMapper extends BaseMapper<Asset> {
    // 基础CRUD方法已由MyBatis-Plus提供
    
    // 分页查询：所有资产（带资产类型和实训室名称）
    Page<Asset> selectAllAssetsWithTypeAndLabName(
            Page<Asset> page,
            @Param("assetName") String assetName,
            @Param("typeId") Integer typeId,
            @Param("labId") Long labId,
            @Param("status") String status
    );
}
