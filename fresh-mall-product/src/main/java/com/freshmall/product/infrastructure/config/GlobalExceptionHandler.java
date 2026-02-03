package com.freshmall.product.infrastructure.config;

import com.freshmall.common.exception.BusinessException;
import com.freshmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 捕获参数校验异常 (Spring Validation)
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        // 获取第一条错误信息
        String message = e.getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.fail(message);
    }

    /**
     * 捕获所有未知的系统异常 (兜底)
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统未知异常", e);
        return Result.fail("系统繁忙，请稍后再试");
    }
}