package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LabInfoCreateUpdateDTO {

    // 实训室ID，更新时使用，创建时为空
    private Long labId;

    @NotNull(message = "实训室名称不能为空")
    private String labName;

    @NotNull(message = "地理位置不能为空")
    private String location;

    @NotNull(message = "容纳人数不能为空")
    @Positive(message = "容纳人数必须大于0")
    private Integer capacity;

    @NotNull(message = "管理员ID不能为空")
    private Long managerId;

    private String contact;

    // 可选，默认"正常使用"
    private String status;
}
