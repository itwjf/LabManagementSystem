package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.AssetOutRecordCreateDTO;
import com.example.labmanagementsystem.entity.Asset;
import com.example.labmanagementsystem.entity.AssetOutRecord;
import com.example.labmanagementsystem.mapper.AssetOutRecordMapper;
import com.example.labmanagementsystem.service.AssetOutRecordService;
import com.example.labmanagementsystem.service.AssetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 资产出库记录服务实现类
 */
@Service
public class AssetOutRecordServiceImpl extends ServiceImpl<AssetOutRecordMapper, AssetOutRecord> implements AssetOutRecordService {

    @Autowired
    private AssetService assetService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAssetOutRecord(AssetOutRecordCreateDTO dto) {
        // 1. 创建出库记录
        AssetOutRecord outRecord = new AssetOutRecord();
        BeanUtils.copyProperties(dto, outRecord);
        
        // 设置出库日期
        outRecord.setOutDate(LocalDateTime.now());
        
        // 保存出库记录
        this.save(outRecord);
        
        // 2. 更新资产信息
        Asset asset = assetService.getById(dto.getAssetId());
        if (asset != null) {
            // 减少可用数量和总数量
            asset.setTotalQuantity(asset.getTotalQuantity() - dto.getQuantity());
            asset.setAvailableQuantity(asset.getAvailableQuantity() - dto.getQuantity());
            
            // 保存更新
            assetService.updateById(asset);
        }
    }

    @Override
    public Page<AssetOutRecord> getAllOutRecords(Page<AssetOutRecord> page, Long assetId, String assetName, Long labId, String outReason, String approvalStatus, String outDateStart, String outDateEnd) {
        return baseMapper.selectAllOutRecordsWithAssetAndLabName(page, assetId, assetName, labId, outReason, approvalStatus, outDateStart, outDateEnd);
    }
}
