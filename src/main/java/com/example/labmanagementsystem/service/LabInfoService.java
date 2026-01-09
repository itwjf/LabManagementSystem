package com.example.labmanagementsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.labmanagementsystem.dto.LabInfoCreateUpdateDTO;
import com.example.labmanagementsystem.entity.LabInfo;

/**
 * 实训室信息服务接口
 */
public interface LabInfoService extends IService<LabInfo> {
    
    /**
     * 创建或更新实训室信息
     * @param dto 实训室创建/更新DTO
     */
    void saveOrUpdateLab(LabInfoCreateUpdateDTO dto);
    
    /**
     * 分页查询所有实训室信息
     * @param page 分页对象
     * @param labName 实训室名称
     * @param location 地理位置
     * @param status 实训室状态
     * @return 分页实训室信息
     */
    Page<LabInfo> getAllLabs(Page<LabInfo> page, String labName, String location, String status);
}
