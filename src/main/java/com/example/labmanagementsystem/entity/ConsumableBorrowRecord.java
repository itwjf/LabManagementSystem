package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("consumable_borrow_record")
public class ConsumableBorrowRecord {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 耗材ID
     */
    private Long consumableId;

    /**
     * 借用人员ID（关联user.id）
     */
    private Long borrowerId;

    /**
     * 冗余：借用人员姓名
     */
    private String borrowerName;

    /**
     * 借用部门
     */
    private String borrowDepartment;

    /**
     * 借用数量
     */
    private Integer borrowQuantity;

    /**
     * 借用日期
     */
    private LocalDate borrowDate;

    /**
     * 预计归还日期
     */
    private LocalDate expectedReturnDate;

    /**
     * 登记人ID（关联user.id）
     */
    private Long registerPersonId;

    /**
     * 冗余：登记人姓名
     */
    private String registerPersonName;

    /**
     * 实际归还数量
     */
    private Integer returnQuantity;

    /**
     * 实际归还日期
     */
    private LocalDate actualReturnDate;

    /**
     * 损耗情况（完好/部分损耗/全部损耗）
     */
    private String damageCondition;

    /**
     * 经办人ID（关联user.id）
     */
    private Long handlerId;

    /**
     * 冗余：经办人姓名
     */
    private String handlerName;

    /**
     * 状态（未归还/部分未归还/已归还）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

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

    // 新增：耗材名称（来自consumable表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是consumable_borrow_record表的字段
    private String consumableName;

    // 新增：耗材规格型号（来自consumable表）
    @TableField(exist = false)
    private String specModel;

    // 新增：耗材单位（来自consumable表）
    @TableField(exist = false)
    private String unit;
}
