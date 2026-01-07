package com.example.labmanagementsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.labmanagementsystem.dto.DeviceBorrowCreateDTO;
import com.example.labmanagementsystem.dto.DeviceReturnDTO;
import com.example.labmanagementsystem.entity.DeviceBorrow;

public interface DeviceBorrowService extends IService<DeviceBorrow> {

    void createBorrow(DeviceBorrowCreateDTO dto);

    void returnDevice(DeviceReturnDTO dto);

    IPage<DeviceBorrow> getMyBorrows(int page, int size, String deviceId, String deviceName, String returnStatus, String borrowTimeStart, String borrowTimeEnd);

    IPage<DeviceBorrow> getAllBorrows(int page, int size, String deviceId, String deviceName, String borrowerName, String department, String returnStatus, String borrowTimeStart, String borrowTimeEnd);
}
