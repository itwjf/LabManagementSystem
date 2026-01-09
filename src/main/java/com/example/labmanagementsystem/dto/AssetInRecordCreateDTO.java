package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetInRecordCreateDTO {

    @NotNull(message = "实训室ID不能为空")
    private Long labId;

    @NotNull(message = "资产编号不能为空")
    private Long assetId;

    @NotNull(message = "入库数量不能为空")
    @Positive(message = "入库数量必须大于0")
    private Integer quantity;

    @NotNull(message = "入库单价不能为空")
    @Positive(message = "入库单价必须大于0")
    private BigDecimal unitPrice;

    private LocalDate purchaseDate;

    private Long purchaserId;

    @NotNull(message = "入库经办人ID不能为空")
    private Long handlerId;

    private String remark;
}
