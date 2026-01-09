package com.example.labmanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.labmanagementsystem.dto.AssetInRecordCreateDTO;
import com.example.labmanagementsystem.entity.AssetInRecord;

public interface AssetInRecordService extends IService<AssetInRecord> {
    // 创建资产入库记录
    AssetInRecord createAssetInRecord(AssetInRecordCreateDTO dto);
    
    // 分页查询所有入库记录
    Page<AssetInRecord> getAllInRecords(Page<AssetInRecord> page, Long assetId, String assetName, Long labId, String purchaserName, String inDateStart, String inDateEnd);
}
