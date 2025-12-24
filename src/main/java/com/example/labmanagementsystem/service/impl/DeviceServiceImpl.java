package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.entity.Device;
import com.example.labmanagementsystem.mapper.DeviceMapper;
import com.example.labmanagementsystem.service.DeviceService;
import org.springframework.stereotype.Service;


/**
 * DeviceService 的实现类
 * ServiceImpl<DeviceMapper, Device> 自动注入 mapper 并实现 IService 的方法
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
        implements DeviceService {
    // 空着就行！通用 CRUD 已由父类实现
    // 以后复杂的业务逻辑（比如“借设备”）再在这里写

}
