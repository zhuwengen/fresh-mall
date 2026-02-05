package com.freshmall.product.domain.service;

import com.freshmall.product.domain.aggregate.Attribute;
import com.freshmall.product.domain.aggregate.CategoryAttribute;

import java.util.List;
import java.util.Map;

/**
 * Attribute 领域服务接口
 * 管理属性定义、类目-属性关联和属性值验证
 */
public interface AttributeService {

    /**
     * 创建属性
     * 
     * @param name 属性名称（唯一）
     * @param attributeType 属性类型（TEXT、NUMBER、ENUM）
     * @param valueRange 值范围约束（JSON 格式）
     * @return 创建的属性 ID
     */
    Long createAttribute(String name, String attributeType, String valueRange);

    /**
     * 关联属性到类目
     * 创建类目-属性关联关系
     * 
     * @param categoryId 类目 ID
     * @param attributeId 属性 ID
     * @param required 是否必需
     * @param sortOrder 排序顺序
     * @return 创建的关联 ID
     */
    Long associateAttributeToCategory(Long categoryId, Long attributeId, Boolean required, Integer sortOrder);

    /**
     * 查询类目的属性列表
     * 按 sort_order 升序排序
     * 
     * @param categoryId 类目 ID
     * @return 属性列表（包含关联信息）
     */
    List<Attribute> getCategoryAttributes(Long categoryId);

    /**
     * 查询类目的必需属性列表
     * 
     * @param categoryId 类目 ID
     * @return 必需属性列表
     */
    List<Attribute> getRequiredCategoryAttributes(Long categoryId);

    /**
     * 验证 SKU 规格值是否符合类目属性约束
     * 检查必需属性是否存在，以及属性值是否符合类型和范围约束
     * 
     * @param categoryId 类目 ID
     * @param specifications SKU 规格（属性名 -> 属性值）
     */
    void validateSpecifications(Long categoryId, Map<String, Object> specifications);

    /**
     * 根据 ID 查询属性
     * 
     * @param attributeId 属性 ID
     * @return 属性实体
     */
    Attribute getAttributeById(Long attributeId);

    /**
     * 根据名称查询属性
     * 
     * @param name 属性名称
     * @return 属性实体
     */
    Attribute getAttributeByName(String name);
}
