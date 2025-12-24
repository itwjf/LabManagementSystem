package com.example.labmanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
public class DatabaseTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() throws Exception{

        System.out.println("✅ 当前数据源 URL: " + dataSource.getConnection().getMetaData().getURL());
        try (Connection conn = dataSource.getConnection()) {

            System.out.println("数据库连接成功！");
            System.out.println("URL: " + conn.getMetaData().getURL());
        }
    }
}
