package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceReturnDTO {

    @NotNull(message = "借用记录ID不能为空")
    private Long id;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "实际归还时间不能为空")
    private String actualReturnTime;

    @NotNull(message = "设备归还状态不能为空")
    private String deviceStatusOnReturn; // 枚举值：正常 / 损坏 / 丢失

    // 当 deviceStatusOnReturn != "正常" 时填写损坏/丢失说明
    private String damageDescription;

    @NotNull(message = "经办人不能为空")
    private String handler;
}
