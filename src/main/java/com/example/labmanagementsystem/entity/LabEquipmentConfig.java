package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("lab_equipment_config")
public class LabEquipmentConfig {

    /**
     * 配置ID
     */
    @TableId(type = IdType.AUTO)
    private Integer configId;

    /**
     * 实训室编号
     */
    private Long labId;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 规格型号
     */
    private String specModel;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 设备状态（正常/故障/维修中）
     */
    private String status;

    /**
     * 更新时间
     * FieldFill.INSERT_UPDATE 表示：INSERT 和 UPDATE 都会自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 新增：实训室名称（来自lab_info表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是lab_equipment_config表的字段
    private String labName;
}
