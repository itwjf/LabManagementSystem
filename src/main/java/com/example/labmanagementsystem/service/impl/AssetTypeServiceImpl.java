package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.AssetTypeCreateUpdateDTO;
import com.example.labmanagementsystem.entity.AssetType;
import com.example.labmanagementsystem.mapper.AssetTypeMapper;
import com.example.labmanagementsystem.service.AssetTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资产类型服务实现类
 */
@Service
public class AssetTypeServiceImpl extends ServiceImpl<AssetTypeMapper, AssetType> implements AssetTypeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateAssetType(AssetTypeCreateUpdateDTO dto) {
        AssetType assetType = new AssetType();
        BeanUtils.copyProperties(dto, assetType);
        this.saveOrUpdate(assetType);
    }

    @Override
    public Page<AssetType> getAllAssetTypes(Page<AssetType> page, String typeName) {
        return baseMapper.selectAllAssetTypes(page, typeName);
    }
}
