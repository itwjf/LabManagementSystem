package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("asset_out_record")
public class AssetOutRecord {

    /**
     * 出库记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer recordId;

    /**
     * 实训室ID
     */
    private Long labId;

    /**
     * 资产编号
     */
    private Long assetId;

    /**
     * 出库数量
     */
    private Integer quantity;

    /**
     * 出库原因（报废/调拨/损坏处置等）
     */
    private String outReason;

    /**
     * 出库日期
     */
    private LocalDateTime outDate;

    /**
     * 出库经办人ID（关联user.id）
     */
    private Long handlerId;

    /**
     * 冗余：出库经办人姓名
     */
    private String handlerName;

    /**
     * 审批人ID（关联user.id）
     */
    private Long approverId;

    /**
     * 冗余：审批人姓名
     */
    private String approverName;

    /**
     * 审批状态（待审批/已审批/已拒绝）
     */
    private String approvalStatus;

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

    // 新增：资产名称（来自asset表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是asset_out_record表的字段
    private String assetName;

    // 新增：实训室名称（来自lab_info表）
    @TableField(exist = false)
    private String labName;
}
