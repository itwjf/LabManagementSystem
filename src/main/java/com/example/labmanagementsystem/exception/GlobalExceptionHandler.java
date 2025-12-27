package com.example.labmanagementsystem.exception;

import com.example.labmanagementsystem.common.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理路径参数或请求参数类型不匹配（如 ?page=abc）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        Object value = ex.getValue();
        return Result.error(400, "参数 '" + name + "' 的值 '" + value + "' 类型不正确，请传入有效数据");
    }

    /**
     * 处理 NumberFormatException（如 Integer.parseInt("abc")）
     */
    @ExceptionHandler(NumberFormatException.class)
    public Result<?> handleNumberFormatException(NumberFormatException ex) {
        return Result.error(400, "数字格式错误，请检查输入是否为有效数字");
    }

    /**
     * 处理 JSON 解析失败（如 POST 传了非法 JSON）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return Result.error(400, "请求体 JSON 格式不正确，请检查语法");
    }

    /**
     * 处理 Spring Validation 校验失败（如果你以后加 @Valid）
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidationException(Exception ex) {
        return Result.error(400, "请求参数校验失败：" + ex.getMessage());
    }

    /**
     * 处理数据库唯一约束冲突（如重复设备编号）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKey(DuplicateKeyException ex) {
        return Result.error(409, "数据已存在，违反唯一性约束");
    }

    /**
     * 处理所有未预期的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleGenericException(Exception ex) {
        // 开发阶段可打印堆栈，生产环境建议记录日志而非暴露细节
        ex.printStackTrace();
        return Result.error(500, "服务器内部错误，请稍后再试");
    }
}
