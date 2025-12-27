package com.example.labmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.entity.Device;
import com.example.labmanagementsystem.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices") // 所有接口路径前缀
public class DeviceController {


    private DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 查询所有设备
     */
    @GetMapping
    public Result<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.list();

        return Result.success(devices);
    }

    /**
     * 根据 ID 查询单个设备
     */
    @GetMapping("/{id}")
    public Result<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getById(id);
        if (device == null) {
            return Result.error(404, "设备未找到");
        }
        return Result.success(device);
    }

    /**
     * 分页查询设备（支持按名称、状态、类别模糊/精确搜索）
     */
    @GetMapping("/page")
    public Result<IPage<Device>> getDevicesPage(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String category){

        if (size>100) size = 100; //最多查100条，防止攻击

        //构造分页对象
        Page<Device> pageObj = new Page<>(page,size);

        //构造查询条件
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()){
            //模糊匹配
            queryWrapper.like(Device::getName,name);
        }
        //精确匹配
        if (status != null){
            queryWrapper.eq(Device::getStatus,status);
        }
        if (category != null){
            queryWrapper.eq(Device::getCategory,category);
        }

        IPage<Device> result = deviceService.page(pageObj, queryWrapper);
        // 执行分页查询
        return Result.success(result);
    }


    /**
     * 新增设备
     */
    @PostMapping
    public Result<Device> createDevice(@RequestBody Device device) {
        deviceService.save(device); // 自动填充 createdAt/updatedAt
        return Result.success(device);
    }

    /**
     * 更新设备
     * @PathVariable: 从 URL 路径中取参数（如 /devices/1 → id=1）
     * @RequestBody :从请求体（JSON）中解析对象
     */
    @PutMapping("/{id}")
    public Result<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        Device existing = deviceService.getById(id);
        if (existing == null) {
            return Result.error(404, "要更新的设备不存在");
        }

        device.setId(id);
        deviceService.updateById(device);
        return Result.success(device);
    }

    /**
     * 删除设备
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDevice(@PathVariable Long id) {

        Device existing = deviceService.getById(id);
        if (existing == null) {
            return Result.error(404, "要删除的设备不存在");
        }
        deviceService.removeById(id);
        return Result.success(null);
    }


}
