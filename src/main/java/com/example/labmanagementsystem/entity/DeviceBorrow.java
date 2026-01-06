package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("device_borrow")
public class DeviceBorrow {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;
    private Long borrowerId;
    private String borrowerName;
    private String department;
    private String labLocation;

    private LocalDateTime borrowTime;          // 借用时间（创建时自动设为 now）
    private LocalDateTime expectedReturnTime;  // 预计归还时间（前端传）
    private LocalDateTime actualReturnTime;    // 实际归还时间（归还时设为 now）

    private String purpose;
    private String deviceConditionOnBorrow;   // 借出状态：正常/轻微瑕疵

    private String returnStatus;              // 未归还 / 正常 / 损坏 / 丢失
    private String damageDescription;

    private Long registrarId;                 // 登记人（通常 = borrowerId）
    private LocalDateTime createdAt;


    // 新增：设备名称（来自 device 表）
    @TableField(exist = false) // 告诉 MyBatis Plus：这不是 device_borrow 表的字段
    private String deviceName;
}
