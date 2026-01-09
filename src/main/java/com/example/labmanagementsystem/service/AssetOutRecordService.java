package com.example.labmanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.labmanagementsystem.dto.AssetOutRecordCreateDTO;
import com.example.labmanagementsystem.entity.AssetOutRecord;

/**
 * 资产出库记录服务接口
 */
public interface AssetOutRecordService extends IService<AssetOutRecord> {
    
    /**
     * 创建资产出库记录
     * @param dto 出库记录创建DTO
     * @return 创建的出库记录
     */
    AssetOutRecord createAssetOutRecord(AssetOutRecordCreateDTO dto);
    
    /**
     * 分页查询所有出库记录
     * @param page 分页对象
     * @param assetId 资产ID
     * @param assetName 资产名称
     * @param labId 实训室ID
     * @param outReason 出库原因
     * @param approvalStatus 审批状态
     * @param outDateStart 出库日期开始
     * @param outDateEnd 出库日期结束
     * @return 分页出库记录
     */
    Page<AssetOutRecord> getAllOutRecords(Page<AssetOutRecord> page, Long assetId, String assetName, Long labId, String outReason, String approvalStatus, String outDateStart, String outDateEnd);
}
