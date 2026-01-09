package com.example.labmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AssetOutRecordCreateDTO {

    @NotNull(message = "实训室ID不能为空")
    private Long labId;

    @NotNull(message = "资产编号不能为空")
    private Long assetId;

    @NotNull(message = "出库数量不能为空")
    @Positive(message = "出库数量必须大于0")
    private Integer quantity;

    @NotNull(message = "出库原因不能为空")
    private String outReason;

    @NotNull(message = "出库经办人ID不能为空")
    private Long handlerId;

    private Long approverId;

    private String remark;
}
