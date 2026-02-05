package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.aggregate.Attribute;
import com.freshmall.product.domain.aggregate.CategoryAttribute;
import com.freshmall.product.domain.model.AttributeType;
import com.freshmall.product.domain.repository.AttributeRepository;
import com.freshmall.product.domain.repository.CategoryAttributeRepository;
import com.freshmall.product.domain.repository.CategoryRepository;
import com.freshmall.product.domain.service.AttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Attribute 领域服务实现
 * 实现属性管理的核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Long createAttribute(String name, String attributeType, String valueRange) {
        log.info("创建属性: name={}, attributeType={}, valueRange={}", name, attributeType, valueRange);

        // 验证属性名称
        Attribute.validateName(name);

        // 检查属性名称是否已存在
        if (attributeRepository.existsByName(name)) {
            throw new BusinessException(ResultCode.ATTRIBUTE_NAME_DUPLICATE);
        }

        // 解析并验证属性类型
        AttributeType type;
        try {
            type = AttributeType.valueOf(attributeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的属性类型，必须是 TEXT、NUMBER 或 ENUM");
        }
        Attribute.validateAttributeType(type);

        // 根据属性类型验证值范围格式
        if (type == AttributeType.ENUM) {
            Attribute.validateEnumValueRange(valueRange);
        } else if (type == AttributeType.NUMBER) {
            Attribute.validateNumberValueRange(valueRange);
        }

        // 创建属性实体
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setAttributeType(type);
        attribute.setValueRange(valueRange);

        // 保存属性
        Attribute savedAttribute = attributeRepository.save(attribute);

        log.info("属性创建成功: id={}, name={}", savedAttribute.getId(), savedAttribute.getName());
        return savedAttribute.getId();
    }

    @Override
    @Transactional
    public Long associateAttributeToCategory(Long categoryId, Long attributeId, Boolean required, Integer sortOrder) {
        log.info("关联属性到类目: categoryId={}, attributeId={}, required={}, sortOrder={}", 
                 categoryId, attributeId, required, sortOrder);

        // 验证类目 ID 和属性 ID
        CategoryAttribute.validateCategoryId(categoryId);
        CategoryAttribute.validateAttributeId(attributeId);

        // 验证类目是否存在
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 验证属性是否存在
        if (!attributeRepository.findById(attributeId).isPresent()) {
            throw new BusinessException(ResultCode.ATTRIBUTE_NOT_FOUND);
        }

        // 检查类目-属性关联是否已存在
        if (categoryAttributeRepository.existsByCategoryIdAndAttributeId(categoryId, attributeId)) {
            throw new BusinessException("类目-属性关联已存在");
        }

        // 验证排序顺序
        if (sortOrder != null) {
            CategoryAttribute.validateSortOrder(sortOrder);
        }

        // 创建类目-属性关联实体
        CategoryAttribute categoryAttribute = new CategoryAttribute();
        categoryAttribute.setCategoryId(categoryId);
        categoryAttribute.setAttributeId(attributeId);
        categoryAttribute.setRequired(required);
        categoryAttribute.setSortOrder(sortOrder);
        
        // 设置默认值
        categoryAttribute.setDefaults();

        // 保存关联
        CategoryAttribute savedCategoryAttribute = categoryAttributeRepository.save(categoryAttribute);

        log.info("类目-属性关联创建成功: id={}", savedCategoryAttribute.getId());
        return savedCategoryAttribute.getId();
    }

    @Override
    public List<Attribute> getCategoryAttributes(Long categoryId) {
        log.info("查询类目属性: categoryId={}", categoryId);

        // 验证类目是否存在
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 查询类目-属性关联（已按 sortOrder 排序）
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findByCategoryId(categoryId);

        // 提取属性 ID 列表
        List<Long> attributeIds = categoryAttributes.stream()
                .map(CategoryAttribute::getAttributeId)
                .collect(Collectors.toList());

        if (attributeIds.isEmpty()) {
            log.info("类目没有关联属性: categoryId={}", categoryId);
            return List.of();
        }

        // 批量查询属性
        List<Attribute> attributes = attributeRepository.findByIds(attributeIds);

        // 按照 categoryAttributes 的顺序排序（保持 sortOrder 顺序）
        Map<Long, Attribute> attributeMap = attributes.stream()
                .collect(Collectors.toMap(Attribute::getId, attr -> attr));

        List<Attribute> sortedAttributes = categoryAttributes.stream()
                .map(ca -> attributeMap.get(ca.getAttributeId()))
                .filter(attr -> attr != null)
                .collect(Collectors.toList());

        log.info("类目属性查询成功: categoryId={}, attributeCount={}", categoryId, sortedAttributes.size());
        return sortedAttributes;
    }

    @Override
    public List<Attribute> getRequiredCategoryAttributes(Long categoryId) {
        log.info("查询类目必需属性: categoryId={}", categoryId);

        // 验证类目是否存在
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 查询必需属性关联
        List<CategoryAttribute> requiredCategoryAttributes = categoryAttributeRepository.findRequiredByCategoryId(categoryId);

        // 提取属性 ID 列表
        List<Long> attributeIds = requiredCategoryAttributes.stream()
                .map(CategoryAttribute::getAttributeId)
                .collect(Collectors.toList());

        if (attributeIds.isEmpty()) {
            log.info("类目没有必需属性: categoryId={}", categoryId);
            return List.of();
        }

        // 批量查询属性
        List<Attribute> attributes = attributeRepository.findByIds(attributeIds);

        log.info("类目必需属性查询成功: categoryId={}, requiredAttributeCount={}", categoryId, attributes.size());
        return attributes;
    }

    @Override
    public void validateSpecifications(Long categoryId, Map<String, Object> specifications) {
        log.info("验证 SKU 规格: categoryId={}, specifications={}", categoryId, specifications);

        // 验证类目是否存在
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 验证规格不为空
        if (specifications == null || specifications.isEmpty()) {
            throw new BusinessException(ResultCode.SKU_INVALID_SPECIFICATIONS, "SKU 规格不能为空");
        }

        // 查询类目的所有属性
        List<CategoryAttribute> categoryAttributes = categoryAttributeRepository.findByCategoryId(categoryId);
        
        // 提取属性 ID 列表
        List<Long> attributeIds = categoryAttributes.stream()
                .map(CategoryAttribute::getAttributeId)
                .collect(Collectors.toList());

        if (attributeIds.isEmpty()) {
            log.info("类目没有定义属性，跳过规格验证: categoryId={}", categoryId);
            return;
        }

        // 批量查询属性
        List<Attribute> attributes = attributeRepository.findByIds(attributeIds);
        Map<String, Attribute> attributeMap = attributes.stream()
                .collect(Collectors.toMap(Attribute::getName, attr -> attr));

        // 构建必需属性映射
        Map<Long, Boolean> requiredMap = categoryAttributes.stream()
                .collect(Collectors.toMap(CategoryAttribute::getAttributeId, CategoryAttribute::getRequired));

        // 1. 检查必需属性是否都存在
        for (Attribute attribute : attributes) {
            Boolean isRequired = requiredMap.get(attribute.getId());
            if (Boolean.TRUE.equals(isRequired)) {
                if (!specifications.containsKey(attribute.getName())) {
                    throw new BusinessException(ResultCode.SKU_INVALID_SPECIFICATIONS, 
                            String.format("缺少必需属性: %s", attribute.getName()));
                }
            }
        }

        // 2. 验证每个规格值是否符合属性约束
        for (Map.Entry<String, Object> entry : specifications.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();

            // 查找属性定义
            Attribute attribute = attributeMap.get(attrName);
            if (attribute == null) {
                // 规格中包含未定义的属性，记录警告但不阻止
                log.warn("规格包含未定义的属性: {}", attrName);
                continue;
            }

            // 验证属性值
            try {
                attribute.validateValue(attrValue);
            } catch (BusinessException e) {
                throw new BusinessException(ResultCode.ATTRIBUTE_VALUE_INVALID, 
                        String.format("属性 %s 的值不合法: %s", attrName, e.getMessage()));
            }
        }

        log.info("SKU 规格验证通过: categoryId={}", categoryId);
    }

    @Override
    public Attribute getAttributeById(Long attributeId) {
        log.info("查询属性详情: attributeId={}", attributeId);

        return attributeRepository.findById(attributeId)
                .orElseThrow(() -> new BusinessException(ResultCode.ATTRIBUTE_NOT_FOUND));
    }

    @Override
    public Attribute getAttributeByName(String name) {
        log.info("根据名称查询属性: name={}", name);

        return attributeRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ResultCode.ATTRIBUTE_NOT_FOUND));
    }
}
