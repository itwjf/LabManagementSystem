package com.example.labmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.labmanagementsystem.dto.DeviceBorrowCreateDTO;
import com.example.labmanagementsystem.dto.DeviceReturnDTO;
import com.example.labmanagementsystem.entity.Device;
import com.example.labmanagementsystem.entity.DeviceBorrow;
import com.example.labmanagementsystem.mapper.DeviceBorrowMapper;
import com.example.labmanagementsystem.security.LoginUserDetails;
import com.example.labmanagementsystem.service.DeviceBorrowService;
import com.example.labmanagementsystem.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
     *
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnDevice(DeviceReturnDTO dto) {

        DeviceBorrow borrow = this.getById(dto.getBorrowId());

        if (borrow == null) {
            throw new RuntimeException("借用记录不存在");
        }
        if (!"未归还".equals(borrow.getReturnStatus())) {
            throw new RuntimeException("设备已归还，无法重复操作");
        }

        borrow.setActualReturnTime(LocalDateTime.now());
        borrow.setReturnStatus(dto.getReturnStatus());

        // 更新设备状态
        Device device = deviceService.getById(borrow.getDeviceId());
        device.setStatus("正常".equals(dto.getReturnStatus()) ? "可用" : dto.getReturnStatus());
        deviceService.updateById(device);

    }

    /**
     * 分页查询我的借用记录
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<DeviceBorrow> getMyBorrows(int page, int size) {
        LoginUserDetails userDetails = (LoginUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();

        Page<DeviceBorrow> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<DeviceBorrow> queryWrapper = new LambdaQueryWrapper<>(); 
        queryWrapper.eq(DeviceBorrow::getBorrowerId, userId)
                .orderByDesc(DeviceBorrow::getBorrowTime);

        return this.page(pageObj, queryWrapper);
    }
}
