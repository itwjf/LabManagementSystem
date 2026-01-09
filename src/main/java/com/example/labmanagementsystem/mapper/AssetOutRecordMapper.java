package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.entity.AssetOutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AssetOutRecord 表的 Mapper 接口
 * 继承 BaseMapper<AssetOutRecord> 后，自动拥有：
 * - insert, delete, update, select 等通用方法
 * - 无需写 SQL！
 */
@Mapper // 告诉 Spring 这是一个 MyBatis Mapper（必须加！）
public interface AssetOutRecordMapper extends BaseMapper<AssetOutRecord> {
    // 基础CRUD方法已由MyBatis-Plus提供
    
    // 分页查询：所有出库记录（带资产名称和实训室名称）
    Page<AssetOutRecord> selectAllOutRecordsWithAssetAndLabName(
            Page<AssetOutRecord> page,
            @Param("assetId") Long assetId,
            @Param("assetName") String assetName,
            @Param("labId") Long labId,
            @Param("outReason") String outReason,
            @Param("approvalStatus") String approvalStatus,
            @Param("outDateStart") String outDateStart,
            @Param("outDateEnd") String outDateEnd
    );
}
