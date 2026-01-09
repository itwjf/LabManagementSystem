package com.example.labmanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.dto.AssetOutRecordCreateDTO;
import com.example.labmanagementsystem.entity.AssetOutRecord;
import com.example.labmanagementsystem.service.AssetOutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-out-records") // 所有接口路径前缀
public class AssetOutRecordController {

    private final AssetOutRecordService assetOutRecordService;

    @Autowired
    public AssetOutRecordController(AssetOutRecordService assetOutRecordService) {
        this.assetOutRecordService = assetOutRecordService;
    }

    /**
     * 查询所有资产出库记录
     */
    @GetMapping
    public Result<List<AssetOutRecord>> getAllOutRecords() {
        List<AssetOutRecord> records = assetOutRecordService.list();
        return Result.success(records);
    }

    /**
     * 根据 ID 查询单个资产出库记录
     */
    @GetMapping("/{id}")
    public Result<AssetOutRecord> getOutRecordById(@PathVariable Long id) {
        AssetOutRecord record = assetOutRecordService.getById(id);
        if (record == null) {
            return Result.error(404, "出库记录未找到");
        }
        return Result.success(record);
    }

    /**
     * 分页查询资产出库记录（支持多条件筛选）
     */
    @GetMapping("/page")
    public Result<IPage<AssetOutRecord>> getOutRecordsPage(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) Long assetId,
                                                        @RequestParam(required = false) String assetName,
                                                        @RequestParam(required = false) Long labId,
                                                        @RequestParam(required = false) String outReason,
                                                        @RequestParam(required = false) String approvalStatus,
                                                        @RequestParam(required = false) String outDateStart,
                                                        @RequestParam(required = false) String outDateEnd) {

        if (size > 100) size = 100; // 最多查100条，防止攻击

        // 构造分页对象
        Page<AssetOutRecord> pageObj = new Page<>(page, size);

        // 调用Service层的分页查询方法
        IPage<AssetOutRecord> result = assetOutRecordService.getAllOutRecords(pageObj, assetId, assetName, labId, outReason, approvalStatus, outDateStart, outDateEnd);
        return Result.success(result);
    }

    /**
     * 创建资产出库记录
     */
    @PostMapping
    public Result<AssetOutRecord> createAssetOutRecord(@RequestBody AssetOutRecordCreateDTO dto) {
        AssetOutRecord record = assetOutRecordService.createAssetOutRecord(dto);
        return Result.success(record);
    }

    /**
     * 删除资产出库记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteOutRecord(@PathVariable Long id) {
        AssetOutRecord existing = assetOutRecordService.getById(id);
        if (existing == null) {
            return Result.error(404, "要删除的出库记录不存在");
        }
        assetOutRecordService.removeById(id);
        return Result.success(null);
    }
}