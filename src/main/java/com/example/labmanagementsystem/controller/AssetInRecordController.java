package com.example.labmanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.dto.AssetInRecordCreateDTO;
import com.example.labmanagementsystem.entity.AssetInRecord;
import com.example.labmanagementsystem.service.AssetInRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-in-records") // 所有接口路径前缀
public class AssetInRecordController {

    private final AssetInRecordService assetInRecordService;

    @Autowired
    public AssetInRecordController(AssetInRecordService assetInRecordService) {
        this.assetInRecordService = assetInRecordService;
    }

    /**
     * 查询所有资产入库记录
     */
    @GetMapping
    public Result<List<AssetInRecord>> getAllInRecords() {
        List<AssetInRecord> records = assetInRecordService.list();
        return Result.success(records);
    }

    /**
     * 根据 ID 查询单个资产入库记录
     */
    @GetMapping("/{id}")
    public Result<AssetInRecord> getInRecordById(@PathVariable Long id) {
        AssetInRecord record = assetInRecordService.getById(id);
        if (record == null) {
            return Result.error(404, "入库记录未找到");
        }
        return Result.success(record);
    }

    /**
     * 分页查询资产入库记录（支持多条件筛选）
     */
    @GetMapping("/page")
    public Result<IPage<AssetInRecord>> getInRecordsPage(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) Long assetId,
                                                       @RequestParam(required = false) String assetName,
                                                       @RequestParam(required = false) Long labId,
                                                       @RequestParam(required = false) String purchaserName,
                                                       @RequestParam(required = false) String inDateStart,
                                                       @RequestParam(required = false) String inDateEnd) {

        if (size > 100) size = 100; // 最多查100条，防止攻击

        // 构造分页对象
        Page<AssetInRecord> pageObj = new Page<>(page, size);

        // 调用Service层的分页查询方法
        IPage<AssetInRecord> result = assetInRecordService.getAllInRecords(pageObj, assetId, assetName, labId, purchaserName, inDateStart, inDateEnd);
        return Result.success(result);
    }

    /**
     * 创建资产入库记录
     */
    @PostMapping
    public Result<AssetInRecord> createAssetInRecord(@RequestBody AssetInRecordCreateDTO dto) {
        AssetInRecord record = assetInRecordService.createAssetInRecord(dto);
        return Result.success(record);
    }

    /**
     * 删除资产入库记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteInRecord(@PathVariable Long id) {
        AssetInRecord existing = assetInRecordService.getById(id);
        if (existing == null) {
            return Result.error(404, "要删除的入库记录不存在");
        }
        assetInRecordService.removeById(id);
        return Result.success(null);
    }
}