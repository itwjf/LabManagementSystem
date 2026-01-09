package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("consumable")
public class Consumable {

    /**
     * 耗材ID
     */
    @TableId(type = IdType.AUTO)
    private Long consumableId;

    /**
     * 耗材名称
     */
    private String consumableName;

    /**
     * 规格型号
     */
    private String specModel;

    /**
     * 单位
     */
    private String unit;

    /**
     * 初始库存
     */
    private Integer initialStock;

    /**
     * 当前库存
     */
    private Integer currentStock;

    /**
     * 最低库存阈值
     */
    private Integer minStock;

    /**
     * 存放实训室编号
     */
    private Long labId;

    /**
     * 保管人ID（关联user.id）
     */
    private Long keeperId;

    /**
     * 冗余：保管人姓名
     */
    private String keeperName;

    /**
     * 状态（正常/停用）
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

    // 新增：实训室名称（来自lab_info表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是consumable表的字段
    private String labName;
}
