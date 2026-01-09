package com.example.labmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("asset_type")
public class AssetType {

    /**
     * 资产类型ID
     */
    @TableId(type = IdType.AUTO)
    private Integer typeId;

    /**
     * 资产类型名称（设备类/家具类/耗材类等）
     */
    private String typeName;

    /**
     * 类型描述
     */
    private String description;

    /**
     * 创建时间
     * FieldFill.INSERT 表示：只有在 INSERT（插入）时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
