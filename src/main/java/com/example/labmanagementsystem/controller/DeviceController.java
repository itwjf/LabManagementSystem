package com.example.labmanagementsystem.controller;


import com.example.labmanagementsystem.entity.Device;
import com.example.labmanagementsystem.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices") // 所有接口路径前缀
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 查询所有设备
     */
    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.list(); // MyBatis-Plus 提供的方法
    }

    /**
     * 根据 ID 查询单个设备
     */
    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable Long id) {
        return deviceService.getById(id);
    }

    /**
     * 新增设备
     */
    @PostMapping
    public Device createDevice(@RequestBody Device device) {
        deviceService.save(device); // 自动填充 createdAt/updatedAt
        return device;
    }

    /**
     * 更新设备
     * @PathVariable: 从 URL 路径中取参数（如 /devices/1 → id=1）
     * @RequestBody :从请求体（JSON）中解析对象
     */
    @PutMapping("/{id}")
    public Device updateDevice(@PathVariable Long id, @RequestBody Device device) {
        device.setId(id); // 确保 ID 一致
        deviceService.updateById(device); // 自动更新 updatedAt
        return device;
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id) {
        deviceService.removeById(id);
    }


}
