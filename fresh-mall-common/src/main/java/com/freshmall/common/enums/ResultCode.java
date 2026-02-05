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
    PRODUCT_OFF_SHELF(2002, "商品已下架"),
    
    // 3xxx 商品中心错误码
    // SPU 相关 (30xx)
    SPU_NOT_FOUND(3001, "SPU 不存在"),
    SPU_ALREADY_DELETED(3002, "SPU 已被删除"),
    SPU_INVALID_STATUS_TRANSITION(3003, "无效的状态转换"),
    SPU_NO_ENABLED_SKU(3004, "没有启用的 SKU，无法发布"),
    SPU_CATEGORY_NOT_LEAF(3005, "类目不是叶子类目"),
    SPU_NAME_INVALID(3006, "SPU 名称不合法"),
    
    // SKU 相关 (31xx)
    SKU_NOT_FOUND(3101, "SKU 不存在"),
    SKU_CODE_DUPLICATE(3102, "SKU 编码重复"),
    SKU_INVALID_SPECIFICATIONS(3103, "SKU 规格不合法"),
    SKU_SPU_NOT_FOUND(3104, "关联的 SPU 不存在"),
    SKU_SPECIFICATIONS_EMPTY(3105, "SKU 规格不能为空"),
    
    // 库存相关 (32xx)
    STOCK_NOT_FOUND(3201, "库存记录不存在"),
    STOCK_INSUFFICIENT(3202, "库存不足"),
    STOCK_OPTIMISTIC_LOCK_FAILED(3203, "库存更新冲突，请重试"),
    STOCK_INVARIANT_VIOLATED(3204, "库存数据不一致"),
    
    // 价格相关 (33xx)
    PRICE_NOT_FOUND(3301, "未找到有效价格"),
    PRICE_INVALID_AMOUNT(3302, "价格金额必须大于零"),
    PRICE_INVALID_TIME_RANGE(3303, "价格时间范围不合法"),
    PRICE_SKU_NOT_FOUND(3304, "价格关联的 SKU 不存在"),
    
    // 类目相关 (34xx)
    CATEGORY_NOT_FOUND(3401, "类目不存在"),
    CATEGORY_CODE_DUPLICATE(3402, "类目编码重复"),
    CATEGORY_HAS_CHILDREN(3403, "类目有子类目，不能分配 SPU"),
    CATEGORY_HAS_SPU(3404, "类目有关联 SPU，不能删除"),
    CATEGORY_PARENT_NOT_FOUND(3405, "父类目不存在"),
    
    // 属性相关 (35xx)
    ATTRIBUTE_NOT_FOUND(3501, "属性不存在"),
    ATTRIBUTE_NAME_DUPLICATE(3502, "属性名称重复"),
    ATTRIBUTE_VALUE_INVALID(3503, "属性值不符合约束"),
    ATTRIBUTE_TYPE_INVALID(3504, "属性类型不合法"),
    ATTRIBUTE_VALUE_RANGE_INVALID(3505, "属性值范围格式不合法"),
    ATTRIBUTE_REQUIRED_MISSING(3506, "缺少必需属性"),
    
    // 类目-属性关系相关 (36xx)
    CATEGORY_ATTRIBUTE_NOT_FOUND(3601, "类目-属性关联不存在"),
    CATEGORY_ATTRIBUTE_DUPLICATE(3602, "类目-属性关联已存在");

    final int code;
    final String msg;
}