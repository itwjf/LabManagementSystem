package com.example.labmanagementsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.common.Result;
import com.example.labmanagementsystem.entity.LabInfo;
import com.example.labmanagementsystem.service.LabInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab-info") // 所有接口路径前缀
public class LabInfoController {

    private final LabInfoService labInfoService;

    @Autowired
    public LabInfoController(LabInfoService labInfoService) {
        this.labInfoService = labInfoService;
    }

    /**
     * 查询所有实训室信息
     */
    @GetMapping
    public Result<List<LabInfo>> getAllLabs() {
        List<LabInfo> labs = labInfoService.list();
        return Result.success(labs);
    }

    /**
     * 根据 ID 查询单个实训室信息
     */
    @GetMapping("/{id}")
    public Result<LabInfo> getLabById(@PathVariable Long id) {
        LabInfo lab = labInfoService.getById(id);
        if (lab == null) {
            return Result.error(404, "实训室未找到");
        }
        return Result.success(lab);
    }

    /**
     * 分页查询实训室信息（支持按名称、位置、状态筛选）
     */
    @GetMapping("/page")
    public Result<IPage<LabInfo>> getLabsPage(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String labName,
                                            @RequestParam(required = false) String location,
                                            @RequestParam(required = false) String status) {

        if (size > 100) size = 100; // 最多查100条，防止攻击

        // 构造分页对象
        Page<LabInfo> pageObj = new Page<>(page, size);

        // 调用Service层的分页查询方法
        IPage<LabInfo> result = labInfoService.getAllLabs(pageObj, labName, location, status);
        return Result.success(result);
    }

    /**
     * 新增实训室信息
     */
    @PostMapping
    public Result<LabInfo> createLab(@RequestBody LabInfo labInfo) {
        labInfoService.save(labInfo); // 自动填充 createdAt/updatedAt
        return Result.success(labInfo);
    }

    /**
     * 更新实训室信息
     */
    @PutMapping("/{id}")
    public Result<LabInfo> updateLab(@PathVariable Long id, @RequestBody LabInfo labInfo) {
        LabInfo existing = labInfoService.getById(id);
        if (existing == null) {
            return Result.error(404, "要更新的实训室不存在");
        }

        labInfo.setLabId(id);
        labInfoService.updateById(labInfo);
        return Result.success(labInfo);
    }

    /**
     * 删除实训室信息
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteLab(@PathVariable Long id) {
        LabInfo existing = labInfoService.getById(id);
        if (existing == null) {
            return Result.error(404, "要删除的实训室不存在");
        }
        labInfoService.removeById(id);
        return Result.success(null);
    }
}