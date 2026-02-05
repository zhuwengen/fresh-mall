package com.freshmall.product.domain.valueobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 规格值对象
 * 封装 SKU 规格数据，提供验证和序列化功能
 */
@Getter
@EqualsAndHashCode
public class Specifications implements Serializable {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private final Map<String, Object> specs;
    
    private Specifications(Map<String, Object> specs) {
        if (specs == null || specs.isEmpty()) {
            throw new IllegalArgumentException("规格不能为空");
        }
        this.specs = new HashMap<>(specs);
    }
    
    public static Specifications of(Map<String, Object> specs) {
        return new Specifications(specs);
    }
    
    public static Specifications fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON 字符串不能为空");
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> specs = OBJECT_MAPPER.readValue(json, Map.class);
            return new Specifications(specs);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("无效的 JSON 格式: " + e.getMessage(), e);
        }
    }
    
    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(specs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("规格序列化失败: " + e.getMessage(), e);
        }
    }
    
    public Object get(String key) {
        return specs.get(key);
    }
    
    public boolean containsKey(String key) {
        return specs.containsKey(key);
    }
    
    public int size() {
        return specs.size();
    }
    
    public Map<String, Object> asMap() {
        return new HashMap<>(specs);
    }
    
    @Override
    public String toString() {
        return toJson();
    }
}
