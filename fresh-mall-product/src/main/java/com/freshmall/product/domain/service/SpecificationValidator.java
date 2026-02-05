package com.freshmall.product.domain.service;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.aggregate.Attribute;
import com.freshmall.product.domain.aggregate.CategoryAttribute;
import com.freshmall.product.domain.repository.AttributeRepository;
import com.freshmall.product.domain.repository.CategoryAttributeRepository;
import com.freshmall.product.domain.valueobject.Specifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规格验证服务
 * 验证 SKU 规格是否符合类目属性定义
 */
@Service
@RequiredArgsConstructor
public class SpecificationValidator {

    private final CategoryAttributeRepository categoryAttributeRepository;
    private final AttributeRepository attributeRepository;

    /**
     * 验证规格是否符合类目属性定义
     * 
     * @param categoryId 类目 ID
     * @param specifications 规格值对象
     * @throws BusinessException 如果规格不符合要求
     */
    public void validate(Long categoryId, Specifications specifications) {
        if (categoryId == null) {
            throw new BusinessException("类目 ID 不能为空");
        }
        
        if (specifications == null) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }

        // 1. 获取类目关联的属性定义
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findByCategoryId(categoryId);
        
        if (categoryAttributes.isEmpty()) {
            // 如果类目没有定义属性，则不进行验证
            return;
        }

        // 2. 获取所有属性定义
        List<Long> attributeIds = categoryAttributes.stream()
                .map(CategoryAttribute::getAttributeId)
                .collect(Collectors.toList());
        
        List<Attribute> attributes = attributeRepository.findByIds(attributeIds);
        Map<Long, Attribute> attributeMap = attributes.stream()
                .collect(Collectors.toMap(Attribute::getId, attr -> attr));

        // 3. 检查必需属性
        for (CategoryAttribute categoryAttribute : categoryAttributes) {
            if (categoryAttribute.getRequired()) {
                Attribute attribute = attributeMap.get(categoryAttribute.getAttributeId());
                if (attribute == null) {
                    continue;
                }
                
                if (!specifications.containsKey(attribute.getName())) {
                    throw new BusinessException(
                            ResultCode.ATTRIBUTE_REQUIRED_MISSING,
                            "缺少必需属性: " + attribute.getName()
                    );
                }
            }
        }

        // 4. 验证属性值类型和范围
        Map<String, Object> specsMap = specifications.asMap();
        for (Map.Entry<String, Object> entry : specsMap.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();

            // 查找属性定义
            Attribute attribute = attributes.stream()
                    .filter(attr -> attr.getName().equals(attrName))
                    .findFirst()
                    .orElse(null);

            if (attribute != null) {
                // 验证属性值
                try {
                    attribute.validateValue(attrValue);
                } catch (BusinessException e) {
                    throw new BusinessException(
                            ResultCode.ATTRIBUTE_VALUE_INVALID,
                            "属性 '" + attrName + "' 值不合法: " + e.getMessage()
                    );
                }
            }
            // 如果属性不在类目定义中，允许通过（灵活性）
        }
    }

    /**
     * 验证规格是否符合类目属性定义（使用 Map）
     * 
     * @param categoryId 类目 ID
     * @param specificationsMap 规格 Map
     * @throws BusinessException 如果规格不符合要求
     */
    public void validate(Long categoryId, Map<String, Object> specificationsMap) {
        if (specificationsMap == null || specificationsMap.isEmpty()) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }
        
        Specifications specifications = Specifications.of(specificationsMap);
        validate(categoryId, specifications);
    }
}
