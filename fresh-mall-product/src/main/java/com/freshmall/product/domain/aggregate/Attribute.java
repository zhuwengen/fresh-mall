package com.freshmall.product.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.model.AttributeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Attribute 聚合根
 * 管理属性定义、类型和值约束
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_attribute")
public class Attribute extends BaseEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 属性名称（唯一）
     */
    private String name;

    /**
     * 属性类型（TEXT、NUMBER、ENUM）
     */
    private AttributeType attributeType;

    /**
     * 值范围约束（JSON 格式）
     * - ENUM: ["value1", "value2", ...]
     * - NUMBER: {"min": 0, "max": 100}
     * - TEXT: null 或空
     */
    private String valueRange;

    /**
     * 验证属性名称
     * 
     * @param name 属性名称
     * @throws BusinessException 如果名称为空
     */
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("属性名称不能为空");
        }
    }

    /**
     * 验证属性类型
     * 
     * @param attributeType 属性类型
     * @throws BusinessException 如果类型为空
     */
    public static void validateAttributeType(AttributeType attributeType) {
        if (attributeType == null) {
            throw new BusinessException("属性类型不能为空");
        }
    }

    /**
     * 验证 ENUM 类型的值范围格式
     * 
     * @param valueRange 值范围 JSON
     * @throws BusinessException 如果格式不合法
     */
    public static void validateEnumValueRange(String valueRange) {
        if (valueRange == null || valueRange.trim().isEmpty()) {
            throw new BusinessException("ENUM 类型必须提供值范围");
        }
        
        try {
            List<String> values = OBJECT_MAPPER.readValue(valueRange, new TypeReference<List<String>>() {});
            if (values == null || values.isEmpty()) {
                throw new BusinessException("ENUM 类型值范围不能为空");
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException("ENUM 类型值范围必须是有效的 JSON 数组");
        }
    }

    /**
     * 验证 NUMBER 类型的值范围格式
     * 
     * @param valueRange 值范围 JSON
     * @throws BusinessException 如果格式不合法
     */
    public static void validateNumberValueRange(String valueRange) {
        if (valueRange == null || valueRange.trim().isEmpty()) {
            throw new BusinessException("NUMBER 类型必须提供值范围");
        }
        
        try {
            Map<String, Object> range = OBJECT_MAPPER.readValue(valueRange, new TypeReference<Map<String, Object>>() {});
            if (!range.containsKey("min") || !range.containsKey("max")) {
                throw new BusinessException("NUMBER 类型值范围必须包含 min 和 max");
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException("NUMBER 类型值范围必须是有效的 JSON 对象");
        }
    }

    /**
     * 验证属性值是否符合约束
     * 
     * @param value 属性值
     * @throws BusinessException 如果值不符合约束
     */
    public void validateValue(Object value) {
        if (value == null) {
            throw new BusinessException("属性值不能为空");
        }

        switch (this.attributeType) {
            case TEXT:
                validateTextValue(value);
                break;
            case NUMBER:
                validateNumberValue(value);
                break;
            case ENUM:
                validateEnumValue(value);
                break;
            default:
                throw new BusinessException("未知的属性类型");
        }
    }

    /**
     * 验证文本类型值
     */
    private void validateTextValue(Object value) {
        if (!(value instanceof String)) {
            throw new BusinessException("TEXT 类型属性值必须是字符串");
        }
    }

    /**
     * 验证数字类型值
     */
    private void validateNumberValue(Object value) {
        BigDecimal numValue;
        
        if (value instanceof Number) {
            numValue = new BigDecimal(value.toString());
        } else if (value instanceof String) {
            try {
                numValue = new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                throw new BusinessException("NUMBER 类型属性值必须是数字");
            }
        } else {
            throw new BusinessException("NUMBER 类型属性值必须是数字");
        }

        if (this.valueRange != null && !this.valueRange.trim().isEmpty()) {
            try {
                Map<String, Object> range = OBJECT_MAPPER.readValue(this.valueRange, new TypeReference<Map<String, Object>>() {});
                BigDecimal min = new BigDecimal(range.get("min").toString());
                BigDecimal max = new BigDecimal(range.get("max").toString());
                
                if (numValue.compareTo(min) < 0 || numValue.compareTo(max) > 0) {
                    throw new BusinessException(String.format("数值超出范围 [%s, %s]", min, max));
                }
            } catch (JsonProcessingException e) {
                throw new BusinessException("值范围格式错误");
            }
        }
    }

    /**
     * 验证枚举类型值
     */
    private void validateEnumValue(Object value) {
        String strValue = value.toString();
        
        if (this.valueRange != null && !this.valueRange.trim().isEmpty()) {
            try {
                List<String> allowedValues = OBJECT_MAPPER.readValue(this.valueRange, new TypeReference<List<String>>() {});
                if (!allowedValues.contains(strValue)) {
                    throw new BusinessException(String.format("枚举值不合法，允许的值: %s", allowedValues));
                }
            } catch (JsonProcessingException e) {
                throw new BusinessException("值范围格式错误");
            }
        }
    }
}
