package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("lab_info")
public class LabInfo {

    /**
     * 实训室编号
     */
    @TableId(type = IdType.AUTO)
    private Long labId;

    /**
     * 实训室名称
     */
    private String labName;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 容纳人数
     */
    private Integer capacity;

    /**
     * 管理员ID（关联user.id）
     */
    private Long managerId;

    /**
     * 冗余：管理员姓名
     */
    private String managerName;

    /**
     * 联系电话
     */
    private String contact;

    /**
     * 实训室状态（正常使用/维护中/已停用等）
     */
    private String status;

    /**
     * 创建时间
     * FieldFill.INSERT 表示：只有在 INSERT（插入）时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * FieldFill.INSERT_UPDATE 表示：INSERT 和 UPDATE 都会自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
