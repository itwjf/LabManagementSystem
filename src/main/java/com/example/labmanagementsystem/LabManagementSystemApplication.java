package com.example.labmanagementsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@MapperScan("com.example.labmanagementsystem.mapper")
public class LabManagementSystemApplication {

	public static void main(String[] args) {SpringApplication.run(LabManagementSystemApplication.class, args);

		System.out.println(new BCryptPasswordEncoder().encode("123456"));
	
	}

}
