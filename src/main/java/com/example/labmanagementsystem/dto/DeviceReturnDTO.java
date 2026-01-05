package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceReturnDTO {

    @NotNull(message = "借用记录ID不能为空")
    private Long borrowId;

    @NotNull(message = "归还状态不能为空")
    private String returnStatus; // 枚举值：正常 / 损坏 / 丢失

    // 当 returnStatus != "正常" 时建议填写
    private String damageDescription;
}
