package com.example.labmanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.labmanagementsystem.entity.DeviceBorrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceBorrowMapper extends BaseMapper<DeviceBorrow> {

    // 分页查询：我的借用记录（带设备名）
    Page<DeviceBorrow> selectMyBorrowsWithDeviceName(
            Page<DeviceBorrow> page,
            @Param("borrowerId") Long borrowerId
    );

    // 分页查询：所有借用记录（带设备名）
    Page<DeviceBorrow> selectAllBorrowsWithDeviceName(Page<DeviceBorrow> page);
}
