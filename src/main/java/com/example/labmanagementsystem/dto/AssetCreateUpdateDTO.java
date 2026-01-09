package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetCreateUpdateDTO {

    // 资产ID，更新时使用，创建时为空
    private Long assetId;

    @NotNull(message = "资产名称不能为空")
    private String assetName;

    @NotNull(message = "资产类型ID不能为空")
    private Integer typeId;

    private String specModel;

    @NotNull(message = "单位不能为空")
    private String unit;

    @NotNull(message = "单价不能为空")
    @Positive(message = "单价必须大于0")
    private BigDecimal unitPrice; 

    @NotNull(message = "总数量不能为空")
    @Positive(message = "总数量必须大于0")
    private Integer totalQuantity;

    @NotNull(message = "可用数量不能为空")
    private Integer availableQuantity;

    private LocalDate purchaseDate;

    private Long purchaserId;

    private String supplier;

    @NotNull(message = "资产状态不能为空")
    private String status;

    @NotNull(message = "所属实训室ID不能为空")
    private Long labId;
}
