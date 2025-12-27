package com.example.labmanagementsystem.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Result<T> {

    private int code; //状态码
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.timestamp = LocalDateTime.now();
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        result.timestamp = LocalDateTime.now();
        return result;
    }
}
