package com.freshmall.common.result;

import com.freshmall.common.enums.ResultCode;
import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private int code;
    private String msg;
    private T data;
    private long timestamp;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功 - 无数据
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    // 成功 - 有数据
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    // 成功 - 自定义消息
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    // 失败 - 使用通用错误码
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAILURE.getCode(), ResultCode.FAILURE.getMsg(), null);
    }

    // 失败 - 指定错误码
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    // 失败 - 指定错误码和自定义消息
    public static <T> Result<T> fail(ResultCode resultCode, String msg) {
        return new Result<>(resultCode.getCode(), msg, null);
    }

    // 失败 - 自定义消息
    public static <T> Result<T> fail(String msg) {
        return new Result<>(ResultCode.FAILURE.getCode(), msg, null);
    }
}