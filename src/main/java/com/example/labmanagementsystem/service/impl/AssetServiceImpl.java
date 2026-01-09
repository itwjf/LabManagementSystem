package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.AssetInRecordCreateDTO;
import com.example.labmanagementsystem.entity.Asset;
import com.example.labmanagementsystem.mapper.AssetMapper;
import com.example.labmanagementsystem.service.AssetService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAsset(AssetInRecordCreateDTO assetInRecordCreateDTO) {
        Asset asset = new Asset();

        // 复制 DTO 到实体
        BeanUtils.copyProperties(assetInRecordCreateDTO, asset);

        // 计算总价值和可用数量
        // 注意：这里将 Integer 类型的数量转换为 BigDecimal 类型
        // 
        asset.setTotalValue(asset.getUnitPrice().multiply(new BigDecimal(assetInRecordCreateDTO.getQuantity())));
        
        
        asset.setTotalQuantity(assetInRecordCreateDTO.getQuantity());
        asset.setAvailableQuantity(assetInRecordCreateDTO.getQuantity());

        this.saveOrUpdate(asset);
    }

    @Override
    public Page<Asset> selectAllAssets(Page<Asset> page, String assetName, Integer typeId, Long labId, String status) {
        return baseMapper.selectAllAssetsWithTypeAndLabName(page, assetName, typeId, labId, status);
    }
}
