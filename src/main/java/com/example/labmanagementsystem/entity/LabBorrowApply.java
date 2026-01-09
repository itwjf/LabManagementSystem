package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("lab_borrow_apply")
public class LabBorrowApply {

    /**
     * 申请单ID
     */
    @TableId(type = IdType.AUTO)
    private Long applyId;

    /**
     * 实训室编号
     */
    private Long labId;

    /**
     * 借用部门
     */
    private String borrowDepartment;

    /**
     * 借用人员ID（关联user.id）
     */
    private Long borrowerId;

    /**
     * 冗余：借用人员姓名
     */
    private String borrowerName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 借用开始时间
     */
    private LocalDateTime startTime;

    /**
     * 借用结束时间
     */
    private LocalDateTime endTime;

    /**
     * 借用用途
     */
    private String purpose;

    /**
     * 登记人ID（关联user.id）
     */
    private Long registerPersonId;

    /**
     * 冗余：登记人姓名
     */
    private String registerPersonName;

    /**
     * 申请状态（待审批/已审批/已驳回/已完成/逾期未归还）
     */
    private String status;

    /**
     * 审批人ID（关联user.id）
     */
    private Long approverId;

    /**
     * 冗余：审批人姓名
     */
    private String approverName;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 审批意见
     */
    private String approvalOpinion;

    /**
     * 实际归还时间
     */
    private LocalDateTime actualReturnTime;

    /**
     * 归还经办人ID（关联user.id）
     */
    private Long returnHandlerId;

    /**
     * 冗余：归还经办人姓名
     */
    private String returnHandlerName;

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

    // 新增：实训室名称（来自lab_info表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是lab_borrow_apply表的字段
    private String labName;
}
