package com.example.labmanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.entity.AssetType;
import com.example.labmanagementsystem.service.AssetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-types") // 所有接口路径前缀
public class AssetTypeController {

    private final AssetTypeService assetTypeService;

    @Autowired
    public AssetTypeController(AssetTypeService assetTypeService) {
        this.assetTypeService = assetTypeService;
    }

    /**
     * 查询所有资产类型
     */
    @GetMapping
    public Result<List<AssetType>> getAllAssetTypes() {
        List<AssetType> assetTypes = assetTypeService.list();
        return Result.success(assetTypes);
    }

    /**
     * 根据 ID 查询单个资产类型
     */
    @GetMapping("/{id}")
    public Result<AssetType> getAssetTypeById(@PathVariable Integer id) {
        AssetType assetType = assetTypeService.getById(id);
        if (assetType == null) {
            return Result.error(404, "资产类型未找到");
        }
        return Result.success(assetType);
    }

    /**
     * 分页查询资产类型（支持按名称筛选）
     */
    @GetMapping("/page")
    public Result<IPage<AssetType>> getAssetTypesPage(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String typeName) {

        if (size > 100) size = 100; // 最多查100条，防止攻击

        // 构造分页对象
        Page<AssetType> pageObj = new Page<>(page, size);

        // 调用Service层的分页查询方法
        IPage<AssetType> result = assetTypeService.getAllAssetTypes(pageObj, typeName);
        return Result.success(result);
    }

    /**
     * 新增资产类型
     */
    @PostMapping
    public Result<AssetType> createAssetType(@RequestBody AssetType assetType) {
        assetTypeService.save(assetType); // 自动填充 createdAt/updatedAt
        return Result.success(assetType);
    }

    /**
     * 更新资产类型
     */
    @PutMapping("/{id}")
    public Result<AssetType> updateAssetType(@PathVariable Integer id, @RequestBody AssetType assetType) {
        AssetType existing = assetTypeService.getById(id);
        if (existing == null) {
            return Result.error(404, "要更新的资产类型不存在");
        }

        assetType.setTypeId(id);
        assetTypeService.updateById(assetType);
        return Result.success(assetType);
    }

    /**
     * 删除资产类型
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAssetType(@PathVariable Integer id) {
        AssetType existing = assetTypeService.getById(id);
        if (existing == null) {
            return Result.error(404, "要删除的资产类型不存在");
        }
        assetTypeService.removeById(id);
        return Result.success(null);
    }
}