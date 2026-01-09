package com.example.labmanagementsystem.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    /**
     * 插入数据时自动填充
     * @param metaObject MyBatis-Plus 内部的对象包装器
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        // strictInsertFill(对象, 字段名, 值提供者, 字段类型)
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);

        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }


    /**
     * 更新数据时自动填充
     * @param metaObject MyBatis-Plus 内部的对象包装器
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        // 只更新 updateTime，createTime 不变
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }


}
