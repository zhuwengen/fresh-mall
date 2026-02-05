package com.freshmall.common.exception;

import com.freshmall.common.enums.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final int code;

    public BusinessException(String msg) {
        super(msg);
        this.code = ResultCode.FAILURE.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String msg) {
        super(msg);
        this.code = resultCode.getCode();
    }
}