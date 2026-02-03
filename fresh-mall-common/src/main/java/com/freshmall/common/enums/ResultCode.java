package com.freshmall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    FAILURE(500, "业务异常"),
    
    // 1xxx 请求错误
    PARAM_ERROR(1001, "参数校验失败"),
    UN_AUTHORIZED(1002, "未登录或Token过期"),
    FORBIDDEN(1003, "没有权限"),
    
    // 2xxx 业务错误 (根据后续业务添加)
    USER_NOT_EXIST(2001, "用户不存在"),
    PRODUCT_OFF_SHELF(2002, "商品已下架");

    final int code;
    final String msg;
}