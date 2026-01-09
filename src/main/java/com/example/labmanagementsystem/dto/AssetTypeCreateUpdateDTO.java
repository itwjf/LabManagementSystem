package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetTypeCreateUpdateDTO {

    // 资产类型ID，更新时使用，创建时为空
    private Integer typeId;

    @NotNull(message = "资产类型名称不能为空")
    private String typeName;

    private String description;
}
