package com.example.labmanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.labmanagementsystem.dto.AssetTypeCreateUpdateDTO;
import com.example.labmanagementsystem.entity.AssetType;

/**
 * 资产类型服务接口
 */
public interface AssetTypeService extends IService<AssetType> {
    
    /**
     * 创建或更新资产类型
     * @param dto 资产类型创建/更新DTO
     */
    void saveOrUpdateAssetType(AssetTypeCreateUpdateDTO dto);
    
    /**
     * 分页查询所有资产类型
     * @param page 分页对象
     * @param typeName 资产类型名称
     * @return 分页资产类型
     */
    Page<AssetType> getAllAssetTypes(Page<AssetType> page, String typeName);
}
