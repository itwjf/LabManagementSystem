package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("asset_in_record")
public class AssetInRecord {

    /**
     * 入库记录ID
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
     * 入库数量
     */
    private Integer quantity;

    /**
     * 入库单价
     */
    private BigDecimal unitPrice;

    /**
     * 入库总金额
     */
    private BigDecimal totalAmount;

    /**
     * 购置日期
     */
    private LocalDate purchaseDate;

    /**
     * 采购人ID（关联user.id）
     */
    private Long purchaserId;

    /**
     * 冗余：采购人姓名
     */
    private String purchaserName;

    /**
     * 入库经办人ID（关联user.id）
     */
    private Long handlerId;

    /**
     * 冗余：入库经办人姓名
     */
    private String handlerName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库日期
     */
    private LocalDateTime inDate;

    /**
     * 创建时间
     * FieldFill.INSERT 表示：只有在 INSERT（插入）时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 新增：资产名称（来自asset表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是asset_in_record表的字段
    private String assetName;

    // 新增：实训室名称（来自lab_info表）
    @TableField(exist = false)
    private String labName;
}
