package com.example.labmanagementsystem.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data // Lombok 自动生成 getter/setter/toString 等
@TableName("device")
public class Device {

    @ExcelProperty("设备ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ExcelProperty("设备名称")
    private String name;
    @ExcelProperty("设备类别")
    private String category;
    @ExcelProperty("状态")
    private String status;
    @ExcelProperty("存放位置")
    private String location;
    @ExcelProperty("购入日期")
    @ColumnWidth(15) //定义excel表格列宽
    private LocalDate purchaseDate;

    /**
     * 创建时间
     * FieldFill.INSERT 表示：只有在 INSERT（插入）时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * FieldFill.INSERT_UPDATE 表示：INSERT 和 UPDATE 都会自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;


}
