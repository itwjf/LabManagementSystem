package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.DeviceBorrowCreateDTO;
import com.example.labmanagementsystem.dto.DeviceReturnDTO;
import com.example.labmanagementsystem.entity.Device;
import com.example.labmanagementsystem.entity.DeviceBorrow;
import com.example.labmanagementsystem.entity.User;
import com.example.labmanagementsystem.exception.ServiceException;
import com.example.labmanagementsystem.mapper.DeviceBorrowMapper;
import com.example.labmanagementsystem.security.LoginUserDetails;
import com.example.labmanagementsystem.service.DeviceBorrowService;
import com.example.labmanagementsystem.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DeviceBorrowServiceImpl extends ServiceImpl<DeviceBorrowMapper, DeviceBorrow> implements DeviceBorrowService {

    @Autowired
    private DeviceService deviceService;

    /**
     *
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBorrow(DeviceBorrowCreateDTO dto) {
        // 1. 从 SecurityContext 获取当前用户（无需查库！）
        LoginUserDetails userDetails = (LoginUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var currentUser = userDetails.getUser(); // 这就是完整的 User 对象

        // 2. 获取设备
        Device device = deviceService.getById(dto.getDeviceId());
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }
        if (!"可用".equals(device.getStatus())) {
            throw new RuntimeException("设备不可借用，当前状态：" + device.getStatus());
        }

        // 3. 构建借用记录
        DeviceBorrow borrow = new DeviceBorrow();
        borrow.setDeviceId(dto.getDeviceId());
        borrow.setBorrowerId(currentUser.getId());
        borrow.setBorrowerName(currentUser.getRealName());
        borrow.setDepartment(currentUser.getDepartment());
        borrow.setLabLocation(device.getLocation());
        borrow.setPurpose(dto.getPurpose());
        borrow.setExpectedReturnTime(dto.getExpectedReturnTime());
        borrow.setDeviceConditionOnBorrow(
                dto.getDeviceConditionOnBorrow() != null ?
                        dto.getDeviceConditionOnBorrow() : "正常"
        );
        borrow.setRegistrarId(currentUser.getId());
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setReturnStatus("未归还");
        borrow.setCreatedAt(LocalDateTime.now());

        this.save(borrow);

        // 4. 更新设备状态
        device.setStatus("已借出");
        deviceService.updateById(device);
    }

    /**
     * 设备归还登记
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnDevice(DeviceReturnDTO dto) {

        DeviceBorrow borrow = this.getById(dto.getId());

        if (borrow == null) {
            throw new RuntimeException("借用记录不存在");
        }
        if (!"未归还".equals(borrow.getReturnStatus())) {
            throw new RuntimeException("设备已归还，无法重复操作");
        }

        // 1. 更新借用记录
        // 解析前端传来的日期字符串，支持格式：yyyy-MM-dd HH:mm:ss
        LocalDateTime actualReturnTime = LocalDateTime.parse(dto.getActualReturnTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        borrow.setActualReturnTime(actualReturnTime);
        borrow.setReturnStatus("正常".equals(dto.getDeviceStatusOnReturn()) ? "已归还" : "异常归还");
        borrow.setDeviceStatusOnReturn(dto.getDeviceStatusOnReturn());
        borrow.setDamageDescription(dto.getDamageDescription());
        borrow.setHandler(dto.getHandler());
        borrow.setReturnTime(LocalDateTime.now());
        
        this.updateById(borrow);

        // 2. 更新设备状态
        Device device = deviceService.getById(dto.getDeviceId());
        if (device != null) {
            // 如果设备归还状态正常，设备状态变为"可用"；否则保持异常状态（损坏/丢失）
            if ("正常".equals(dto.getDeviceStatusOnReturn())) {
                device.setStatus("可用");
            } else if ("损坏".equals(dto.getDeviceStatusOnReturn())) {
                device.setStatus("损坏");
            } else if ("丢失".equals(dto.getDeviceStatusOnReturn())) {
                device.setStatus("丢失");
            }
            deviceService.updateById(device);
        }
    }

    /**
     *     公共查询方法（不对外暴露）
     */
    private IPage<DeviceBorrow> queryBorrows(Page<DeviceBorrow> page, LambdaQueryWrapper<DeviceBorrow> wrapper) {
        wrapper.orderByDesc(DeviceBorrow::getBorrowTime);
        return this.page(page, wrapper);
    }
    /**
     * 分页查询我的借用记录
     */
    @Override
    public IPage<DeviceBorrow> getMyBorrows(int page, int size, String deviceId, String deviceName, String returnStatus, String borrowTimeStart, String borrowTimeEnd) {
        LoginUserDetails userDetails = (LoginUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();

        Page<DeviceBorrow> pageObj = new Page<>(page, size);

        // 使用自定义SQL查询，联表获取设备名称
        return baseMapper.selectMyBorrowsWithDeviceName(
                pageObj,
                userId,
                deviceId,
                deviceName,
                returnStatus,
                borrowTimeStart,
                borrowTimeEnd
        );
    }

    /**
     * 获取全部借用记录：一般是管理员用户
     */
    @Override
    public IPage<DeviceBorrow> getAllBorrows(int page, int size, String deviceId, String deviceName, String borrowerName, String department, String returnStatus, String borrowTimeStart, String borrowTimeEnd) {
        // 1. 获取当前登录用户详情
        LoginUserDetails userDetails = (LoginUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userDetails.getUser().getRole();

        if(!"ADMIN".equals(role)){
            throw new ServiceException("权限不足：仅管理员可查看全部借用记录");
        }

        // 3. 构造分页对象
        Page<DeviceBorrow> pageObj = new Page<>(page, size);

        // 使用自定义SQL查询，联表获取设备名称
        return baseMapper.selectAllBorrowsWithDeviceName(
                pageObj,
                deviceId,
                deviceName,
                borrowerName,
                department,
                returnStatus,
                borrowTimeStart,
                borrowTimeEnd
        );
    }
}
