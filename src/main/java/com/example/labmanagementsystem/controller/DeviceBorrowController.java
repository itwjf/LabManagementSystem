package com.example.labmanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.dto.DeviceBorrowCreateDTO;
import com.example.labmanagementsystem.dto.DeviceReturnDTO;
import com.example.labmanagementsystem.entity.DeviceBorrow;
import com.example.labmanagementsystem.service.DeviceBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device-borrows")
@RequiredArgsConstructor
public class DeviceBorrowController {

    private final DeviceBorrowService deviceBorrowService;

    /**
     * 设备借用登记
     */
    @PostMapping("/borrow")
    public Result<?> borrowDevice(@RequestBody DeviceBorrowCreateDTO dto){
        deviceBorrowService.createBorrow(dto);
        return Result.success("借用登记成功");
    }

    /**
     * 设备归还登记
     */
    @PostMapping("/return")
    public Result<?> returnDevice(@RequestBody DeviceReturnDTO dto) {
        deviceBorrowService.returnDevice(dto);
        return Result.success("归还登记成功");
    }

    /**
     * 分页查询我的借用记录
     */
    @GetMapping("/my")
    public Result<IPage<DeviceBorrow>> getMyBorrows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        if (size > 100) size = 100;

        // 注意：Service 中需提供按 borrowerId 查询的方法
        IPage<DeviceBorrow> result = deviceBorrowService.getMyBorrows(page, size);

        return Result.success(result);
    }

    /**
     * 管理员：分页查询所有借用记录
     */
    @GetMapping
    public Result<IPage<DeviceBorrow>> getAllBorrows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (size > 100) size = 100;

        // Service 中需新增方法：getAllBorrows(page, size)
        IPage<DeviceBorrow> result = deviceBorrowService.getAllBorrows(page, size);
        return Result.success(result);
    }
}
