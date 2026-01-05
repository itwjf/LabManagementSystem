package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceBorrowCreateDTO {
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "预计归还时间不能为空")
    private LocalDateTime expectedReturnTime;

    @NotNull(message = "借用用途不能为空")
    private String purpose;

    // 可选，默认“正常”
    private String deviceConditionOnBorrow;
}
