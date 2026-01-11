package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.AssetInRecordCreateDTO;
import com.example.labmanagementsystem.entity.Asset;
import com.example.labmanagementsystem.entity.AssetInRecord;
import com.example.labmanagementsystem.mapper.AssetInRecordMapper;
import com.example.labmanagementsystem.service.AssetInRecordService;
import com.example.labmanagementsystem.service.AssetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@Service
public class AssetInRecordServiceImpl extends ServiceImpl<AssetInRecordMapper, AssetInRecord> implements AssetInRecordService {

    @Autowired
    private AssetService assetService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetInRecord createAssetInRecord(AssetInRecordCreateDTO dto) {
        // 1. 创建入库记录
        AssetInRecord inRecord = new AssetInRecord();
        BeanUtils.copyProperties(dto, inRecord);
        
        // 设置入库日期
        inRecord.setInDate(LocalDateTime.now());
        
        // 计算总金额
        inRecord.setTotalAmount(dto.getUnitPrice().multiply(new BigDecimal(dto.getQuantity())));
        
        // 保存入库记录
        this.save(inRecord);
        
        // 2. 更新资产信息
        Asset asset = assetService.getById(dto.getAssetId());
        if (asset != null) {
            // 增加数量
            asset.setTotalQuantity(asset.getTotalQuantity() + dto.getQuantity());
            asset.setAvailableQuantity(asset.getAvailableQuantity() + dto.getQuantity());
            
            // 更新总价值
            // 注意：这里将 Integer 类型的数量转换为 BigDecimal 类型
            asset.setTotalValue(asset.getTotalValue().add(dto.getUnitPrice().multiply(new BigDecimal(dto.getQuantity()))));
            
            // 保存更新
            assetService.updateById(asset);
        }
        
        // 返回创建的入库记录
        return inRecord;
    }

    @Override
    public Page<AssetInRecord> getAllInRecords(Page<AssetInRecord> page, Long assetId, String assetName, Long labId, String purchaserName, String inDateStart, String inDateEnd) {
        return baseMapper.selectAllInRecordsWithAssetAndLabName(page, assetId, assetName, labId, purchaserName, inDateStart, inDateEnd);
    }
}
