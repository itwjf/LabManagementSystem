package com.example.labmanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.entity.Asset;
import com.example.labmanagementsystem.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets") // 所有接口路径前缀
public class AssetController {

    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * 查询所有资产
     */
    @GetMapping
    public Result<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.list();
        return Result.success(assets);
    }

    /**
     * 根据 ID 查询单个资产
     */
    @GetMapping("/{id}")
    public Result<Asset> getAssetById(@PathVariable Long id) {
        Asset asset = assetService.getById(id);
        if (asset == null) {
            return Result.error(404, "资产未找到");
        }
        return Result.success(asset);
    }

    /**
     * 分页查询资产（支持按名称、类型、实训室、状态筛选）
     */
    @GetMapping("/page")
    public Result<IPage<Asset>> getAssetsPage(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String assetName,
                                             @RequestParam(required = false) Integer typeId,
                                             @RequestParam(required = false) Long labId,
                                             @RequestParam(required = false) String status) {

        if (size > 100) size = 100; // 最多查100条，防止攻击

        // 构造分页对象
        Page<Asset> pageObj = new Page<>(page, size);

        // 调用Service层的分页查询方法
        IPage<Asset> result = assetService.selectAllAssets(pageObj, assetName, typeId, labId, status);
        return Result.success(result);
    }

    /**
     * 新增资产
     */
    @PostMapping
    public Result<Asset> createAsset(@RequestBody Asset asset) {
        assetService.save(asset); // 自动填充 createdAt/updatedAt
        return Result.success(asset);
    }

    /**
     * 更新资产
     */
    @PutMapping("/{id}")
    public Result<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        Asset existing = assetService.getById(id);
        if (existing == null) {
            return Result.error(404, "要更新的资产不存在");
        }

        asset.setAssetId(id);
        assetService.updateById(asset);
        return Result.success(asset);
    }

    /**
     * 删除资产
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(@PathVariable Long id) {
        Asset existing = assetService.getById(id);
        if (existing == null) {
            return Result.error(404, "要删除的资产不存在");
        }
        assetService.removeById(id);
        return Result.success(null);
    }
}