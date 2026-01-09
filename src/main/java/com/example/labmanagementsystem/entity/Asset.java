package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("asset")
public class Asset {

    /**
     * 资产编号
     */
    @TableId(type = IdType.AUTO)
    private Long assetId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 资产类型ID
     */
    private Integer typeId;

    /**
     * 规格型号
     */
    private String specModel;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总数量
     */
    private Integer totalQuantity;

    /**
     * 可用数量
     */
    private Integer availableQuantity;

    /**
     * 总价值
     */
    private BigDecimal totalValue;

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
     * 供应商
     */
    private String supplier;

    /**
     * 资产状态（正常使用/出库待处置/已报废等）
     */
    private String status;

    /**
     * 所属实训室ID
     */
    private Long labId;

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

    // 新增：资产类型名称（来自asset_type表）
    @TableField(exist = false) // 告诉MyBatis Plus：这不是asset表的字段
    private String typeName;

    // 新增：所属实训室名称（来自lab_info表）
    @TableField(exist = false)
    private String labName;
}
