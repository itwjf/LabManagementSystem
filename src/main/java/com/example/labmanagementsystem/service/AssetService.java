package com.example.labmanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.labmanagementsystem.dto.AssetInRecordCreateDTO;
import com.example.labmanagementsystem.entity.Asset;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;



public interface AssetService extends IService<Asset>{

    // 资产创建/更新
    void saveAsset(AssetInRecordCreateDTO assetInRecordCreateDTO);

    // 分页查询所有资产
    Page<Asset> selectAllAssets(
            Page<Asset> page,
            @Param("assetName") String assetName,
            @Param("typeId") Integer typeId,
            @Param("labId") Long labId,
            @Param("status") String status
    );


}
