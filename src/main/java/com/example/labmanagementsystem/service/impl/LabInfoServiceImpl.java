package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.LabInfoCreateUpdateDTO;
import com.example.labmanagementsystem.entity.LabInfo;
import com.example.labmanagementsystem.mapper.LabInfoMapper;
import com.example.labmanagementsystem.service.LabInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 实训室信息服务实现类
 */
@Service
public class LabInfoServiceImpl extends ServiceImpl<LabInfoMapper, LabInfo> implements LabInfoService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateLab(LabInfoCreateUpdateDTO dto) {
        LabInfo labInfo = new LabInfo();
        BeanUtils.copyProperties(dto, labInfo);
        
        // 如果没有指定状态，设置默认状态为"正常使用"
        if (labInfo.getStatus() == null || labInfo.getStatus().isEmpty()) {
            labInfo.setStatus("正常使用");
        }
        
        this.saveOrUpdate(labInfo);
    }

    @Override
    public Page<LabInfo> getAllLabs(Page<LabInfo> page, String labName, String location, String status) {
        return baseMapper.selectAllLabs(page, labName, location, status);
    }
}
