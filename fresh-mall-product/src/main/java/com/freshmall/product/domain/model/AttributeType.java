package com.freshmall.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 属性类型枚举
 */
@Getter
@AllArgsConstructor
public enum AttributeType {
    
    /**
     * 文本类型 - 自由文本
     */
    TEXT("文本"),
    
    /**
     * 数字类型 - 数值范围
     */
    NUMBER("数字"),
    
    /**
     * 枚举类型 - 预定义值列表
     */
    ENUM("枚举");
    
    private final String description;
}
