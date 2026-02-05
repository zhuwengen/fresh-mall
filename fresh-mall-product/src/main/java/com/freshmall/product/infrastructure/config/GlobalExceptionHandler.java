package com.freshmall.product.infrastructure.config;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器：将异常转换为标准 Result JSON
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常 (我们自己手动抛出的)
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常 [code={}]: {}", e.getCode(), e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 捕获参数校验异常 - @Valid 注解触发 (用于 @RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return new Result<>(ResultCode.PARAM_ERROR.getCode(), message, null);
    }

    /**
     * 捕获参数校验异常 - 用于表单提交和 @ModelAttribute
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return new Result<>(ResultCode.PARAM_ERROR.getCode(), message, null);
    }

    /**
     * 捕获所有未知的系统异常 (兜底)
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统未知异常: {}", e.getMessage(), e);
        return Result.fail("系统繁忙，请稍后再试");
    }
}